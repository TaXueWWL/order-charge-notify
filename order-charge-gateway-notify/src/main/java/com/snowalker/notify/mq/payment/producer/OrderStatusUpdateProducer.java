package com.snowalker.notify.mq.payment.producer;

import com.snowalker.order.charge.message.constant.MessageProtocolConst;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/12 9:39
 * @className OrderStatusUpdateProducer
 * @desc 订单状态修改生产者
 */
@Component
public class OrderStatusUpdateProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderStatusUpdateProducer.class);

    /**事务回查线程池*/
    private ExecutorService executorService;
    /**事务消息生产者*/
    private TransactionMQProducer transactionMQProducer;

    @Value("${rocketmq.nameServer}")
    private String nameSrvAddr;

    @Resource(name = "localTranListenerImpl")
    TransactionListener transactionListener;

    @PostConstruct
    public void init() {
        // 初始化回查线程池
        executorService = new ThreadPoolExecutor(
                5,
                512,
                10000L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(512),
                runnable -> {
                    Thread thread = new Thread(runnable);
                    thread.setName(MessageProtocolConst.ORDER_STATUS_UPDATE_TOPIC.getProducerGroup() + "-check-thread");
                    return null;
                });

        transactionMQProducer = new TransactionMQProducer(MessageProtocolConst.ORDER_STATUS_UPDATE_TOPIC.getProducerGroup());
        transactionMQProducer.setNamesrvAddr(nameSrvAddr);
        transactionMQProducer.setExecutorService(executorService);
        transactionMQProducer.setTransactionListener(transactionListener);
        try {
            transactionMQProducer.start();
        } catch (MQClientException e) {
            throw new RuntimeException("启动[订单状态修改生产者]OrderStatusUpdateProducer异常", e);
        }
        LOGGER.info("启动[订单状态修改生产者]OrderStatusUpdateProducer成功, topic={}", MessageProtocolConst.ORDER_STATUS_UPDATE_TOPIC.getTopic());
    }

    public TransactionMQProducer getProducer() {
        return transactionMQProducer;
    }
}
