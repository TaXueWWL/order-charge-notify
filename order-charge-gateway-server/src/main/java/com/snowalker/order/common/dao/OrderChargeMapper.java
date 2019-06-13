package com.snowalker.order.common.dao;

import com.snowalker.order.common.dao.dataobject.OrderInfoDO;
import com.snowalker.order.common.dao.dataobject.OrderInfoDobj;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/11 20:01
 * @className OrderChargeMapper
 * @desc 订单交易Mapper
 */
public interface OrderChargeMapper {

    /**
     * 订单入库
     * @param orderInfoDO
     * @return
     */
    int insertOrder(OrderInfoDO orderInfoDO);

    /**
     * 订单查询
     * @param orderInfoDO
     * @return
     */
    OrderInfoDobj queryOrderInfoByOrderId(OrderInfoDO orderInfoDO);

    /**
     * 订单状态修改为处理中
     * @param orderInfoDO
     * @return
     */
    int updateOrderDealing(OrderInfoDO orderInfoDO);

    /**
     * 支付状态修改为成功
     * @param orderInfoDO
     * @return
     */
    int updateOrderPayStatusSucc(OrderInfoDO orderInfoDO);

    /**
     * 通知状态改成功
     * @param orderInfoDO
     * @return
     */
    int updateOrderNotifyStatusSucc(OrderInfoDO orderInfoDO);
}
