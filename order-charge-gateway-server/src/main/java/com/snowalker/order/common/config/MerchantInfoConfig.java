package com.snowalker.order.common.config;

import com.alibaba.fastjson.JSON;
import com.snowalker.order.common.dao.dataobject.MerchantInfoDO;
import com.snowalker.order.common.service.MerchantInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/11 14:10
 * @className MerchantInfoConfig
 * @desc
 */
@Component
public class MerchantInfoConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantInfoConfig.class);

    private static final Map<String, MerchantInfoDO> MERCHANTS_MAP = new ConcurrentHashMap<>(16);

    @Resource(name = "merchantInfoService")
    MerchantInfoService merchantInfoService;

    @PostConstruct
    public void init() {

        List<MerchantInfoDO> list = merchantInfoService.queryMerchantList();
        if (list != null && list.size() > 0) {
            list.stream().forEach((merchantInfoDO -> {
                String key = merchantInfoDO.getMerchantPurseId();
                MERCHANTS_MAP.put(key, merchantInfoDO);
            }));
            LOGGER.info("商户配置信息初始化完成,MERCHANTS_MAP={}", JSON.toJSONString(MERCHANTS_MAP));
        } else {
            LOGGER.info("商户配置信息为空, 请及时添加");
        }
    }

    public static Map<String, MerchantInfoDO> getMerchantsMap() {
        return MERCHANTS_MAP;
    }
}
