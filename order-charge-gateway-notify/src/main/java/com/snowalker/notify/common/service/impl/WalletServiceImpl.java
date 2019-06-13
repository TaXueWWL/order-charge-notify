package com.snowalker.notify.common.service.impl;

import com.snowalker.notify.common.dao.WalletMapper;
import com.snowalker.notify.common.dao.dataobject.ChargeRecordEntity;
import com.snowalker.notify.common.dao.dataobject.OrderEntity;
import com.snowalker.notify.common.dao.dataobject.WalletEntity;
import com.snowalker.notify.common.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/12 9:20
 * @className WalletServiceImpl
 * @desc 钱包接口实现
 */
@Service(value = "walletService")
public class WalletServiceImpl implements WalletService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalletServiceImpl.class);

    @Autowired
    WalletMapper walletMapper;

    /**
     * 新增用户钱包
     * @param walletEntity
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insertWallet(WalletEntity walletEntity) {
        String purseId = walletEntity.getPurseId();
        try {
            walletMapper.insertWallet(walletEntity);
            LOGGER.info("钱包数据插入成功,purseId={}", purseId);
        } catch (Exception e) {
            LOGGER.error("钱包数据插入异常,purseId={},e={}", purseId, e);
            throw e;
        }
    }

    /**
     * 修改用户钱包数据
     * @param walletEntity
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateWallet(WalletEntity walletEntity, String orderId) {
        // 插入扣款流水
        Map<String, Object> paramsInsert = new HashMap<>(16);
        String recordId = UUID.randomUUID().toString().replace("-", "");
        paramsInsert.put("recordId", recordId);
        paramsInsert.put("orderId", orderId);
        paramsInsert.put("purseId", walletEntity.getPurseId());
        paramsInsert.put("merchantName", walletEntity.getMerchantName());
        paramsInsert.put("chargeMoney", walletEntity.getChargeMoney());
        try {
            walletMapper.insertChargeRecord(paramsInsert);
            LOGGER.info("扣款流水插入成功,purseId={},recordId={}", walletEntity.getPurseId(), recordId);
        } catch (Exception e) {
            LOGGER.error("扣款流水插入异常,purseId={},recordId={},e={}", walletEntity.getPurseId(), recordId, e);
            throw e;
        }
        // 执行扣款
        Map<String, Object> paramsUpdate = new HashMap<>(16);
        paramsUpdate.put("chargeMoney", walletEntity.getChargeMoney());
        paramsUpdate.put("version", walletEntity.getVersion());
        paramsUpdate.put("purseId", walletEntity.getPurseId());
        int updateCount = walletMapper.updateWallet(paramsUpdate);
        if (updateCount == 1) {
            LOGGER.info("钱包数据修改成功,purseId={}", walletEntity.getPurseId());
            return true;
        } else {
            LOGGER.error("并发修改钱包数据修改异常,purseId={}", walletEntity.getPurseId());
            throw new RuntimeException("Exception occurred while updating wallet, purseId=" + walletEntity.getPurseId());
        }
    }

    /**
     * 查询扣款流水
     * @param orderId
     * @return
     */
    @Override
    public ChargeRecordEntity queryChargeRecordByOrderId(String orderId) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderId(orderId);
        return walletMapper.queryChargeRecordByOrderId(orderEntity);
    }

    /**
     * 查询钱包信息
     * @param purseId
     * @return
     */
    @Override
    public WalletEntity queryWalletInfoByPurseId(String purseId) {
        Map<String, Object> queryParams = new HashMap<>(16);
        queryParams.put("purseId", purseId);
        WalletEntity walletEntity = walletMapper.queryWalletInfoByPurseId(queryParams);
        LOGGER.info("根据钱包purseId={},查询到钱包信息:walletInfo={}", purseId, walletEntity.toString());
        return walletEntity;
    }
}
