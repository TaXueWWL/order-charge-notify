package com.snowalker.notify.mq.notify.listener;

import com.snowalker.notify.common.constant.NotifyConstant;
import com.snowalker.notify.common.util.DateUtil;
import com.snowalker.notify.common.util.LogExceptionWapper;
import com.snowalker.notify.mq.payment.producer.OrderStatusUpdateProducer;
import com.snowalker.order.charge.message.constant.MessageProtocolConst;
import com.snowalker.order.charge.message.constant.UpdateEventTypeConst;
import com.snowalker.order.charge.message.protocol.OrderResultNofityProtocol;
import com.snowalker.order.charge.message.protocol.OrderStatusUpdateProtocol;
import com.snowalker.order.charge.request.ChargeNotifyRequest;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/12 14:17
 * @className NotifySendListenerImpl
 * @desc 通知发送消息回调
 */
@Component(value = "notifySendListenerImpl")
public class NotifySendListenerImpl implements MessageListenerConcurrently {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotifySendListenerImpl.class);

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    OrderStatusUpdateProducer orderStatusUpdateProducer;

    private static final Integer MAX_RECONSUME_TIMES = 5;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

        try {
            for (MessageExt msg : msgs) {
                // 消息解码
                String message = new String(msg.getBody());
                // 消费次数
                int reconsumeTimes = msg.getReconsumeTimes();
                String msgId = msg.getMsgId();
                String logSuffix = ",msgId=" + msgId + ",reconsumeTimes=" + reconsumeTimes;

                LOGGER.info("[通知发送消息消费者]-OrderNotifySendProducer-接收到消息,message={},{}", message, logSuffix);
                // 请求组装
                OrderResultNofityProtocol protocol = new OrderResultNofityProtocol();
                protocol.decode(message);
                // 参数加签，获取用户privatekey
                String privateKey = protocol.getPrivateKey();
                String notifyUrl = protocol.getMerchantNotifyUrl();
                String purseId = protocol.getPurseId();
                ChargeNotifyRequest chargeNotifyRequest = new ChargeNotifyRequest();
                chargeNotifyRequest.setChannel_orderid(protocol.getChannelOrderId())
                        .setFinish_time(DateUtil.formatDate(new Date(System.currentTimeMillis())))
                        .setOrder_status(NotifyConstant.NOTIFY_SUCCESS)
                        .setPlat_orderid(protocol.getOrderId())
                        .setSign(chargeNotifyRequest.sign(privateKey));
                LOGGER.info("[通知发送消息消费者]-OrderNotifySendProducer-订单结果通知入参:{},{}", chargeNotifyRequest.toString(), logSuffix);
                // 通知发送
                return sendNotifyByPost(reconsumeTimes, logSuffix, protocol, notifyUrl, purseId, chargeNotifyRequest);
            }
        } catch (Exception e) {
            LOGGER.error("[通知发送消息消费者]消费异常,e={}", LogExceptionWapper.getStackTrace(e));
        }
        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
    }

    /**
     * 进行通知POST报文发送
     * @param reconsumeTimes
     * @param logSuffix
     * @param protocol
     * @param notifyUrl
     * @param purseId
     * @param chargeNotifyRequest
     * @return
     * @throws MQClientException
     * @throws RemotingException
     * @throws MQBrokerException
     * @throws InterruptedException
     */
    private ConsumeConcurrentlyStatus sendNotifyByPost(int reconsumeTimes, String logSuffix, OrderResultNofityProtocol protocol, String notifyUrl, String purseId, ChargeNotifyRequest chargeNotifyRequest) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //  封装参数，千万不要替换为Map与HashMap，否则参数无法传递
        MultiValueMap<String, String> params= new LinkedMultiValueMap<String, String>();
        params.add("order_status", chargeNotifyRequest.getOrder_status());
        params.add("channel_orderid", chargeNotifyRequest.getChannel_orderid());
        params.add("plat_orderid", chargeNotifyRequest.getPlat_orderid());
        params.add("finish_time", chargeNotifyRequest.getFinish_time());
        params.add("sign", chargeNotifyRequest.getSign());
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(params, headers);
        ResponseEntity<String> notifyResponse = restTemplate.exchange(
                notifyUrl, HttpMethod.POST, requestEntity, String.class);
        // 返回参校验
        if (notifyResponse == null) {
            LOGGER.error("[通知发送消息消费者]-OrderNotifySendProducer-当前商户通知返回为空,等待下次通知.purseId={},{}", purseId, logSuffix);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
        if (reconsumeTimes > MAX_RECONSUME_TIMES) {
            // TODO 入冲发表
            LOGGER.info("[通知发送消息消费者]-OrderNotifySendProducer-当前商户通知次数大于5次,不再通知,purseId={},{}", purseId, logSuffix);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        // 解析返回参
        String notifyBody = notifyResponse.getBody();
        LOGGER.info("[通知发送消息消费者]-OrderNotifySendProducer-订单结果通知出参:[{}],{}", notifyBody, logSuffix);
        if (!NotifyConstant.NOTIFY_RETURN_SUCC.equals(notifyBody)) {
            LOGGER.info("[通知发送消息消费者]-OrderNotifySendProducer-订单结果通知[失败],等待下次通知.purseId={},{}", purseId, logSuffix);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
        // 通知成功,发送订单状态更新消息，事件=EVENT_UPDATE_NOTIFY_OD_STATUS
        OrderStatusUpdateProtocol orderStatusUpdateProtocol = new OrderStatusUpdateProtocol();
        orderStatusUpdateProtocol.setTopicName(MessageProtocolConst.ORDER_STATUS_UPDATE_TOPIC.getTopic());
        orderStatusUpdateProtocol.setOrderId(protocol.getOrderId())
                .setChargeMoney(protocol.getChargeMoney())
                .setPurseId(protocol.getPurseId())
                .setMerchantName(protocol.getMerchantName())
                .setEventType(UpdateEventTypeConst.EVENT_UPDATE_NOTIFY_OD_STATUS.getEventType());
        Message updateOrderStatusMsg =
                new Message(MessageProtocolConst.ORDER_STATUS_UPDATE_TOPIC.getTopic(),
                        orderStatusUpdateProtocol.encode().getBytes());
        orderStatusUpdateProducer.getProducer().send(updateOrderStatusMsg);
        LOGGER.info("[通知发送消息消费者]-OrderNotifySendProducer-发送通知状态更新消息结束,purseId={},{}", purseId, logSuffix);
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
