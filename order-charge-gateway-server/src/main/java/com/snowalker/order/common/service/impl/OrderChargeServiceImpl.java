package com.snowalker.order.common.service.impl;

import com.alibaba.fastjson.JSON;
import com.snowalker.order.charge.message.constant.MessageProtocolConst;
import com.snowalker.order.charge.message.protocol.WalletPaymentProtocol;
import com.snowalker.order.charge.request.ChargeOrderRequest;
import com.snowalker.order.common.config.MerchantInfoConfig;
import com.snowalker.order.common.config.ProductConfig;
import com.snowalker.order.common.config.ProductVO;
import com.snowalker.order.common.dao.OrderChargeMapper;
import com.snowalker.order.common.dao.dataobject.MerchantInfoDO;
import com.snowalker.order.common.dao.dataobject.OrderInfoDO;
import com.snowalker.order.common.dao.dataobject.OrderInfoDobj;
import com.snowalker.order.common.dto.Result;
import com.snowalker.order.common.service.OrderChargeService;
import com.snowalker.order.common.util.LogExceptionWapper;
import com.snowalker.order.mq.payment.producer.ChargeOrderPaymentTranProducer;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/11 15:12
 * @className OrderChargeServiceImpl
 * @desc 订单本地service实现
 */
@Service(value = "orderChargeService")
public class OrderChargeServiceImpl implements OrderChargeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderChargeServiceImpl.class);


    @Autowired
    ChargeOrderPaymentTranProducer chargeOrderPaymentTranProducer;

    @Autowired
    OrderChargeMapper orderChargeMapper;

    @Autowired
    OrderChargeService orderChargeService;
    /**
     * 下单前置合法性校验
     * @param chargeOrderRequest
     * @param sessionId
     * @return
     */
    @Override
    public boolean checkValidBeforeChargeOrder(ChargeOrderRequest chargeOrderRequest, String sessionId) {

        // 入参校验
        if (chargeOrderRequest == null) {
            LOGGER.info("sessionId={},下单请求参数chargeOrderRequest为空,返回下单失败", sessionId);
            return false;
        }
        LOGGER.info("sessionId={},下单开始,下单请求参数chargeOrderRequest=[{}].", sessionId, JSON.toJSONString(chargeOrderRequest));
        String purseId = chargeOrderRequest.getPurseId();
        String prodId = chargeOrderRequest.getProdId();
        if (StringUtils.isBlank(purseId) || StringUtils.isBlank(prodId)) {
            LOGGER.info("sessionId={},下单必要参数为空,返回下单失败", sessionId);
            return false;
        }
        //  商户校验
        MerchantInfoDO merchantInfoDO = MerchantInfoConfig.getMerchantsMap().get(purseId);
        if (merchantInfoDO == null) {
            LOGGER.info("sessionId={},purseId对应的商户信息不存在,返回下单失败", sessionId);
            return false;
        }
        // 商品校验
        ProductVO productVO = ProductConfig.getProductsMap().get(prodId);
        if (productVO == null) {
            LOGGER.info("sessionId={},prodId={},对应的商品信息不存在,返回下单失败", sessionId, prodId);
            return false;
        }
        // 商品库存校验
        Integer productStock = productVO.getProductStock();
        if (productStock <= 0) {
            LOGGER.info("sessionId={},prodId={},productStock={},对应的商品库存不足,返回下单失败", sessionId, prodId, productStock);
            return false;
        }
        // 参数验签
        String paramSign = chargeOrderRequest.sign(merchantInfoDO.getMerchantPrivateKey());
        String interfaceSign = chargeOrderRequest.getSign();
        if (!paramSign.equals(interfaceSign)) {
            LOGGER.info("sessionId={},purseId={},当前商户下单接口请求参数签名校验失败.正确的签名=[{}],请求参数中签名=[{}]",
                    sessionId, prodId, paramSign, interfaceSign);
            return false;
        }
        return true;
    }

    /**
     * TODO 需学员实现
     * 下单并发送扣款事务消息
     * @param chargeOrderRequest
     * @param sessionId
     * @return
     */
    @Override
    public Result sendPaymentTransactionMsg(ChargeOrderRequest chargeOrderRequest, String sessionId) {
        // 本地下单，事务消息：扣款
        String purseId = chargeOrderRequest.getPurseId();
        String channelOrderId = chargeOrderRequest.getChannelOrderId();
        String merchantName = chargeOrderRequest.getMerchantName();
        String chargeMoney = chargeOrderRequest.getChargePrice();
        String userPhoneNo = chargeOrderRequest.getUserPhoneNum();
        // 消息协议组装 消息发送
        WalletPaymentProtocol walletPaymentProtocol = new WalletPaymentProtocol();
        walletPaymentProtocol.setPurseId(purseId)
                .setChannelOrderId(channelOrderId)
                .setMerchantName(merchantName)
                .setChargeMoney(chargeMoney)
                .setOrderId(UUID.randomUUID().toString())
                .setUserPhoneNo(userPhoneNo);
        String msgBody = walletPaymentProtocol.encode();
        try {
            // 发送扣款事务消息
            Message message = new Message(MessageProtocolConst.WALLET_PAYMENT_TOPIC.getTopic(), msgBody.getBytes());
            SendResult sendResult = chargeOrderPaymentTranProducer.getProducer().sendMessageInTransaction(message, null);
            if (sendResult == null) {
                LOGGER.error("sessionId={},扣款事务消息投递失败,msgBody={},sendResult=null", sessionId, msgBody);
                return new Result("20000", "FAIL", null);
            }
            // 订单已入库,状态修改为处理中
            OrderInfoDO orderInfoDO = new OrderInfoDO().setChannelOrderId(channelOrderId).setOrderId(walletPaymentProtocol.getOrderId());
            orderChargeService.updateOrderDealing(orderInfoDO);
            // 订单接收成功
            LOGGER.info("sessionId={},收单成功,扣款事务消息投递成功.msgBody={},sendResult={}", sessionId, msgBody, JSON.toJSONString(sendResult));
            return new Result("10000", "SUCCESS", chargeOrderRequest);
        } catch (MQClientException e) {
            // 消息发送异常，按下单失败处理
            LOGGER.error("sessionId={}, 扣款事务消息发送异常,协议体={},e={}", sessionId, msgBody, LogExceptionWapper.getStackTrace(e));
        }
        return new Result("20000", "FAIL", null);
    }

    /**
     * 订单入库
     * @param orderInfoDO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insertOrder(OrderInfoDO orderInfoDO) {
        int insertCount = 0;
        String orderId = orderInfoDO.getOrderId();
        try {
            insertCount = orderChargeMapper.insertOrder(orderInfoDO);
        } catch (Exception e) {
            LOGGER.error("orderId={},初始化订单入库[异常],事务回滚,e={}", orderId, LogExceptionWapper.getStackTrace(e));
            String message =
                    String.format("[insertOrder]orderId=%s,订单入库[异常],事务回滚", orderId);
            throw new RuntimeException(message);
        }
        if (insertCount != 1) {
            LOGGER.error("orderId={},初始化订单入库[失败],事务回滚,e={}", orderId);
            String message =
                    String.format("[insertOrder]orderId=%s,订单入库[失败],事务回滚", orderId);
            throw new RuntimeException(message);
        }
        return true;
    }

    /**
     * 订单查询
     * @param orderInfoDO
     * @return
     */
    @Override
    public OrderInfoDobj queryOrderInfo(OrderInfoDO orderInfoDO) {
        return orderChargeMapper.queryOrderInfoByOrderId(orderInfoDO);
    }

    /**
     * 订单状态修改为处理中
     * @param orderInfoDO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateOrderDealing(OrderInfoDO orderInfoDO) {
        int updateCount = 0;
        String orderId = orderInfoDO.getOrderId();
        try {
            updateCount = orderChargeMapper.updateOrderDealing(orderInfoDO);
        } catch (Exception e) {
            LOGGER.error("[updateOrderDealing]orderId={},订单状态修改为处理中[异常],事务回滚,e={}", orderId, LogExceptionWapper.getStackTrace(e));
            String message =
                    String.format("[updateOrderDealing]orderId=%s,订单状态修改为处理中[异常],事务回滚", orderId);
            throw new RuntimeException(message);
        }
        if (updateCount != 1) {
            LOGGER.error("[updateOrderDealing]orderId={},订单状态修改为处理中[失败],事务回滚,e={}", orderId);
            String message =
                    String.format("[updateOrderDealing]orderId=%s,订单状态修改为处理中[失败],事务回滚", orderId);
            throw new RuntimeException(message);
        }
        return true;
    }

    /**
     * 支付状态修改为成功
     * @param orderInfoDO
     * @return
     */
    @Override
    public boolean updateOrderPayStatusSucc(OrderInfoDO orderInfoDO) {
        int updateCount = 0;
        String orderId = orderInfoDO.getOrderId();
        try {
            updateCount = orderChargeMapper.updateOrderPayStatusSucc(orderInfoDO);
        } catch (Exception e) {
            LOGGER.error("[updateOrderPayStatusSucc]orderId={},支付状态修改为SUCC[异常],事务回滚,e={}", orderId, LogExceptionWapper.getStackTrace(e));
            String message =
                    String.format("[updateOrderPayStatusSucc]orderId=%s,支付状态修改为SUCC[异常],事务回滚", orderId);
            throw new RuntimeException(message);
        }
        if (updateCount != 1) {
            LOGGER.error("[updateOrderPayStatusSucc]orderId={},支付状态修改为SUCC[失败],事务回滚,e={}", orderId);
            String message =
                    String.format("[updateOrderPayStatusSucc]orderId=%s,支付状态修改为SUCC[失败],事务回滚", orderId);
            throw new RuntimeException(message);
        }
        return true;
    }

    /**
     * 通知状态处理中改成功
     * @param orderInfoDO
     * @return
     */
    @Override
    public boolean updateOrderNotifyStatusSucc(OrderInfoDO orderInfoDO) {
        int updateCount = 0;
        String orderId = orderInfoDO.getOrderId();
        try {
            updateCount = orderChargeMapper.updateOrderNotifyStatusSucc(orderInfoDO);
        } catch (Exception e) {
            LOGGER.error("[updateOrderNotifyStatusSucc]orderId={},通知状态处理中改SUCC[异常],事务回滚,e={}", orderId, LogExceptionWapper.getStackTrace(e));
            String message =
                    String.format("[updateOrderNotifyStatusSucc]orderId=%s,通知状态处理中改SUCC[异常],事务回滚", orderId);
            throw new RuntimeException(message);
        }
        if (updateCount != 1) {
            LOGGER.error("[updateOrderNotifyStatusSucc]orderId={},通知状态处理中改SUCC[失败],事务回滚,e={}", orderId);
            String message =
                    String.format("[updateOrderNotifyStatusSucc]orderId=%s,通知状态处理中改SUCC[失败],事务回滚", orderId);
            throw new RuntimeException(message);
        }
        return true;
    }

}
