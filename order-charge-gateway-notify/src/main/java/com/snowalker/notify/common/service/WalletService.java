package com.snowalker.notify.common.service;

import com.snowalker.notify.common.dao.dataobject.ChargeRecordEntity;
import com.snowalker.notify.common.dao.dataobject.WalletEntity;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/12 9:15
 * @className WalletService
 * @desc 钱包相关接口定义
 */
public interface WalletService {

    /**
     * 新增用户钱包
     * @param walletEntity
     */
    void insertWallet(WalletEntity walletEntity);

    /**
     * 修改用户钱包数据
     * @param walletEntity
     * @return
     */
    boolean updateWallet(WalletEntity walletEntity, String orderId);

    /**
     * 查询扣款流水
     * @param orderId
     * @return
     */
    ChargeRecordEntity queryChargeRecordByOrderId(String orderId);

    /**
     * 查询钱包信息
     * @param purseId
     * @return
     */
    WalletEntity queryWalletInfoByPurseId(String purseId);
}
