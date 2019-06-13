package com.snowalker.gateway.merchant.order;

import com.alibaba.fastjson.JSON;
import com.snowalker.gateway.merchant.order.dto.CodeMsg;
import com.snowalker.gateway.merchant.order.dto.Result;
import com.snowalker.gateway.merchant.order.handler.RequestParamValidateHandler;
import com.snowalker.gateway.merchant.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/10 10:06
 * @className OrderChargeController
 * @desc 商户下单接口
 */
@Controller
@RequestMapping(value = "api")
public class OrderChargeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderChargeController.class);

    @Autowired
    RequestParamValidateHandler requestParamValidateHandler;

    @Resource(name = "orderService")
    OrderService orderService;

    /**
     * 第三方渠道下单
     * @param request
     */
    @RequestMapping(value = "charge", method = {RequestMethod.POST})
    public @ResponseBody Result chargeRequst(HttpServletRequest request) {

        String phoneNum = request.getParameter("phone_num");
        String chargeMoney = request.getParameter("charge_money");
        String prodId = request.getParameter("prod_id");

        Result result = new Result("", "");
        // 前置校验：校验参数、产品、库存等
        if (requestParamValidateHandler.checkRequestParams(phoneNum, chargeMoney, prodId)) {
            LOGGER.info("下单接口入参:phoneNum={},chargeMoney={},prodId={}", phoneNum, chargeMoney, prodId);
            result = orderService.chargeOrder(phoneNum, chargeMoney, prodId);
        } else {
            result = Result.error(CodeMsg.UNKNOWN_ERROR);
        }
        LOGGER.info("下单接口出参:result={}", JSON.toJSONString(result));
        return result;
    }


}
