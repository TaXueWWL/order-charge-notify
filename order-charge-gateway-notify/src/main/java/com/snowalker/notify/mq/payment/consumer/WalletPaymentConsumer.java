package com.snowalker.notify.mq.payment.consumer;

import com.snowalker.notify.common.service.WalletService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/12 9:46
 * @className WalletPaymentConsumer
 * @desc 扣款消息消费者
 */
@Component
public class WalletPaymentConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalletPaymentConsumer.class);

    @Value("${rocketmq.nameServer}")
    String nameSrvAddr;

    @Autowired
    WalletService walletService;

    @Resource(name = "walletPaymentMsgListenerImpl")
    private MessageListenerConcurrently messageListener;

    private DefaultMQPushConsumer defaultMQPushConsumer;

    @PostConstruct
    public void init() {
        defaultMQPushConsumer = new DefaultMQPushConsumer(MessageProtocolConst.WALLET_PAYMENT_TOPIC.getConsumerGroup());
        defaultMQPushConsumer.setNamesrvAddr(nameSrvAddr);
        // 从头开始消费
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        // 消费模式:集群模式
        defaultMQPushConsumer.setMessageModel(MessageModel.CLUSTERING);
        // 注册监听器
        defaultMQPushConsumer.registerMessageListener(messageListener);
        // 订阅所有消息
        try {
            defaultMQPushConsumer.subscribe(MessageProtocolConst.WALLET_PAYMENT_TOPIC.getTopic(), "*");
            defaultMQPushConsumer.start();
        } catch (MQClientException e) {
            LOGGER.error("[扣款消息消费者]--WalletPaymentConsumer加载异常!e={}", LogExceptionWapper.getStackTrace(e));
            throw new RuntimeException("[扣款消息消费者]--WalletPaymentConsumer加载异常!", e);
        }
        LOGGER.info("[扣款消息消费者]--WalletPaymentConsumer加载完成!");
    }
}
