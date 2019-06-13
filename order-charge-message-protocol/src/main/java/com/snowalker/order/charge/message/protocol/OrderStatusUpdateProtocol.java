package com.snowalker.order.charge.message.protocol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.snowalker.order.charge.message.constant.MessageProtocolConst;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/2/18 14:35
 * @className OrderStatusUpdateProtocol
 * @desc 订单状态修改消息协议
 */
public class OrderStatusUpdateProtocol extends BaseMsg implements Serializable {

    private static final long serialVersionUID = -6415079919585308245L;

    private String purseId;
    private String merchantName;
    private String orderId;
    private String chargeMoney;
    private String eventType;

    private Map<String, String> header;
    private Map<String, String> body;

    @Override
    public String encode() {
        // 组装消息协议头
        ImmutableMap.Builder headerBuilder = new ImmutableMap.Builder<String, String>()
                .put("version", this.getVersion())
                .put("topicName", MessageProtocolConst.ORDER_STATUS_UPDATE_TOPIC.getTopic());
        header = headerBuilder.build();

        body = new ImmutableMap.Builder<String, String>()
                .put("purseId", this.getPurseId())
                .put("merchantName", this.getMerchantName())
                .put("chargeMoney", this.getChargeMoney())
                .put("orderId", this.getOrderId())
                .put("eventType", this.getEventType())
                .build();

        ImmutableMap<String, Object> map = new ImmutableMap.Builder<String, Object>()
                .put("header", header)
                .put("body", body)
                .build();
        // 返回序列化消息Json串
        String ret_string = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ret_string = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("OrderStatusUpdateProtocol消息序列化json异常", e);
        }
        return ret_string;
    }

    @Override
    public void decode(String msg) {
        Preconditions.checkNotNull(msg);
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(msg);
            // header
            this.setVersion(root.get("header").get("version").asText());
            this.setTopicName(root.get("header").get("topicName").asText());
            // body
            this.setPurseId(root.get("body").get("purseId").asText());
            this.setMerchantName(root.get("body").get("merchantName").asText());
            this.setChargeMoney(root.get("body").get("chargeMoney").asText());
            this.setOrderId(root.get("body").get("orderId").asText());
            this.setEventType(root.get("body").get("eventType").asText());
        } catch (IOException e) {
            throw new RuntimeException("OrderStatusUpdateProtocol消息反序列化异常", e);
        }
    }

    public String getOrderId() {
        return orderId;
    }

    public OrderStatusUpdateProtocol setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public String getPurseId() {
        return purseId;
    }

    public OrderStatusUpdateProtocol setPurseId(String purseId) {
        this.purseId = purseId;
        return this;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public OrderStatusUpdateProtocol setMerchantName(String merchantName) {
        this.merchantName = merchantName;
        return this;
    }

    public String getChargeMoney() {
        return chargeMoney;
    }

    public OrderStatusUpdateProtocol setChargeMoney(String chargeMoney) {
        this.chargeMoney = chargeMoney;
        return this;
    }

    public String getEventType() {
        return eventType;
    }

    public OrderStatusUpdateProtocol setEventType(String eventType) {
        this.eventType = eventType;
        return this;
    }

    @Override
    public String toString() {
        return "OrderStatusUpdateProtocol{" +
                "purseId='" + purseId + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", orderId='" + orderId + '\'' +
                ", chargeMoney='" + chargeMoney + '\'' +
                ", eventType='" + eventType + '\'' +
                ", header=" + header +
                ", body=" + body +
                "} " + super.toString();
    }
}
