package com.snowalker.gateway.merchant.order.handler;

import com.snowalker.gateway.merchant.goods.ProductConfig;
import com.snowalker.gateway.merchant.goods.ProductVO;
import com.snowalker.gateway.merchant.order.dto.CodeMsg;
import com.snowalker.gateway.merchant.order.exception.ApiException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/10 10:28
 * @className RequestParamValidateHandler
 * @desc 请求参数校验
 */
@Component
public class RequestParamValidateHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestParamValidateHandler.class);

    /**
     * 下单请求参数校验
     * @param phoneNum
     * @param chargeMoney
     * @param prodId
     */
    public boolean checkRequestParams(String phoneNum, String chargeMoney, String prodId) {
        // 非空校验
        if (StringUtils.isBlank(phoneNum) || StringUtils.isBlank(chargeMoney) || StringUtils.isBlank(prodId)) {
            String errorMsg = String.format("下单请求入参存在空值, phoneNum=%s, chargeMoney=%s, prodId=%s", phoneNum, chargeMoney, prodId);
            LOGGER.error(errorMsg);
            throw new ApiException(new CodeMsg(CodeMsg.PARAM_ILLEGAL, errorMsg));
        }
        // 产品校验
        ProductVO productVO = ProductConfig.getProductsMap().get(prodId);
        if (productVO == null) {
            String errorMsg = String.format("产品不存在!, prodId=%s", prodId);
            LOGGER.error(errorMsg);
            throw new ApiException(new CodeMsg(CodeMsg.PRODUCT_NOT_EXIST, errorMsg));
        }
        // 库存校验
        int productStock = productVO.getProductStock();
        if (productStock <= 0) {
            String errorMsg = String.format("产品库存不足!, prodId=%s", prodId);
            LOGGER.error(errorMsg);
            throw new ApiException(new CodeMsg(CodeMsg.PRODUCT_STOCK_NOT_ENOUGH, errorMsg));
        }
        return true;
    }
}
