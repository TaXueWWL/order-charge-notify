package com.snowalker.notify.mq.payment.producer.listener;

import com.snowalker.notify.common.dao.dataobject.ChargeRecordEntity;
import com.snowalker.notify.common.dao.dataobject.WalletEntity;
import com.snowalker.notify.common.service.WalletService;
import com.snowalker.notify.common.util.LogExceptionWapper;
import com.snowalker.order.charge.message.protocol.OrderStatusUpdateProtocol;
import com.snowalker.order.charge.message.protocol.WalletPaymentProtocol;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/12 9:41
 * @className PaymentTranListenerImpl
 * @desc 扣款本地事务监听回调
 */
@Component(value = "localTranListenerImpl")
public class LocalTranListenerImpl implements TransactionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalTranListenerImpl.class);

    @Autowired
    WalletService walletService;

    /**
     * 扣款本地事务
     * @param msg
     * @param arg
     * @return
     */
    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        String message = new String(msg.getBody());
        LOGGER.info("[扣款本地事务监听回调]执行逻辑--接收到消息, message={}", message);
        OrderStatusUpdateProtocol protocol = new OrderStatusUpdateProtocol();
        protocol.decode(message);
        // 扣款
        String purseId = protocol.getPurseId();
        String merchantName = protocol.getMerchantName();
        LOGGER.info("[扣款本地事务监听回调]反序列化扣款消息成功,开始进行扣款操作,purseId={}", purseId);
        WalletEntity walletEntity = new WalletEntity();
        walletEntity.setChargeMoney(
                new BigDecimal(protocol.getChargeMoney()))
                .setPurseId(purseId)
                .setMerchantName(merchantName);
        // 查询当前账户信息获取版本号,基于乐观锁更新
        WalletEntity realWalletInfo = walletService.queryWalletInfoByPurseId(protocol.getPurseId());
        // 判断是否足够扣减
        BigDecimal currBalanceAccount = realWalletInfo.getBalanceAccount();
        if (currBalanceAccount.subtract(new BigDecimal(protocol.getChargeMoney())).longValue() < 0) {
            LOGGER.error("执行钱包扣款,账户不足扣减,消息回滚.purseId={}", purseId);
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
        // 足够扣减,进行账户扣减
        int version = realWalletInfo.getVersion();
        walletEntity.setVersion(version);
        try {
            if (!walletService.updateWallet(walletEntity, protocol.getOrderId())) {
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }
        } catch (Exception e) {
            LOGGER.error("执行钱包扣款本地扣款异常,purseId={},e={}", purseId, LogExceptionWapper.getStackTrace(e));
            return LocalTransactionState.UNKNOW;
        }
        LOGGER.info("[扣款本地事务监听回调]扣款本地操作成功,本地事务提交,purseId={}", purseId);
        return LocalTransactionState.COMMIT_MESSAGE;
    }

    /**
     * 本地事务回查: 回查依据是否存在扣款流水
     * @param msg
     * @return
     */
    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        // 本地事务回查，查询扣款流水
        String message = new String(msg.getBody());
        String msgId = msg.getMsgId();
        LOGGER.info("[扣款本地事务回查]-接收到消息,msgId={},message={}", msgId, message);
        WalletPaymentProtocol payProtocol = new WalletPaymentProtocol();
        payProtocol.decode(message);
        // 获取订单号
        String orderId = payProtocol.getOrderId();
        ChargeRecordEntity chargeRecordEntity =
                walletService.queryChargeRecordByOrderId(orderId);
        if (chargeRecordEntity == null) {
            LOGGER.info("[扣款本地事务回查]-本地不存在扣款流水,消息回滚,msgId={}", msgId);
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
        LOGGER.info("[扣款本地事务回查]-本地存在扣款流水,消息提交,msgId={}", msgId);
        return LocalTransactionState.COMMIT_MESSAGE;
    }
}
