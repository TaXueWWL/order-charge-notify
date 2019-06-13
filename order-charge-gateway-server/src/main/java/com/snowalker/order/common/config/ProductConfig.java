package com.snowalker.order.common.config;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/10 9:41
 * @className GoodsConfig
 * @desc 商品配置
 *         prod_id:BJ_SY_JJC_01
 *         prod_name:南航北京-三亚经济舱
 *         prod_stock:999
 */
@Configuration
public class ProductConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductConfig.class);

    private static final Map<String, ProductVO> PRODUCTS_MAP = new ConcurrentHashMap<>(16);

    @PostConstruct
    public void init() {
        ProductVO productVO = new ProductVO();
        String productId = "BJ_SY_JJC_01";
        productVO.setProductId(productId)
                .setProductName("南航北京-三亚经济舱")
                .setProductStock(200);
        PRODUCTS_MAP.put(productId, productVO);
        LOGGER.info("平台商品配置加载完毕，productConfig={}", JSON.toJSONString(PRODUCTS_MAP));
    }

    public static Map<String, ProductVO> getProductsMap() {
        return PRODUCTS_MAP;
    }
}
