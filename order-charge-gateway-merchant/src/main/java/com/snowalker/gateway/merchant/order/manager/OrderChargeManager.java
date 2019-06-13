package com.snowalker.gateway.merchant.order.manager;

import com.alibaba.fastjson.JSON;
import com.snowalker.gateway.merchant.order.dto.ChargeOrderDto;
import com.snowalker.gateway.merchant.order.dto.Result;
import com.snowalker.gateway.merchant.order.util.DateUtil;
import com.snowalker.gateway.merchant.order.util.LogExceptionWapper;
import com.snowalker.order.charge.constant.ResponseCodeEnum;
import com.snowalker.order.charge.request.ChargeOrderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/10 16:39
 * @className OrderChargeManager
 * @desc 远程下单交互防腐层
 */
@Component
public class OrderChargeManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderChargeManager.class);

    @Autowired
    RestTemplate restTemplate;

    @Value("${agent.config.purseid}")
    private String purseId;

    @Value("${agent.config.merchant.name}")
    private String merchantName;

    @Value("${agent.config.private.key}")
    private String privateKey;

    @Value("${agent.config.request.url}")
    private String requestUrl;

    /**
     * 下单接口调用
     * @param chargeOrderDto
     * @return
     */
    public ResponseCodeEnum chargeRequest(ChargeOrderDto chargeOrderDto) {

        // 组装请求参数
        ChargeOrderRequest chargeOrderRequest = new ChargeOrderRequest();
        chargeOrderRequest.setChannelOrderId(chargeOrderDto.getOrderId())
                .setChargePrice(chargeOrderDto.getChargeMoney().toString())
                .setPurseId(purseId)
                .setMerchantName(merchantName)
                .setUserPhoneNum(chargeOrderDto.getPhoneNum())
                .setTimestamp(DateUtil.formatDate(new Date(System.currentTimeMillis())))
                .setProdId(chargeOrderDto.getOutProductId())
                .setSign(chargeOrderRequest.sign(privateKey));
        // 1. 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 2. 设置请求参数
        Map<String, Object> requestParam = new HashMap<>();
        requestParam.put("channelOrderId", chargeOrderRequest.getChannelOrderId());
        requestParam.put("chargePrice", chargeOrderRequest.getChargePrice());
        requestParam.put("merchantName", chargeOrderRequest.getMerchantName());
        requestParam.put("purseId", chargeOrderRequest.getPurseId());
        requestParam.put("timestamp", chargeOrderRequest.getTimestamp());
        requestParam.put("userPhoneNum", chargeOrderRequest.getUserPhoneNum());
        requestParam.put("prodId", chargeOrderRequest.getProdId());
        requestParam.put("sign", chargeOrderRequest.getSign());
        LOGGER.info("请求远端下单地址:{}, 下单入参:{}", requestUrl, requestParam.toString());
        // 3. 请求开始
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestParam, headers);
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = restTemplate.postForEntity(requestUrl, entity, String.class);
            LOGGER.info("请求远端下单地址:{}, 下单出参:{}", requestUrl, JSON.toJSONString(responseEntity));
            if (responseEntity == null) {
                LOGGER.error("请求远端下单,返回为NULL,下单地址:{},返回下单状态未知:{}", requestUrl, ResponseCodeEnum.UNKNOWN);
                return ResponseCodeEnum.UNKNOWN;
            }
            // 解析返回参
            String responseBody = responseEntity.getBody();
            // 转换返回体到对象
            Result result = JSON.parseObject(responseBody, Result.class);
            String code = result.getCode();
            String msg = result.getMsg();
            if (ResponseCodeEnum.UNKNOWN.getCode().equals(code)) {
                LOGGER.info("请求远端下单,返回下单未知,code={},msg={}", code, msg);
                return ResponseCodeEnum.UNKNOWN;
            }
            if (ResponseCodeEnum.FAIL.getCode().equals(code)) {
                LOGGER.info("请求远端下单,返回下单失败,code={},msg={}", code, msg);
                return ResponseCodeEnum.FAIL;
            }
            if (ResponseCodeEnum.SUCCESS.getCode().equals(code)) {
                LOGGER.info("请求远端下单,返回下单成功,code={},msg={}", code, msg);
                return ResponseCodeEnum.SUCCESS;
            } else {
                LOGGER.info("请求远端下单,其他未知情况,code={},msg={}", code, msg);
                return ResponseCodeEnum.UNKNOWN;
            }
        } catch (Exception e) {
            LOGGER.error("请求远端下单异常,e={}", LogExceptionWapper.getStackTrace(e));
            return ResponseCodeEnum.UNKNOWN;
        }
    }
}
