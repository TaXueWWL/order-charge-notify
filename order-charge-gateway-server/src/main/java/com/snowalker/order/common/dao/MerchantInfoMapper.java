package com.snowalker.order.common.dao;

import com.snowalker.order.common.dao.dataobject.MerchantInfoDO;

import java.util.List;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/11 14:06
 * @className MerchantInfoMapper
 * @desc 商户基本信息Mapper
 */
public interface MerchantInfoMapper {

    /**
     * 查询商户信息列表
     * @return
     */
    List<MerchantInfoDO> queryMerchantList();
}
