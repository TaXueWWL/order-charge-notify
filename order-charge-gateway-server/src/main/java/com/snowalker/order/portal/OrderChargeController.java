package com.snowalker.order.portal;

import com.snowalker.order.common.dto.Result;
import com.snowalker.order.charge.request.ChargeOrderRequest;
import com.snowalker.order.common.service.OrderChargeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/10 16:13
 * @className OrderChargeController
 * @desc 订单充值接口
 */
@Controller
@RequestMapping("api")
public class OrderChargeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderChargeController.class);

    @Resource(name = "orderChargeService")
    OrderChargeService orderChargeService;

    /**
     * 平台下单接口
     * @param chargeOrderRequest
     * @return
     */
    @RequestMapping(value = "charge.do", method = {RequestMethod.POST})
    public @ResponseBody Result chargeRequst(@RequestBody ChargeOrderRequest chargeOrderRequest) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String sessionId = attributes.getSessionId();
        // 下单前置校验
        if (!orderChargeService.checkValidBeforeChargeOrder(chargeOrderRequest, sessionId)) {
            return new Result("20000", "FAIL", null);
        }
        return orderChargeService.sendPaymentTransactionMsg(chargeOrderRequest, sessionId);
    }

}
