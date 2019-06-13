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
 * @className WalletPayProtocol
 * @desc 钱包支付(扣款)消息协议
 */
public class WalletPaymentProtocol extends BaseMsg implements Serializable {

    private static final long serialVersionUID = -6415079919585308245L;

    private String orderId;
    private String channelOrderId;
    private String userPhoneNo;
    private String chargeMoney;
    private String purseId;
    private String merchantName;


    private Map<String, String> header;
    private Map<String, String> body;

    @Override
    public String encode() {
        // 组装消息协议头
        ImmutableMap.Builder headerBuilder = new ImmutableMap.Builder<String, String>()
                .put("version", this.getVersion())
                .put("topicName", MessageProtocolConst.WALLET_PAYMENT_TOPIC.getTopic());
        header = headerBuilder.build();

        body = new ImmutableMap.Builder<String, String>()
                .put("purseId", this.getPurseId())
                .put("merchantName", this.getMerchantName())
                .put("chargeMoney", this.getChargeMoney())
                .put("channelOrderId", this.getChannelOrderId())
                .put("orderId", this.getOrderId())
                .put("userPhoneNo", this.getUserPhoneNo())
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
            throw new RuntimeException("WalletPaymentProtocol消息序列化json异常", e);
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
            this.setChannelOrderId(root.get("body").get("channelOrderId").asText());
            this.setOrderId(root.get("body").get("orderId").asText());
            this.setUserPhoneNo(root.get("body").get("userPhoneNo").asText());
        } catch (IOException e) {
            throw new RuntimeException("WalletPaymentProtocol反序列化消息异常", e);
        }
    }

    public String getOrderId() {
        return orderId;
    }

    public WalletPaymentProtocol setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public String getUserPhoneNo() {
        return userPhoneNo;
    }

    public WalletPaymentProtocol setUserPhoneNo(String userPhoneNo) {
        this.userPhoneNo = userPhoneNo;
        return this;
    }

    public String getPurseId() {
        return purseId;
    }

    public WalletPaymentProtocol setPurseId(String purseId) {
        this.purseId = purseId;
        return this;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public WalletPaymentProtocol setMerchantName(String merchantName) {
        this.merchantName = merchantName;
        return this;
    }

    public String getChannelOrderId() {
        return channelOrderId;
    }

    public WalletPaymentProtocol setChannelOrderId(String channelOrderId) {
        this.channelOrderId = channelOrderId;
        return this;
    }

    public String getChargeMoney() {
        return chargeMoney;
    }

    public WalletPaymentProtocol setChargeMoney(String chargeMoney) {
        this.chargeMoney = chargeMoney;
        return this;
    }

    @Override
    public String toString() {
        return "WalletPaymentProtocol{" +
                "purseId='" + purseId + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", channelOrderId='" + channelOrderId + '\'' +
                ", chargeMoney='" + chargeMoney + '\'' +
                ", header=" + header +
                ", body=" + body +
                "} " + super.toString();
    }
}
