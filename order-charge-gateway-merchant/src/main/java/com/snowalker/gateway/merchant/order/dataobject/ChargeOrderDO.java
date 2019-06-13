package com.snowalker.gateway.merchant.order.dataobject;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/11 11:26
 * @className ChargeOrderDO
 * @desc 订单数据库DO
 */
public class ChargeOrderDO {

    private Integer id;
    private Date gmtCreate;
    private Date gmtUpdate;
    private String orderId;
    private Integer orderStatus;
    private String userPhoneNo;
    private String prodId;
    private String prodName;
    private String outProdId;
    private String outProdName;
    private BigDecimal chargeMoney;
    private Date finishTime;
    private String outOrderId;

    public Integer getId() {
        return id;
    }

    public ChargeOrderDO setId(Integer id) {
        this.id = id;
        return this;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public ChargeOrderDO setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
        return this;
    }

    public Date getGmtUpdate() {
        return gmtUpdate;
    }

    public ChargeOrderDO setGmtUpdate(Date gmtUpdate) {
        this.gmtUpdate = gmtUpdate;
        return this;
    }

    public String getOrderId() {
        return orderId;
    }

    public ChargeOrderDO setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public ChargeOrderDO setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
        return this;
    }

    public String getUserPhoneNo() {
        return userPhoneNo;
    }

    public ChargeOrderDO setUserPhoneNo(String userPhoneNo) {
        this.userPhoneNo = userPhoneNo;
        return this;
    }

    public String getProdId() {
        return prodId;
    }

    public ChargeOrderDO setProdId(String prodId) {
        this.prodId = prodId;
        return this;
    }

    public String getProdName() {
        return prodName;
    }

    public ChargeOrderDO setProdName(String prodName) {
        this.prodName = prodName;
        return this;
    }

    public String getOutProdId() {
        return outProdId;
    }

    public ChargeOrderDO setOutProdId(String outProdId) {
        this.outProdId = outProdId;
        return this;
    }

    public String getOutProdName() {
        return outProdName;
    }

    public ChargeOrderDO setOutProdName(String outProdName) {
        this.outProdName = outProdName;
        return this;
    }

    public BigDecimal getChargeMoney() {
        return chargeMoney;
    }

    public ChargeOrderDO setChargeMoney(BigDecimal chargeMoney) {
        this.chargeMoney = chargeMoney;
        return this;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public ChargeOrderDO setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
        return this;
    }

    public String getOutOrderId() {
        return outOrderId;
    }

    public ChargeOrderDO setOutOrderId(String outOrderId) {
        this.outOrderId = outOrderId;
        return this;
    }

    @Override
    public String toString() {
        return "ChargeOrderDO{" +
                "id=" + id +
                ", gmtCreate=" + gmtCreate +
                ", gmtUpdate=" + gmtUpdate +
                ", orderId='" + orderId + '\'' +
                ", orderStatus=" + orderStatus +
                ", userPhoneNo='" + userPhoneNo + '\'' +
                ", prodId='" + prodId + '\'' +
                ", prodName='" + prodName + '\'' +
                ", outProdId='" + outProdId + '\'' +
                ", outProdName='" + outProdName + '\'' +
                ", chargeMoney=" + chargeMoney +
                ", finishTime=" + finishTime +
                ", outOrderId='" + outOrderId + '\'' +
                '}';
    }
}
