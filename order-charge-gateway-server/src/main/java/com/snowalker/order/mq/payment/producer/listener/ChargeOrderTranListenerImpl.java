package com.snowalker.order.mq.payment.producer.listener;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/11 15:51
 * @className ChargeOrderTranListenerImpl
 * @desc 订单交易事务监听器回调实现
 */
import com.snowalker.order.charge.message.protocol.WalletPaymentProtocol;
import com.snowalker.order.common.dao.dataobject.OrderInfoDO;
import com.snowalker.order.common.dao.dataobject.OrderInfoDobj;
import com.snowalker.order.common.service.OrderChargeService;
import com.snowalker.order.common.util.LogExceptionWapper;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 下单事务消息本地回调实现
 * 主要实现：本地订单入库、订单事务回查
 * TODO 需要学员实现
 * @author snowalker
 * @date 2019/6/11 16:09
 */
@Component(value = "chargeOrderTranListenerImpl")
public class ChargeOrderTranListenerImpl implements TransactionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChargeOrderTranListenerImpl.class);

    @Resource(name = "orderChargeService")
    OrderChargeService orderChargeService;

    /**
     * 执行本地订单入库操作
     * @param msg
     * @param arg
     * @return
     */
    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        // 消息解码
        String message = new String(msg.getBody());
        WalletPaymentProtocol walletPaymentProtocol = new WalletPaymentProtocol();
        walletPaymentProtocol.decode(message);
        LOGGER.info("订单入库实体WalletPaymentProtocol={}", walletPaymentProtocol.toString());
        // 组装下单实体
        OrderInfoDO orderInfoDO = new OrderInfoDO();
        BeanUtils.copyProperties(walletPaymentProtocol, orderInfoDO);
        String orderId = orderInfoDO.getOrderId();
        // 执行下单操作
        try {
            if (!orderChargeService.insertOrder(orderInfoDO)) {
                LOGGER.error("订单入库失败,事务消息回滚,LocalTransactionState={},orderId={}", LocalTransactionState.ROLLBACK_MESSAGE, orderId);
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }
        } catch (Exception e) {
            LOGGER.error("订单入库异常,等待回查发起,orderId={},e={}", orderId, LogExceptionWapper.getStackTrace(e));
            return LocalTransactionState.UNKNOW;
        }
        LOGGER.info("订单入库成功,orderId={}", orderId);
        return LocalTransactionState.COMMIT_MESSAGE;
    }

    /**
     * 根据订单号进行回查
     * @param msg
     * @return
     */
    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        String message = new String(msg.getBody());
        String msgId = msg.getMsgId();
        int reconsumeTimes = msg.getReconsumeTimes();
        LOGGER.info("订单入库本地事务回查--接收到消息, msgId={},message={},reconsumeTimes={}", msgId, message, reconsumeTimes);
        // 消息解码
        WalletPaymentProtocol walletPaymentProtocol = new WalletPaymentProtocol();
        walletPaymentProtocol.decode(message);
        String orderId = walletPaymentProtocol.getOrderId();
        // 订单查询
        OrderInfoDO orderInfoDO = new OrderInfoDO().setOrderId(orderId);
        OrderInfoDobj orderInfoDobj = orderChargeService.queryOrderInfo(orderInfoDO);
        if (orderInfoDobj == null) {
            LOGGER.info("订单入库本地事务回查--本地不存在订单,[消息回滚],orderId={},msgId={}", orderId, msgId);
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
        LOGGER.info("订单入库本地事务回查--本地存在订单信息,orderInfoDobj={},msgId={},[消息提交]", orderInfoDobj, msgId);
        return LocalTransactionState.COMMIT_MESSAGE;
    }
}
