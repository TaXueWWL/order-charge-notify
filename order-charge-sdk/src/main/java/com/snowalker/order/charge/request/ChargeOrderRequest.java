package com.snowalker.order.charge.request;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/10 14:33
 * @className ChargeOrderRequest
 * @desc 下单sdk接口请求参数
 */
public class ChargeOrderRequest implements Serializable {

    private static final long serialVersionUID = 2596328097263464531L;

    /**商户订单号*/
    private String channelOrderId;
    private String userPhoneNum;
    private String chargePrice;
    private String purseId;
    private String merchantName;
    private String timestamp;
    private String prodId;
    private String sign;

    public String getProdId() {
        return prodId;
    }

    public ChargeOrderRequest setProdId(String prodId) {
        this.prodId = prodId;
        return this;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public ChargeOrderRequest setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getChannelOrderId() {
        return channelOrderId;
    }

    public ChargeOrderRequest setChannelOrderId(String channelOrderId) {
        this.channelOrderId = channelOrderId;
        return this;
    }

    public String getUserPhoneNum() {
        return userPhoneNum;
    }

    public ChargeOrderRequest setUserPhoneNum(String userPhoneNum) {
        this.userPhoneNum = userPhoneNum;
        return this;
    }

    public String getChargePrice() {
        return chargePrice;
    }

    public ChargeOrderRequest setChargePrice(String chargePrice) {
        this.chargePrice = chargePrice;
        return this;
    }

    public String getPurseId() {
        return purseId;
    }

    public ChargeOrderRequest setPurseId(String purseId) {
        this.purseId = purseId;
        return this;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public ChargeOrderRequest setMerchantName(String merchantName) {
        this.merchantName = merchantName;
        return this;
    }

    public String getSign() {
        return sign;
    }

    public ChargeOrderRequest setSign(String sign) {
        this.sign = sign;
        return this;
    }

    /**
     * MD5签名
     */
    public String sign(String privateKey) {
        Preconditions.checkNotNull(this.getChannelOrderId());
        Preconditions.checkNotNull(this.getUserPhoneNum());
        Preconditions.checkNotNull(this.getChargePrice());
        Preconditions.checkNotNull(this.getPurseId());
        Preconditions.checkNotNull(this.getMerchantName());
        Preconditions.checkNotNull(this.getTimestamp());
        Preconditions.checkNotNull(this.getProdId());
        Preconditions.checkNotNull(privateKey);
        // 参数排序
        Map<String, String> params = new TreeMap<>();
        params.put("channelOrderId", this.getChannelOrderId());
        params.put("userPhoneNum", this.getUserPhoneNum());
        params.put("chargePrice", this.getChargePrice());
        params.put("purseId", this.getPurseId());
        params.put("merchantName", this.getMerchantName());
        params.put("timestamp", this.getTimestamp());
        params.put("prodId", this.getProdId());
        params.put("privateKey", privateKey);
        // 参数拼装并MD5签名
        StringBuilder signSourceBuilder = new StringBuilder();
        for (String key : params.keySet()) {
            signSourceBuilder.append(key).append("=").append(params.get(key)).append("&");
        }
        // 去除最后一个&
        String signSource = signSourceBuilder.toString();
        String beforeSign = signSource.substring(0, signSource.length() - 1);
        // md5签名
        return DigestUtils.md5Hex(beforeSign);
    }

    @Override
    public String toString() {
        return "ChargeOrderRequest{" +
                "channelOrderId='" + channelOrderId + '\'' +
                ", userPhoneNum='" + userPhoneNum + '\'' +
                ", chargePrice='" + chargePrice + '\'' +
                ", purseId='" + purseId + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", prodId='" + prodId + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
