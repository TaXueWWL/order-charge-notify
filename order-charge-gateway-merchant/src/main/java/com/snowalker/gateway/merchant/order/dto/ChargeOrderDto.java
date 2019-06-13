package com.snowalker.gateway.merchant.order.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/10 13:37
 * @className ChargeOrderDto
 * @desc 本地下单入参
 */
public class ChargeOrderDto {

    private String orderId;
    private String phoneNum;
    private int orderStatus = 1;
    private String productId;
    private String productName;
    private String outProductId;
    private String outProductName;
    private BigDecimal chargeMoney;

    /**更新前状态*/
    private int beforeUpdateOrderStatus;
    /**欲更新状态*/
    private int afterUpdateOrderStatus;
    /**结束时间*/
    private Date finishTime;
    /**外部订单号*/
    private String outOrderId;

    public String getOutOrderId() {
        return outOrderId;
    }

    public ChargeOrderDto setOutOrderId(String outOrderId) {
        this.outOrderId = outOrderId;
        return this;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public ChargeOrderDto setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
        return this;
    }

    public int getBeforeUpdateOrderStatus() {
        return beforeUpdateOrderStatus;
    }

    public ChargeOrderDto setBeforeUpdateOrderStatus(int beforeUpdateOrderStatus) {
        this.beforeUpdateOrderStatus = beforeUpdateOrderStatus;
        return this;
    }

    public int getAfterUpdateOrderStatus() {
        return afterUpdateOrderStatus;
    }

    public ChargeOrderDto setAfterUpdateOrderStatus(int afterUpdateOrderStatus) {
        this.afterUpdateOrderStatus = afterUpdateOrderStatus;
        return this;
    }

    public BigDecimal getChargeMoney() {
        return chargeMoney;
    }

    public ChargeOrderDto setChargeMoney(BigDecimal chargeMoney) {
        this.chargeMoney = chargeMoney;
        return this;
    }

    public String getOrderId() {
        return orderId;
    }

    public ChargeOrderDto setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public ChargeOrderDto setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
        return this;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public ChargeOrderDto setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
        return this;
    }

    public String getProductId() {
        return productId;
    }

    public ChargeOrderDto setProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public ChargeOrderDto setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public String getOutProductId() {
        return outProductId;
    }

    public ChargeOrderDto setOutProductId(String outProductId) {
        this.outProductId = outProductId;
        return this;
    }

    public String getOutProductName() {
        return outProductName;
    }

    public ChargeOrderDto setOutProductName(String outProductName) {
        this.outProductName = outProductName;
        return this;
    }

    @Override
    public String toString() {
        return "ChargeOrderDto{" +
                "orderId='" + orderId + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", orderStatus=" + orderStatus +
                ", productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", outProductId='" + outProductId + '\'' +
                ", outProductName='" + outProductName + '\'' +
                ", chargeMoney=" + chargeMoney +
                ", beforeUpdateOrderStatus=" + beforeUpdateOrderStatus +
                ", afterUpdateOrderStatus=" + afterUpdateOrderStatus +
                ", finishTime=" + finishTime +
                ", outOrderId='" + outOrderId + '\'' +
                '}';
    }
}
