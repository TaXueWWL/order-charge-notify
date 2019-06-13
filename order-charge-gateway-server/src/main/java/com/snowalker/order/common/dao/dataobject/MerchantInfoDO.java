package com.snowalker.order.common.dao.dataobject;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/11 14:15
 * @className MerchantInfoDO
 * @desc 商户配置信息
 */
public class MerchantInfoDO {

    private String merchantPurseId;
    private String merchantName;
    private String merchantNotifyUrl;
    private String merchantPrivateKey;

    public String getMerchantPurseId() {
        return merchantPurseId;
    }

    public MerchantInfoDO setMerchantPurseId(String merchantPurseId) {
        this.merchantPurseId = merchantPurseId;
        return this;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public MerchantInfoDO setMerchantName(String merchantName) {
        this.merchantName = merchantName;
        return this;
    }

    public String getMerchantNotifyUrl() {
        return merchantNotifyUrl;
    }

    public MerchantInfoDO setMerchantNotifyUrl(String merchantNotifyUrl) {
        this.merchantNotifyUrl = merchantNotifyUrl;
        return this;
    }

    public String getMerchantPrivateKey() {
        return merchantPrivateKey;
    }

    public MerchantInfoDO setMerchantPrivateKey(String merchantPrivateKey) {
        this.merchantPrivateKey = merchantPrivateKey;
        return this;
    }

    @Override
    public String toString() {
        return "MerchantInfoDO{" +
                "merchantPurseId='" + merchantPurseId + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", merchantNotifyUrl='" + merchantNotifyUrl + '\'' +
                ", merchantPrivateKey='" + merchantPrivateKey + '\'' +
                '}';
    }
}
