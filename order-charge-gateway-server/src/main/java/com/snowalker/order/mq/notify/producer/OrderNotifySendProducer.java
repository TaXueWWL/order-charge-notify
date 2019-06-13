package com.snowalker.order.mq.notify.producer;

import com.snowalker.order.charge.message.constant.MessageProtocolConst;
import com.snowalker.order.common.util.LogExceptionWapper;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/12 11:35
 * @className SendOrderNotifyProducer
 * @desc 订单通知发送生产者
 */
@Component
public class OrderNotifySendProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderNotifySendProducer.class);

    @Value("${rocketmq.nameServer}")
    String nameSrvAddr;

    private DefaultMQProducer defaultMQProducer;

    @PostConstruct
    public void init() {

        defaultMQProducer =
                new DefaultMQProducer(MessageProtocolConst.ORDER_RESULT_NOTIFY_TOPIC.getProducerGroup());
        defaultMQProducer.setNamesrvAddr(nameSrvAddr);
        try {
            defaultMQProducer.start();
        } catch (MQClientException e) {
            LOGGER.error("[订单通知发送生产者]--OrderNotifySendProducer加载异常!e={}", LogExceptionWapper.getStackTrace(e));
            throw new RuntimeException("[订单通知发送生产者]--OrderNotifySendProducer加载异常!", e);
        }
        LOGGER.info("[订单通知发送生产者]--OrderNotifySendProducer加载完成!");
    }

    public DefaultMQProducer getProducer() {
        return defaultMQProducer;
    }
}
