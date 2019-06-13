package com.snowalker.order.charge.response;

import java.io.Serializable;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/10 16:03
 * @className ChargeOrderResponse
 * @desc 下单sdk接口返回参数
 */
public class ChargeOrderResponse implements Serializable {

    private static final long serialVersionUID = -5685058946404699059L;

    /**商户订单号*/
    private String channelOrderId;
    private String userPhoneNum;
    private String chargePrice;
    private String merchantName;
    /**票务服务订单号*/
    private String platOrderId;

    public String getChannelOrderId() {
        return channelOrderId;
    }

    public ChargeOrderResponse setChannelOrderId(String channelOrderId) {
        this.channelOrderId = channelOrderId;
        return this;
    }

    public String getUserPhoneNum() {
        return userPhoneNum;
    }

    public ChargeOrderResponse setUserPhoneNum(String userPhoneNum) {
        this.userPhoneNum = userPhoneNum;
        return this;
    }

    public String getChargePrice() {
        return chargePrice;
    }

    public ChargeOrderResponse setChargePrice(String chargePrice) {
        this.chargePrice = chargePrice;
        return this;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public ChargeOrderResponse setMerchantName(String merchantName) {
        this.merchantName = merchantName;
        return this;
    }

    public String getPlatOrderId() {
        return platOrderId;
    }

    public ChargeOrderResponse setPlatOrderId(String platOrderId) {
        this.platOrderId = platOrderId;
        return this;
    }

    @Override
    public String toString() {
        return "ChargeOrderResponse{" +
                "channelOrderId='" + channelOrderId + '\'' +
                ", userPhoneNum='" + userPhoneNum + '\'' +
                ", chargePrice='" + chargePrice + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", platOrderId='" + platOrderId + '\'' +
                '}';
    }
}
