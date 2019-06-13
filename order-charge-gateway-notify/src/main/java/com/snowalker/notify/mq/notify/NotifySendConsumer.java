package com.snowalker.notify.mq.notify;

import com.snowalker.notify.common.util.LogExceptionWapper;
import com.snowalker.notify.mq.payment.producer.OrderStatusUpdateProducer;
import com.snowalker.order.charge.message.constant.MessageProtocolConst;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/12 14:14
 * @className NotifySendConsumer
 * @desc 订单结果通知消息消费者
 * TODO 需要学员实现
 */
@Component
public class NotifySendConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotifySendConsumer.class);

    @Value("${rocketmq.nameServer}")
    String nameSrvAddr;

    private DefaultMQPushConsumer defaultMQPushConsumer;

    @Resource(name = "notifySendListenerImpl")
    private MessageListenerConcurrently messageListener;

    @PostConstruct
    public void init() {
        defaultMQPushConsumer = new DefaultMQPushConsumer(MessageProtocolConst.ORDER_RESULT_NOTIFY_TOPIC.getConsumerGroup());
        defaultMQPushConsumer.setNamesrvAddr(nameSrvAddr);
        // 从头开始消费
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        // 消费模式:集群模式
        defaultMQPushConsumer.setMessageModel(MessageModel.CLUSTERING);
        // 注册监听器
        defaultMQPushConsumer.registerMessageListener(messageListener);
        // 订阅所有消息
        try {
            defaultMQPushConsumer.subscribe(MessageProtocolConst.ORDER_RESULT_NOTIFY_TOPIC.getTopic(), "*");
            defaultMQPushConsumer.start();
        } catch (MQClientException e) {
            LOGGER.error("[订单结果通知消息消费者]--NotifySendConsumer加载异常!e={}", LogExceptionWapper.getStackTrace(e));
            throw new RuntimeException("[订单结果通知消息消费者]--NotifySendConsumer加载异常!", e);
        }
        LOGGER.info("[订单结果通知消息消费者]--NotifySendConsumer加载完成!");
    }
}
