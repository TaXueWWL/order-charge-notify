package com.snowalker.gateway.merchant.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.snowalker.gateway.merchant.goods.ProductConfig;
import com.snowalker.gateway.merchant.goods.ProductVO;
import com.snowalker.gateway.merchant.order.constant.NotifyConstant;
import com.snowalker.gateway.merchant.order.constant.OrderStatusEnum;
import com.snowalker.gateway.merchant.order.dataobject.ChargeOrderDO;
import com.snowalker.gateway.merchant.order.dto.ChargeOrderDto;
import com.snowalker.gateway.merchant.order.dto.CodeMsg;
import com.snowalker.gateway.merchant.order.dto.Result;
import com.snowalker.gateway.merchant.order.manager.OrderChargeManager;
import com.snowalker.gateway.merchant.order.mapper.OrderInfoMapper;
import com.snowalker.gateway.merchant.order.service.OrderService;
import com.snowalker.gateway.merchant.order.util.DateUtil;
import com.snowalker.gateway.merchant.order.util.LogExceptionWapper;
import com.snowalker.order.charge.constant.ResponseCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;


/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/10 11:49
 * @className OrderServiceImpl
 * @desc
 */
@Service(value = "orderService")
public class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    OrderService orderService;

    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Autowired
    OrderChargeManager orderChargeManager;

    @Override
    public Result chargeOrder(String phoneNum, String chargeMoney, String prodId) {

        // 减库存
        ProductVO productVO = ProductConfig.getProductsMap().get(prodId);
        synchronized (this) {
            int productStock = productVO.getProductStock();
            int afterSubStock = productStock - 1;
            if (afterSubStock < 0) {
                LOGGER.error("库存已不足扣减,下单失败。prodId={}, productStock={}", prodId, productStock);
                return Result.error(new CodeMsg(CodeMsg.PRODUCT_STOCK_NOT_ENOUGH, "库存不足!"));
            }
            // 刷新产品信息
            productVO.setProductStock(afterSubStock);
            LOGGER.error("库存足够扣减,开始下单。prodId={}, productStock={}", prodId, productVO.getProductStock());
            ProductConfig.getProductsMap().put(prodId, productVO);
        }
        // 订单入库
        ChargeOrderDto chargeOrderDto = new ChargeOrderDto();
        chargeOrderDto.setOrderId(UUID.randomUUID().toString())
                .setOrderStatus(OrderStatusEnum.INIT.getCode())
                .setPhoneNum(phoneNum)
                .setProductId(productVO.getProductId())
                .setProductName(productVO.getProductName())
                .setOutProductId(productVO.getProductOutId())
                .setOutProductName(productVO.getProductOutName())
                .setOutProductId(productVO.getProductOutId())
                .setChargeMoney(new BigDecimal(chargeMoney).setScale(3));
        String chargeOrderId = chargeOrderDto.getOrderId();
        LOGGER.info("订单入库开始,入参chargeOrderDto={}", chargeOrderDto.toString());
        if (!orderService.insertOrderInfo(chargeOrderDto)) {
            LOGGER.error("订单入库失败,下单失败,orderId={}", chargeOrderId);
            return Result.error(CodeMsg.INSERT_ORDER_ERROR);
        }
        // 下单前改处理中
        chargeOrderDto.setBeforeUpdateOrderStatus(OrderStatusEnum.INIT.getCode())
                .setAfterUpdateOrderStatus(OrderStatusEnum.DEALING.getCode());
        if (!orderService.updateOrderStatus(chargeOrderDto)) {
            LOGGER.error("订单状态由初始化更新到处理中失败,orderId={}", chargeOrderId);
            return Result.error(CodeMsg.UPDATE_ORDER_ERROR);
        }
        ResponseCodeEnum responseCodeEnum = orderChargeManager.chargeRequest(chargeOrderDto);
        // 改状态
        if (responseCodeEnum == ResponseCodeEnum.FAIL) {
            // 下单失败，订单状态==失败
            LOGGER.info("远程下单返回同步失败,更新订单状态为失败,orderId={}", chargeOrderId);
            chargeOrderDto.setBeforeUpdateOrderStatus(OrderStatusEnum.DEALING.getCode())
                    .setAfterUpdateOrderStatus(OrderStatusEnum.FAIL.getCode());
            orderService.updateOrderStatus(chargeOrderDto);
            return Result.error(CodeMsg.CHARGE_ORDER_FAIL);
        }
        if (responseCodeEnum == ResponseCodeEnum.SUCCESS) {
            // 下单成功
            LOGGER.info("远程下单返回收单成功,等待充值结果通知,orderId={}", chargeOrderId);
            ChargeOrderDto returnOrderDto = new ChargeOrderDto();
            returnOrderDto.setOrderId(chargeOrderDto.getOrderId())
                    .setPhoneNum(chargeOrderDto.getPhoneNum())
                    .setProductId(chargeOrderDto.getProductId())
                    .setProductName(chargeOrderDto.getProductName())
                    .setChargeMoney(chargeOrderDto.getChargeMoney());
            return new Result(CodeMsg.SUCCESS.getCode(), CodeMsg.SUCCESS.getMsg(), JSON.toJSONString(returnOrderDto));
        }
        // 其余情况返回未知，需要配合主动查询（暂不实现）
        LOGGER.info("远程下单状态未知,订单状态仍旧为处理中,orderId={}", chargeOrderId);
        return Result.error(CodeMsg.UNKNOWN_ERROR);
    }

    /**
     * 通知业务逻辑
     * @param sessionId
     * @param orderStatus
     * @param channelOrderId
     * @param platOrderId
     * @param finishTime
     * @return
     */
    @Override
    public String doNotify(String sessionId,
                           String orderStatus,
                           String channelOrderId,
                           String platOrderId,
                           String finishTime) {
        // 订单状态查询,通知幂等
        ChargeOrderDO chargeOrderDO = null;
        try {
            chargeOrderDO = orderService.queryOrderByOrderId(channelOrderId);
        } catch (SQLException e) {
            LOGGER.info("sessionId={}, orderId={}, 订单信息查询异常,稍后继续通知.e={}", sessionId, channelOrderId, LogExceptionWapper.getStackTrace(e));
            return NotifyConstant.NOTIFY_RETURN_FAIL;
        }
        if (chargeOrderDO == null) {
            LOGGER.info("sessionId={}, orderId={}, 平台不存在该订单,不进行处理。", sessionId, channelOrderId);
            return NotifyConstant.NOTIFY_RETURN_FAIL;
        }
        LOGGER.info("sessionId={}, orderId={}, 订单详情={}", sessionId, channelOrderId, chargeOrderDO.toString());
        // 订单幂等判断：订单已经是成功/失败
        Integer currOrderStatus = chargeOrderDO.getOrderStatus();
        if (OrderStatusEnum.SUCCESS.getCode().equals(currOrderStatus) ||
                OrderStatusEnum.FAIL.getCode().equals(currOrderStatus)) {
            LOGGER.info("sessionId={}, orderId={}, 当前订单已经处于终止状态={},不进行处理。", sessionId, channelOrderId, currOrderStatus);
            return NotifyConstant.NOTIFY_RETURN_SUCC;
        }

        // 根据状态更新订单状态
        ChargeOrderDto chargeOrderDto = new ChargeOrderDto();
        if (NotifyConstant.NOTIFY_SUCCESS.equals(orderStatus)) {
            chargeOrderDto.setOrderId(channelOrderId)
                    .setOutOrderId(platOrderId)
                    .setBeforeUpdateOrderStatus(OrderStatusEnum.DEALING.getCode())
                    .setAfterUpdateOrderStatus(OrderStatusEnum.SUCCESS.getCode())
                    .setFinishTime(DateUtil.parseDateFromStr(finishTime));
            LOGGER.info("sessionId={},更新订单状态处理中到成功,更新入参chargeOrderDto={}", sessionId, JSON.toJSONString(chargeOrderDto));
            orderService.updateOrderStatus(chargeOrderDto);
            return NotifyConstant.NOTIFY_RETURN_SUCC;
        }
        if (NotifyConstant.NOTIFY_FAIL.equals(orderStatus)) {
            chargeOrderDto.setOrderId(channelOrderId)
                    .setOutOrderId(platOrderId)
                    .setBeforeUpdateOrderStatus(OrderStatusEnum.DEALING.getCode())
                    .setAfterUpdateOrderStatus(OrderStatusEnum.FAIL.getCode())
                    .setFinishTime(DateUtil.parseDateFromStr(finishTime));
            LOGGER.info("sessionId={},更新订单状态处理中到失败,更新入参chargeOrderDto={}", sessionId, JSON.toJSONString(chargeOrderDto));
            orderService.updateOrderStatus(chargeOrderDto);
            return NotifyConstant.NOTIFY_RETURN_SUCC;
        }
        LOGGER.info("sessionId={},其余情况按照未知处理,返回F,要求上游充值平台进行重试.", sessionId);
        return NotifyConstant.NOTIFY_RETURN_FAIL;
    }

    /**
     * 订单入库
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertOrderInfo(ChargeOrderDto chargeOrderDto) {
        int count = 0;
        String orderId = chargeOrderDto.getOrderId();
        try {
            count = orderInfoMapper.insertOrderInfo(chargeOrderDto);
        } catch (Exception e) {
            LOGGER.error("orderId={},初始化订单入库[异常],事务回滚,e={}", orderId, LogExceptionWapper.getStackTrace(e));
            String message =
                    String.format("[insertOrderInfo]orderId=%s,初始化订单入库[异常],事务回滚", orderId);
            throw new RuntimeException(message);
        }
        if (count != 1) {
            LOGGER.error("orderId={},初始化订单入库[失败],事务回滚,e={}", orderId);
            String message =
                    String.format("[insertOrderInfo]orderId=%s,初始化订单入库[失败],事务回滚", orderId);
            throw new RuntimeException(message);
        }
        return true;
    }

    /**
     * 订单状态更新
     * @param chargeOrderDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateOrderStatus(ChargeOrderDto chargeOrderDto) {
        int orderUpdateCount = 0;
        String orderId = chargeOrderDto.getOrderId();
        try {
            orderUpdateCount = orderInfoMapper.updateOrderStatus(chargeOrderDto);
        } catch (Exception e) {
            LOGGER.error("orderId={},更新订单状态[异常],事务回滚,e={}", orderId, LogExceptionWapper.getStackTrace(e));
            String message =
                    String.format("[updateOrderStatus]orderId=%s,更新订单状态[异常],事务回滚", orderId);
            throw new RuntimeException(message);
        }
        if (orderUpdateCount != 1) {
            LOGGER.warn("orderId={},更新订单状态[操作失败],事务回滚", orderId);
            String message =
                    String.format("[updateOrderStatus]orderId=%s,更新订单状态[异常],事务回滚", orderId);
            throw new RuntimeException(message);
        }
        return true;
    }

    /**
     * 订单详情查询
     * @param channelOrderId
     * @return
     */
    @Override
    public ChargeOrderDO queryOrderByOrderId(String channelOrderId) throws SQLException {
        ChargeOrderDto chargeOrderDto = new ChargeOrderDto();
        chargeOrderDto.setOrderId(channelOrderId);
        return orderInfoMapper.queryOrderByOrderId(chargeOrderDto);
    }
}
