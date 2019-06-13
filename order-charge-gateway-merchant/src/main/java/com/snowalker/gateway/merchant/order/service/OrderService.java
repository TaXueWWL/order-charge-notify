package com.snowalker.gateway.merchant.order.service;

import com.snowalker.gateway.merchant.order.dataobject.ChargeOrderDO;
import com.snowalker.gateway.merchant.order.dto.ChargeOrderDto;
import com.snowalker.gateway.merchant.order.dto.Result;

import java.sql.SQLException;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/10 11:48
 * @className OrderService
 * @desc 订单交易
 */
public interface OrderService {

    /**
     * 下单
     * @param phoneNum
     * @param chargeMoney
     * @param prodId
     * @return
     */
    Result chargeOrder(String phoneNum, String chargeMoney, String prodId);

    /**
     * 本地订单入库
     * @param chargeOrderDto
     * @return
     */
    boolean insertOrderInfo(ChargeOrderDto chargeOrderDto);

    /**
     * 订单状态更新
     * @param chargeOrderDto
     * @return
     */
    boolean updateOrderStatus(ChargeOrderDto chargeOrderDto);

    /**
     * 订单详情查询
     * @param channelOrderId
     * @return
     */
    ChargeOrderDO queryOrderByOrderId(String channelOrderId) throws SQLException;

    /**
     * 通知逻辑
     * @param sessionId
     * @param orderStatus
     * @param channelOrderId
     * @param platOrderId
     * @param finishTime
     * @return
     */
    String doNotify(String sessionId, String orderStatus, String channelOrderId, String platOrderId, String finishTime);
}

