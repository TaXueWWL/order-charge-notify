package com.snowalker.notify.common.dao;

import com.snowalker.notify.common.dao.dataobject.ChargeRecordEntity;
import com.snowalker.notify.common.dao.dataobject.OrderEntity;
import com.snowalker.notify.common.dao.dataobject.WalletEntity;
import java.util.Map;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/2/18 9:30
 * @className WalletMapper
 * @desc 钱包Mapper
 */
public interface WalletMapper {

    /**
     * 新增用户钱包
     * @param walletEntity
     */
    void insertWallet(WalletEntity walletEntity);

    /**
     * 更新用户钱包
     * @param params
     * @return
     */
    int updateWallet(Map<String, Object> params);

    /**
     * 插入扣款流水
     */
    void insertChargeRecord(Map<String, Object> params);

    /**
     * 查询扣款流水
     * @param orderEntity
     * @return
     */
    ChargeRecordEntity queryChargeRecordByOrderId(OrderEntity orderEntity);

    /**
     * 查询钱包信息
     * @param params
     * @return
     */
    WalletEntity queryWalletInfoByPurseId(Map<String, Object> params);
}
