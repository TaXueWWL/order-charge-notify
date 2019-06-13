package com.snowalker.gateway.merchant.order.mapper;

import com.snowalker.gateway.merchant.order.dataobject.ChargeOrderDO;
import com.snowalker.gateway.merchant.order.dto.ChargeOrderDto;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/10 13:46
 * @className OrderMapper
 * @desc 订单本地Mapper
 */
public interface OrderInfoMapper {

    /**
     * 订单入库
     * @param chargeOrderDto
     * @return
     */
    int insertOrderInfo(ChargeOrderDto chargeOrderDto);

    /**
     * 订单状态更新
     * @param chargeOrderDto
     * @return
     */
    int updateOrderStatus(ChargeOrderDto chargeOrderDto);

    /**
     * 查询订单信息
     * @param chargeOrderDto
     * @return
     */
    ChargeOrderDO queryOrderByOrderId(ChargeOrderDto chargeOrderDto);
}
