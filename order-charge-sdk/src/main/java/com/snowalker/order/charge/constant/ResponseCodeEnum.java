package com.snowalker.order.charge.constant;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/10 11:49
 * @className OrderStatusEnum
 * @desc 同步下单及异步回调状态码枚举
 */
public enum ResponseCodeEnum {

    SUCCESS("10000", "成功"),
    FAIL("20000", "失败"),
    UNKNOWN("40004", "未知");

    private String code;
    private String desc;

    ResponseCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
