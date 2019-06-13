package com.snowalker.order.common.service.impl;

import com.snowalker.order.common.dao.MerchantInfoMapper;
import com.snowalker.order.common.dao.dataobject.MerchantInfoDO;
import com.snowalker.order.common.service.MerchantInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/11 14:21
 * @className MerchantInfoServiceImpl
 * @desc 商户信息服务实现
 */
@Service(value = "merchantInfoService")
public class MerchantInfoServiceImpl implements MerchantInfoService {

    @Autowired
    MerchantInfoMapper merchantInfoMapper;

    /**
     * 商户列表查询
     * @return
     */
    @Override
    public List<MerchantInfoDO> queryMerchantList() {
        return merchantInfoMapper.queryMerchantList();
    }
}
