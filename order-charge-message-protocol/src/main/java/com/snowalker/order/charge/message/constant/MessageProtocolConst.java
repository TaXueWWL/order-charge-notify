package com.snowalker.order.charge.message.constant;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/11 14:45
 * @className MessageProtocolConst
 * @desc 消息协议常量
 */
public enum MessageProtocolConst {

    /**WALLET_PAYMENT_TOPIC 钱包扣款协议*/
    WALLET_PAYMENT_TOPIC("WALLET_PAYMENT_TOPIC", "PID_WALLET_PAYMENT", "CID_WALLET_PAYMENT", "钱包扣款协议"),
    /**ORDER_STATUS_UPDATE_TOPIC 订单状态修改协议*/
    ORDER_STATUS_UPDATE_TOPIC("ORDER_STATUS_UPDATE_TOPIC", "PID_ORDER_STATUS_UPDATE", "CID_ORDER_STATUS_UPDATE", "订单状态修改协议"),
    /**ORDER_RESULT_NOTIFY_TOPIC 订单结果通知协议*/
    ORDER_RESULT_NOTIFY_TOPIC("ORDER_RESULT_NOTIFY_TOPIC", "PID_ORDER_RESULT_NOTIFY", "CID_ORDER_RESULT_NOTIFY", "订单结果通知协议")
    ;
    /**消息主题*/
    private String topic;
    /**生产者组*/
    private String producerGroup;
    /**消费者组*/
    private String consumerGroup;
    /**消息描述*/
    private String desc;

    MessageProtocolConst(String topic, String producerGroup, String consumerGroup, String desc) {
        this.topic = topic;
        this.producerGroup = producerGroup;
        this.consumerGroup = consumerGroup;
        this.desc = desc;
    }

    public String getTopic() {
        return topic;
    }

    public String getProducerGroup() {
        return producerGroup;
    }

    public String getDesc() {
        return desc;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }}
