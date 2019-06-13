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
 * @date 2019/6/12 11:55
 * @className OrderNofityProtocol
 * @desc 订单结果通知协议
 */
public class OrderResultNofityProtocol extends BaseMsg implements Serializable {

    private static final long serialVersionUID = 73717163386598209L;

    private String orderId;
    private String channelOrderId;
    private String userPhoneNo;
    private String chargeMoney;
    private String purseId;
    private String merchantName;
    /**商户签名密钥*/
    private String privateKey;
    /**商户通知地址*/
    private String merchantNotifyUrl;

    private Map<String, String> header;
    private Map<String, String> body;

    @Override
    public String encode() {
        // TODO 组装消息协议头
        ImmutableMap.Builder headerBuilder = new ImmutableMap.Builder<String, String>()
                .put("version", this.getVersion())
                .put("topicName", MessageProtocolConst.ORDER_RESULT_NOTIFY_TOPIC.getTopic());
        header = headerBuilder.build();

        body = new ImmutableMap.Builder<String, String>()
                .put("orderId", this.getOrderId())
                .put("channelOrderId", this.getChannelOrderId())
                .put("userPhoneNo", this.getUserPhoneNo())
                .put("chargeMoney", this.getChargeMoney())
                .put("purseId", this.getPurseId())
                .put("merchantName", this.getMerchantName())
                .put("privateKey", this.getPrivateKey())
                .put("merchantNotifyUrl", this.getMerchantNotifyUrl())
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
            throw new RuntimeException("OrderResultNofityProtocol消息序列化json异常", e);
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
            this.setOrderId(root.get("body").get("orderId").asText());
            this.setChannelOrderId(root.get("body").get("channelOrderId").asText());
            this.setUserPhoneNo(root.get("body").get("userPhoneNo").asText());
            this.setChargeMoney(root.get("body").get("chargeMoney").asText());
            this.setPurseId(root.get("body").get("purseId").asText());
            this.setMerchantName(root.get("body").get("merchantName").asText());
            this.setPrivateKey(root.get("body").get("privateKey").asText());
            this.setMerchantNotifyUrl(root.get("body").get("merchantNotifyUrl").asText());
        } catch (IOException e) {
            throw new RuntimeException("OrderStatusUpdateProtocol消息反序列化异常", e);
        }
    }

    public String getMerchantNotifyUrl() {
        return merchantNotifyUrl;
    }

    public OrderResultNofityProtocol setMerchantNotifyUrl(String merchantNotifyUrl) {
        this.merchantNotifyUrl = merchantNotifyUrl;
        return this;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public OrderResultNofityProtocol setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
        return this;
    }

    public String getOrderId() {
        return orderId;
    }

    public OrderResultNofityProtocol setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public String getChannelOrderId() {
        return channelOrderId;
    }

    public OrderResultNofityProtocol setChannelOrderId(String channelOrderId) {
        this.channelOrderId = channelOrderId;
        return this;
    }

    public String getUserPhoneNo() {
        return userPhoneNo;
    }

    public OrderResultNofityProtocol setUserPhoneNo(String userPhoneNo) {
        this.userPhoneNo = userPhoneNo;
        return this;
    }

    public String getChargeMoney() {
        return chargeMoney;
    }

    public OrderResultNofityProtocol setChargeMoney(String chargeMoney) {
        this.chargeMoney = chargeMoney;
        return this;
    }

    public String getPurseId() {
        return purseId;
    }

    public OrderResultNofityProtocol setPurseId(String purseId) {
        this.purseId = purseId;
        return this;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public OrderResultNofityProtocol setMerchantName(String merchantName) {
        this.merchantName = merchantName;
        return this;
    }

    @Override
    public String toString() {
        return "OrderResultNofityProtocol{" +
                "orderId='" + orderId + '\'' +
                ", channelOrderId='" + channelOrderId + '\'' +
                ", userPhoneNo='" + userPhoneNo + '\'' +
                ", chargeMoney='" + chargeMoney + '\'' +
                ", purseId='" + purseId + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", privateKey='" + privateKey + '\'' +
                ", header=" + header +
                ", body=" + body +
                "} " + super.toString();
    }
}
