package com.snowalker.gateway.merchant.goods;

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
 *         prod_id:0001
 *         prod_name:南方航空北京-三亚经济舱
 *         prod_stock:100
 *         prod_out_id:BJ_SY_JJC_01
 *         prod_out_name:南航北京-三亚经济舱
 *
 *         prod_id:BJ_SY_JJC_01
 *         prod_name:南航北京-三亚经济舱
 *         prod_stock:999
 */
@Configuration
public class ProductConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductConfig.class);

    private static final Map<String, ProductVO> PRODUCTS_MAP = new ConcurrentHashMap<>(16);

    /**渠道id：南方航空*/
    public static final String CHANNEL_ID_CHINA_CSAIR = "CHINA_CSAIR";

    @PostConstruct
    public void init() {
        ProductVO productVO = new ProductVO();
        String productId = "0001";
        productVO.setProductId(productId)
                .setProductName("南方航空北京-三亚经济舱")
                .setProductOutId("BJ_SY_JJC_01")
                .setProductOutName("南航北京-三亚经济舱")
                .setProductStock(100)
                .setProductChannelId(CHANNEL_ID_CHINA_CSAIR);
        PRODUCTS_MAP.put(productId, productVO);
        LOGGER.info("渠道商品配置加载完毕，productConfig={}", JSON.toJSONString(PRODUCTS_MAP));
    }

    public static Map<String, ProductVO> getProductsMap() {
        return PRODUCTS_MAP;
    }
}
