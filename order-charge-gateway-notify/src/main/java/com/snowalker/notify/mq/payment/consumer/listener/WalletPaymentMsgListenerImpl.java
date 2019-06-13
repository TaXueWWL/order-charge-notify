package com.snowalker.notify.mq.payment.consumer.listener;

import com.snowalker.notify.common.dao.dataobject.ChargeRecordEntity;
import com.snowalker.notify.common.service.WalletService;
import com.snowalker.notify.common.util.LogExceptionWapper;
import com.snowalker.notify.mq.payment.producer.OrderStatusUpdateProducer;
import com.snowalker.order.charge.message.constant.MessageProtocolConst;
import com.snowalker.order.charge.message.constant.UpdateEventTypeConst;
import com.snowalker.order.charge.message.protocol.OrderStatusUpdateProtocol;
import com.snowalker.order.charge.message.protocol.WalletPaymentProtocol;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/12 9:52
 * @className WalletPaymentMsgListenerImpl
 * @desc 消息消费监听回调实现
 */
@Component(value = "walletPaymentMsgListenerImpl")
public class WalletPaymentMsgListenerImpl implements MessageListenerConcurrently {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalletPaymentMsgListenerImpl.class);

    @Autowired
    OrderStatusUpdateProducer orderStatusUpdateProducer;

    @Autowired
    WalletService walletService;

    /**
     * 钱包扣款关键逻辑
     * @param msgs
     * @param context
     * @return
     */
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        try {
            // 默认msgs只有一条消息
            for (MessageExt msg : msgs) {
                // 消费次数
                int reconsumeTimes = msg.getReconsumeTimes();
                String msgId = msg.getMsgId();
                LOGGER.info("===============msgId={},消费次数={}===============", msgId, reconsumeTimes);
                return walletCharge(msg, msgId);
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            LOGGER.error("钱包扣款消费异常,e={}", e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }

    /**
     * 钱包扣款，并插入扣款流水
     *
     * @param msg
     */
    private ConsumeConcurrentlyStatus walletCharge(MessageExt msg, String msgId) {
        String message = new String(msg.getBody());
        LOGGER.info("msgId={},钱包扣款消费者接收到消息,message={}", msgId, message);
        WalletPaymentProtocol payProtocol = new WalletPaymentProtocol();
        payProtocol.decode(message);

        // 幂等消费逻辑: 根据订单号查询扣款流水，如果存在则直接返回消费成功
        String orderId = payProtocol.getOrderId();
        ChargeRecordEntity chargeRecordEntity = walletService.queryChargeRecordByOrderId(orderId);
        if (chargeRecordEntity != null) {
            LOGGER.info("[扣款本地事务回查]-本地已经存在orderId=[{}]对应的扣款流水,不需要重复消费,msgId={}", orderId, msgId);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }

        try {
            // 组装半消息：扣款成功修改订单状态为成功，消息事件=修改订单支付状态
            OrderStatusUpdateProtocol orderStatusUpdateProtocol = new OrderStatusUpdateProtocol();
            orderStatusUpdateProtocol.setTopicName(MessageProtocolConst.ORDER_STATUS_UPDATE_TOPIC.getTopic());
            orderStatusUpdateProtocol.setOrderId(payProtocol.getOrderId())
                    .setChargeMoney(payProtocol.getChargeMoney())
                    .setPurseId(payProtocol.getPurseId())
                    .setMerchantName(payProtocol.getMerchantName())
                    .setEventType(UpdateEventTypeConst.EVENT_UPDATE_PAY_STATUS.getEventType());

            Message updateOrderStatusMsg =
                    new Message(MessageProtocolConst.ORDER_STATUS_UPDATE_TOPIC.getTopic(),
                            orderStatusUpdateProtocol.encode().getBytes());
            // 半消息发送
            TransactionSendResult transactionSendResult = orderStatusUpdateProducer.getProducer()
                    /**
                     * sendMessageInTransaction(final Message msg,
                     * final Object arg) 第二个参数为回调参数，可以为null，
                     * 该参数和LocalTransactionState executeLocalTransaction(Message msg, Object arg)第二个参数为同一个值
                     */
                    .sendMessageInTransaction(updateOrderStatusMsg, null);
            if (transactionSendResult == null) {
                // 发送未知重新消费
                LOGGER.info("msgId={},订单状态更新半消息发送状态未知,消费状态[RECONSUME_LATER],等待重新消费,orderId={}", msgId, payProtocol.getOrderId(), message);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
            if (transactionSendResult.getLocalTransactionState().equals(LocalTransactionState.COMMIT_MESSAGE)) {
                LOGGER.info("msgId={},订单状态更新半消息发送成功,消费状态[CONSUME_SUCCESS],orderId={},sendResult={}", msgId, payProtocol.getOrderId(), transactionSendResult);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
            if (transactionSendResult.getLocalTransactionState().equals(LocalTransactionState.UNKNOW)) {
                LOGGER.warn("msgId={},订单状态更新本地事务执行状态未知,半消息发送未知,消费状态[RECONSUME_LATER],orderId={},sendResult={}", msgId, payProtocol.getOrderId(), transactionSendResult);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        } catch (Exception e) {
            LOGGER.error("msgId={},订单状态更新半消息发送异常,消费状态[RECONSUME_LATER],e={}", msgId, LogExceptionWapper.getStackTrace(e));
        }
        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
    }
}
