package com.snowalker.order.common.dao.dataobject;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/11 19:50
 * @className OrderInfoDO
 * @desc 订单数据库映射实体
 */
public class OrderInfoDobj {

    private Integer orderStatus;
    private Integer notifyStatus;
    private Integer payStatus;
    private String orderId;
    private String channelOrderId;
    private String userPhoneNo;
    private String chargeMoney;
    private String purseId;
    private String merchantName;

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public OrderInfoDobj setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
        return this;
    }

    public Integer getNotifyStatus() {
        return notifyStatus;
    }

    public OrderInfoDobj setNotifyStatus(Integer notifyStatus) {
        this.notifyStatus = notifyStatus;
        return this;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public OrderInfoDobj setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
        return this;
    }

    public String getOrderId() {
        return orderId;
    }

    public OrderInfoDobj setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public String getChannelOrderId() {
        return channelOrderId;
    }

    public OrderInfoDobj setChannelOrderId(String channelOrderId) {
        this.channelOrderId = channelOrderId;
        return this;
    }

    public String getUserPhoneNo() {
        return userPhoneNo;
    }

    public OrderInfoDobj setUserPhoneNo(String userPhoneNo) {
        this.userPhoneNo = userPhoneNo;
        return this;
    }

    public String getChargeMoney() {
        return chargeMoney;
    }

    public OrderInfoDobj setChargeMoney(String chargeMoney) {
        this.chargeMoney = chargeMoney;
        return this;
    }

    public String getPurseId() {
        return purseId;
    }

    public OrderInfoDobj setPurseId(String purseId) {
        this.purseId = purseId;
        return this;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public OrderInfoDobj setMerchantName(String merchantName) {
        this.merchantName = merchantName;
        return this;
    }

    @Override
    public String toString() {
        return "OrderInfoDobj{" +
                "orderStatus=" + orderStatus +
                ", notifyStatus=" + notifyStatus +
                ", payStatus=" + payStatus +
                ", orderId='" + orderId + '\'' +
                ", channelOrderId='" + channelOrderId + '\'' +
                ", userPhoneNo='" + userPhoneNo + '\'' +
                ", chargeMoney='" + chargeMoney + '\'' +
                ", purseId='" + purseId + '\'' +
                ", merchantName='" + merchantName + '\'' +
                '}';
    }
}
