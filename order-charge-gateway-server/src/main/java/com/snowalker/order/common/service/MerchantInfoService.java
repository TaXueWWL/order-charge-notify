package com.snowalker.order.common.service;

import com.snowalker.order.common.dao.dataobject.MerchantInfoDO;

import java.util.List;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/11 14:13
 * @className MerchantInfoService
 * @desc 商户服务接口
 */
public interface MerchantInfoService {

    /**
     * 查询商户列表
     * @return
     */
    List<MerchantInfoDO> queryMerchantList();
}
