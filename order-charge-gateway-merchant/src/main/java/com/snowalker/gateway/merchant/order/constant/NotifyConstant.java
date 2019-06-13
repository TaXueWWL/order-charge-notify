package com.snowalker.gateway.merchant.order.constant;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/11 12:54
 * @className NotifyConstant
 * @desc
 */
public abstract class NotifyConstant {

    private NotifyConstant() {}

    /**通知结果返回*/
    public static final String NOTIFY_RETURN_SUCC = "T";
    public static final String NOTIFY_RETURN_FAIL = "F";
    /**订单结果通知状态*/
    public static final String NOTIFY_SUCCESS = "SUCCESS";
    public static final String NOTIFY_FAIL = "FAIL";
}
