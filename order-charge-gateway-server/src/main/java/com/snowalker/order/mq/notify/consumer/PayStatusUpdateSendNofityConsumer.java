package com.snowalker.order.mq.notify.consumer;

import com.snowalker.order.charge.message.constant.MessageProtocolConst;
import com.snowalker.order.charge.message.constant.UpdateEventTypeConst;
import com.snowalker.order.charge.message.protocol.OrderResultNofityProtocol;
import com.snowalker.order.charge.message.protocol.OrderStatusUpdateProtocol;
import com.snowalker.order.common.config.MerchantInfoConfig;
import com.snowalker.order.common.dao.dataobject.MerchantInfoDO;
import com.snowalker.order.common.dao.dataobject.OrderInfoDO;
import com.snowalker.order.common.dao.dataobject.OrderInfoDobj;
import com.snowalker.order.common.service.OrderChargeService;
import com.snowalker.order.common.util.LogExceptionWapper;
import com.snowalker.order.mq.notify.producer.OrderNotifySendProducer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/12 10:39
 * @className UpdatePayStatusSendNofityConsumer
 * @desc 修改订单支付状态，发送订单结果通知消费者
 * TODO 需要学员实现
 */
@Component
public class PayStatusUpdateSendNofityConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PayStatusUpdateSendNofityConsumer.class);

    @Value("${rocketmq.nameServer}")
    String nameSrvAddr;

    @Autowired
    OrderChargeService orderChargeService;

    private DefaultMQPushConsumer defaultMQPushConsumer;

    @Autowired
    OrderNotifySendProducer orderNotifySendProducer;

    @PostConstruct
    public void init() {
        defaultMQPushConsumer = new DefaultMQPushConsumer(MessageProtocolConst.ORDER_STATUS_UPDATE_TOPIC.getConsumerGroup());
        defaultMQPushConsumer.setNamesrvAddr(nameSrvAddr);
        // 从头开始消费
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        // 消费模式:集群模式
        defaultMQPushConsumer.setMessageModel(MessageModel.CLUSTERING);
        // 注册监听器
        defaultMQPushConsumer.registerMessageListener(new MessageListenerConcurrently() {

            /**
             * 订单状态修改消费逻辑
             * @param msgs
             * @param context
             * @return
             */
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                try {
                    // 默认msgs只有一条消息
                    for (MessageExt msg : msgs) {
                        String message = new String(msg.getBody());
                        LOGGER.info("[订单状态修改消费者]-UpdatePayStatusSendNofityConsumer-接收到消息,message={}", message);
                        // 消费次数
                        int reconsumeTimes = msg.getReconsumeTimes();
                        String msgId = msg.getMsgId();

                        OrderStatusUpdateProtocol protocol = new OrderStatusUpdateProtocol();
                        protocol.decode(message);

                        String orderId = protocol.getOrderId();
                        String logSuffix = "orderId=" + orderId + ",msgId=" + msgId + ",reconsumeTimes=" + reconsumeTimes;
                        // 获取事件
                        String updateEventType = protocol.getEventType();
                        String purseId = protocol.getPurseId();

                        /**
                         * 扣款完成后，消费订单状态修改消息
                         * 事件=EVENT_UPDATE_PAY_STATUS
                         * 修改订单状态与支付状态成功，通知状态为处理中
                         */
                        if (UpdateEventTypeConst.EVENT_UPDATE_PAY_STATUS.getEventType().equals(updateEventType)) {
                            // 支付状态更新事件
                            OrderInfoDO orderInfoDO = new OrderInfoDO();
                            orderInfoDO.setOrderId(protocol.getOrderId());
                            boolean updateResult = orderChargeService.updateOrderPayStatusSucc(orderInfoDO);
                            if (!updateResult) {
                                LOGGER.info("[订单状态修改消费者-EVENT_UPDATE_PAY_STATUS]-UpdatePayStatusSendNofityConsumer-修改支付状态、订单状态为成功消费失败,等待重新消费,message={},{}", message, logSuffix);
                                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                            }
                            LOGGER.info("[订单状态修改消费者-EVENT_UPDATE_PAY_STATUS]-UpdatePayStatusSendNofityConsumer-修改支付状态、订单状态为成功消费成功,message={},{}", message, logSuffix);
                            // 发送订单结果通知消息
                            sendOrderNotifyMsg(orderId, logSuffix, purseId);
                            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                        }

                        /**
                         * 通知服务发送成功通知到渠道后，
                         * 发布通知状态改成功消息,事件为=EVENT_UPDATE_NOTIFY_OD_STATUS，
                         * 消费消息并更改订单的通知状态
                         */
                        if (UpdateEventTypeConst.EVENT_UPDATE_NOTIFY_OD_STATUS.getEventType().equals(updateEventType)) {
                            // 订单状态及通知状态更新事件
                            OrderInfoDO orderInfoDO = new OrderInfoDO();
                            orderInfoDO.setOrderId(protocol.getOrderId());
                            boolean updateResult = orderChargeService.updateOrderNotifyStatusSucc(orderInfoDO);
                            if (!updateResult) {
                                LOGGER.info("[订单状态修改消费者-EVENT_UPDATE_NOTIFY_OD_STATUS]-UpdatePayStatusSendNofityConsumer-[修改订单通知状态为成功]消费失败,等待重新消费,message={},{}", message, logSuffix);
                                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                            }
                            LOGGER.info("[订单状态修改消费者-EVENT_UPDATE_NOTIFY_OD_STATUS]-UpdatePayStatusSendNofityConsumer-[修改订单通知状态为成功]消息消费成功,message={},{}", message, logSuffix);
                            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                        }
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                } catch (Exception e) {
                    LOGGER.error("[订单状态修改消费者]消费异常,e={}", LogExceptionWapper.getStackTrace(e));
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
        });
        // 订阅所有消息
        try {
            defaultMQPushConsumer.subscribe(MessageProtocolConst.ORDER_STATUS_UPDATE_TOPIC.getTopic(), "*");
            defaultMQPushConsumer.start();
        } catch (MQClientException e) {
            LOGGER.error("[扣款消息消费者]--WalletPaymentConsumer加载异常!e={}", LogExceptionWapper.getStackTrace(e));
            throw new RuntimeException("[扣款消息消费者]--WalletPaymentConsumer加载异常!", e);
        }
        LOGGER.info("[扣款消息消费者]--WalletPaymentConsumer加载完成!");
    }

    /**
     * 发送订单结果通知消息
     * @param orderId
     * @param logSuffix
     */
    private void sendOrderNotifyMsg(String orderId, String logSuffix, String purseId) throws Exception {
        // 1. 查询订单信息
        OrderInfoDO orderInfoDO = new OrderInfoDO().setOrderId(orderId);
        OrderInfoDobj orderInfoDobj = orderChargeService.queryOrderInfo(orderInfoDO);
        // 2. 组装消息协议
        OrderResultNofityProtocol orderResultNofityProtocol = new OrderResultNofityProtocol();
        BeanUtils.copyProperties(orderInfoDobj, orderResultNofityProtocol);
        // 2.1 订单结果通知设置privateKey及通知地址
        MerchantInfoDO merchantInfoDO = MerchantInfoConfig.getMerchantsMap().get(purseId);
        if (merchantInfoDO == null) {
            LOGGER.info("[订单状态修改消费者]供应商配置信息不存在!purseId={},{}", purseId, logSuffix);
            return;
        }
        orderResultNofityProtocol.setPrivateKey(merchantInfoDO.getMerchantPrivateKey());
        orderResultNofityProtocol.setMerchantNotifyUrl(merchantInfoDO.getMerchantNotifyUrl());

        String msgBody = orderResultNofityProtocol.encode();
        Message message = new Message(MessageProtocolConst.ORDER_RESULT_NOTIFY_TOPIC.getTopic(), msgBody.getBytes());
        // 3. 发送消息
        SendResult sendResult = orderNotifySendProducer.getProducer().send(message);
        if (sendResult == null) {
            LOGGER.error("[订单状态修改消费者]发送订单结果通知消息返回为空,{}", logSuffix);
        }
        LOGGER.info("[订单状态修改消费者]发送订单结果通知消息发送成功.{}", logSuffix);
    }

}
