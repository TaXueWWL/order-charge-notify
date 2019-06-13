package com.snowalker.gateway.merchant.order.constant;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/10 11:49
 * @className OrderStatusEnum
 * @desc 订单状态枚举
 */
public enum OrderStatusEnum {

    SUCCESS(0, "成功"),
    INIT(1, "初始化"),
    DEALING(2, "处理中"),
    FAIL(3, "失败");

    private Integer code;
    private String desc;

    OrderStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
