package com.snowalker.order.common.service;

import com.snowalker.order.charge.request.ChargeOrderRequest;
import com.snowalker.order.common.dao.dataobject.OrderInfoDO;
import com.snowalker.order.common.dao.dataobject.OrderInfoDobj;
import com.snowalker.order.common.dto.Result;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/11 15:12
 * @className OrderChargeService
 * @desc
 */
public interface OrderChargeService {

    /**
     * 下单前置校验
     * @param chargeOrderRequest
     * @param sessionId
     * @return
     */
    boolean checkValidBeforeChargeOrder(ChargeOrderRequest chargeOrderRequest, String sessionId);

    /**
     * 下单并发送扣款事务消息
     * @param chargeOrderRequest
     * @param sessionId
     * @return
     */
    Result sendPaymentTransactionMsg(ChargeOrderRequest chargeOrderRequest, String sessionId);

    /**
     * 订单入库
     * @param orderInfoDO
     * @return
     */
    boolean insertOrder(OrderInfoDO orderInfoDO);

    /**
     * 订单查询
     * @param orderInfoDO
     * @return
     */
    OrderInfoDobj queryOrderInfo(OrderInfoDO orderInfoDO);

    /**
     * 订单状态修改为处理中
     * @param orderInfoDO
     * @return
     */
    boolean updateOrderDealing(OrderInfoDO orderInfoDO);

    /**
     * 支付状态修改为成功
     * @param orderInfoDO
     * @return
     */
    boolean updateOrderPayStatusSucc(OrderInfoDO orderInfoDO);

    /**
     * 订单通知状态修改成功
     * @param orderInfoDO
     * @return
     */
    boolean updateOrderNotifyStatusSucc(OrderInfoDO orderInfoDO);
}
