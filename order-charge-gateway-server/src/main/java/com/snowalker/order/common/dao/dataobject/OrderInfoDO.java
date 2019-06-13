package com.snowalker.order.common.dao.dataobject;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/11 19:50
 * @className OrderInfoDO
 * @desc 订单数据库映射实体
 */
public class OrderInfoDO {

    private String orderId;
    private String channelOrderId;
    private String userPhoneNo;
    private String chargeMoney;
    private String purseId;
    private String merchantName;

    /**修改前后的订单状态*/
    private Integer beforeOrderStatus;
    private Integer afterOrderStatus;
    /**修改前后的通知状态*/
    private Integer beforeNotifyStatus;
    private Integer afterNotifyStatus;
    /**修改前后的支付状态*/
    private Integer beforePayStatus;
    private Integer afterPayStatus;

    public Integer getBeforeOrderStatus() {
        return beforeOrderStatus;
    }

    public OrderInfoDO setBeforeOrderStatus(Integer beforeOrderStatus) {
        this.beforeOrderStatus = beforeOrderStatus;
        return this;
    }

    public Integer getAfterOrderStatus() {
        return afterOrderStatus;
    }

    public OrderInfoDO setAfterOrderStatus(Integer afterOrderStatus) {
        this.afterOrderStatus = afterOrderStatus;
        return this;
    }

    public Integer getBeforeNotifyStatus() {
        return beforeNotifyStatus;
    }

    public OrderInfoDO setBeforeNotifyStatus(Integer beforeNotifyStatus) {
        this.beforeNotifyStatus = beforeNotifyStatus;
        return this;
    }

    public Integer getAfterNotifyStatus() {
        return afterNotifyStatus;
    }

    public OrderInfoDO setAfterNotifyStatus(Integer afterNotifyStatus) {
        this.afterNotifyStatus = afterNotifyStatus;
        return this;
    }

    public Integer getBeforePayStatus() {
        return beforePayStatus;
    }

    public OrderInfoDO setBeforePayStatus(Integer beforePayStatus) {
        this.beforePayStatus = beforePayStatus;
        return this;
    }

    public Integer getAfterPayStatus() {
        return afterPayStatus;
    }

    public OrderInfoDO setAfterPayStatus(Integer afterPayStatus) {
        this.afterPayStatus = afterPayStatus;
        return this;
    }

    public String getOrderId() {
        return orderId;
    }

    public OrderInfoDO setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public String getChannelOrderId() {
        return channelOrderId;
    }

    public OrderInfoDO setChannelOrderId(String channelOrderId) {
        this.channelOrderId = channelOrderId;
        return this;
    }

    public String getUserPhoneNo() {
        return userPhoneNo;
    }

    public OrderInfoDO setUserPhoneNo(String userPhoneNo) {
        this.userPhoneNo = userPhoneNo;
        return this;
    }

    public String getChargeMoney() {
        return chargeMoney;
    }

    public OrderInfoDO setChargeMoney(String chargeMoney) {
        this.chargeMoney = chargeMoney;
        return this;
    }

    public String getPurseId() {
        return purseId;
    }

    public OrderInfoDO setPurseId(String purseId) {
        this.purseId = purseId;
        return this;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public OrderInfoDO setMerchantName(String merchantName) {
        this.merchantName = merchantName;
        return this;
    }

    @Override
    public String toString() {
        return "OrderInfoDO{" +
                "orderId='" + orderId + '\'' +
                ", channelOrderId='" + channelOrderId + '\'' +
                ", userPhoneNo='" + userPhoneNo + '\'' +
                ", chargeMoney='" + chargeMoney + '\'' +
                ", purseId='" + purseId + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", beforeOrderStatus=" + beforeOrderStatus +
                ", afterOrderStatus=" + afterOrderStatus +
                ", beforeNotifyStatus=" + beforeNotifyStatus +
                ", afterNotifyStatus=" + afterNotifyStatus +
                ", beforePayStatus=" + beforePayStatus +
                ", afterPayStatus=" + afterPayStatus +
                '}';
    }
}
