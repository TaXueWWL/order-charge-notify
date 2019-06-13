package com.snowalker.notify.common.dao.dataobject;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/2/15 17:21
 * @className ChargeRecordEntity
 * @desc 模拟钱包扣款流水
 */
public class ChargeRecordEntity {

    private int id;
    /**扣款流水id*/
    private String recordId;
    private String orderId;
    private String purseId;
    private String merchantName;
    /**本次交易流水额度*/
    private BigDecimal chargePrice;
    private Date gmtCreate;
    private Date gmtUpdate;

    public String getMerchantName() {
        return merchantName;
    }

    public ChargeRecordEntity setMerchantName(String merchantName) {
        this.merchantName = merchantName;
        return this;
    }

    public int getId() {
        return id;
    }

    public ChargeRecordEntity setId(int id) {
        this.id = id;
        return this;
    }

    public String getRecordId() {
        return recordId;
    }

    public ChargeRecordEntity setRecordId(String recordId) {
        this.recordId = recordId;
        return this;
    }

    public String getOrderId() {
        return orderId;
    }

    public ChargeRecordEntity setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public String getPurseId() {
        return purseId;
    }

    public ChargeRecordEntity setPurseId(String purseId) {
        this.purseId = purseId;
        return this;
    }


    public BigDecimal getChargePrice() {
        return chargePrice;
    }

    public ChargeRecordEntity setChargePrice(BigDecimal chargePrice) {
        this.chargePrice = chargePrice;
        return this;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public ChargeRecordEntity setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
        return this;
    }

    public Date getGmtUpdate() {
        return gmtUpdate;
    }

    public ChargeRecordEntity setGmtUpdate(Date gmtUpdate) {
        this.gmtUpdate = gmtUpdate;
        return this;
    }

    @Override
    public String toString() {
        return "ChargeRecordEntity{" +
                "id=" + id +
                ", recordId='" + recordId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", purseId='" + purseId + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", chargePrice=" + chargePrice +
                ", gmtCreate=" + gmtCreate +
                ", gmtUpdate=" + gmtUpdate +
                '}';
    }
}
