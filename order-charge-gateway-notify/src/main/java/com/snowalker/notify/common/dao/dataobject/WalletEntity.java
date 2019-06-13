package com.snowalker.notify.common.dao.dataobject;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/2/15 16:54
 * @className PurseEntity
 * @desc 模拟钱包数据库操作实体
 */
public class WalletEntity {

    private int id;
    private String purseId;
    private String merchantName;
    private BigDecimal balanceAccount;
    private BigDecimal chargeMoney;
    /**账户状态 账户状态 0 正常 1 异常*/
    private int accountStatus;
    private Date gmtCreate;
    private Date gmtUpdate;
    private int version;

    public BigDecimal getChargeMoney() {
        return chargeMoney;
    }

    public WalletEntity setChargeMoney(BigDecimal chargeMoney) {
        this.chargeMoney = chargeMoney;
        return this;
    }

    public int getVersion() {
        return version;
    }

    public WalletEntity setVersion(int version) {
        this.version = version;
        return this;
    }

    public int getId() {
        return id;
    }

    public WalletEntity setId(int id) {
        this.id = id;
        return this;
    }

    public String getPurseId() {
        return purseId;
    }

    public WalletEntity setPurseId(String purseId) {
        this.purseId = purseId;
        return this;
    }

    public BigDecimal getBalanceAccount() {
        return balanceAccount;
    }

    public WalletEntity setBalanceAccount(BigDecimal balanceAccount) {
        this.balanceAccount = balanceAccount;
        return this;
    }

    public int getAccountStatus() {
        return accountStatus;
    }

    public WalletEntity setAccountStatus(int accountStatus) {
        this.accountStatus = accountStatus;
        return this;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public WalletEntity setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
        return this;
    }

    public Date getGmtUpdate() {
        return gmtUpdate;
    }

    public WalletEntity setGmtUpdate(Date gmtUpdate) {
        this.gmtUpdate = gmtUpdate;
        return this;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public WalletEntity setMerchantName(String merchantName) {
        this.merchantName = merchantName;
        return this;
    }

    @Override
    public String toString() {
        return "WalletEntity{" +
                "id=" + id +
                ", purseId='" + purseId + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", balanceAccount=" + balanceAccount +
                ", chargeMoney=" + chargeMoney +
                ", accountStatus=" + accountStatus +
                ", gmtCreate=" + gmtCreate +
                ", gmtUpdate=" + gmtUpdate +
                ", version=" + version +
                '}';
    }
}
