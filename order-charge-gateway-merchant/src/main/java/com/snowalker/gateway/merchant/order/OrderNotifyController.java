package com.snowalker.gateway.merchant.order;

import com.snowalker.gateway.merchant.order.constant.NotifyConstant;
import com.snowalker.gateway.merchant.order.service.OrderService;
import com.snowalker.order.charge.request.ChargeNotifyRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/10 10:07
 * @className OrderNotifyController
 * @desc 订单充值回调接口
 *  接收并处理成功返回T
 *  其余情况返回F，上游充值平台重试
 */
@Controller
@RequestMapping(value = "api")
public class OrderNotifyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderNotifyController.class);

    @Resource(name = "orderService")
    OrderService orderService;

    @Value("${agent.config.private.key}")
    private String privateKey;

    /**
     * 接受充值结果通知
     * @param chargeNotifyRequest
     * orderStatus 订单状态
     * channelOrderId  本平台订单
     * platOrderId 充值平台订单
     * finishTime 订单结束时间，时间戳yyyyMMddHHmmss
     */
    @RequestMapping(value = "callback", method = {RequestMethod.POST})
    public @ResponseBody String chargeNotify(@ModelAttribute ChargeNotifyRequest chargeNotifyRequest) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String sessionId = attributes.getSessionId();

        if (chargeNotifyRequest == null) {
            LOGGER.info("sessionId={},通知请求参数chargeNotifyDto==null,不进行处理。", sessionId);
            return NotifyConstant.NOTIFY_RETURN_FAIL;
        }
        LOGGER.info("sessionId={},充值结果通知处理开始,请求入参:chargeNotifyRequest={}", sessionId, chargeNotifyRequest.toString());
        // 获取详细通知参数
        String orderStatus = chargeNotifyRequest.getOrder_status();
        String channelOrderId = chargeNotifyRequest.getChannel_orderid();
        String platOrderId = chargeNotifyRequest.getPlat_orderid();
        String finishTime = chargeNotifyRequest.getFinish_time();

        if (StringUtils.isBlank(orderStatus) ||
                StringUtils.isBlank(channelOrderId) ||
                StringUtils.isBlank(platOrderId) ||
                StringUtils.isBlank(finishTime)) {
            LOGGER.info("sessionId={},通知请求参数存在空值,不进行处理。", sessionId);
            return NotifyConstant.NOTIFY_RETURN_FAIL;
        }
        ThreadLocal
        // 签名校验
        String originSign = chargeNotifyRequest.getSign();
        String localSign = chargeNotifyRequest.sign(privateKey);
        if (!localSign.equals(originSign)) {
            LOGGER.info("sessionId={},签名校验失败,不进行处理。originSign={},localSign={}", sessionId, originSign, localSign);
            return NotifyConstant.NOTIFY_RETURN_FAIL;
        }
        LOGGER.info("sessionId={},签名校验成功,准备进行通知处理。originSign={},localSign={}", sessionId, originSign, localSign);
        // 执行通知处理逻辑
        return orderService.doNotify(sessionId, orderStatus, channelOrderId, platOrderId, finishTime);
    }

}
