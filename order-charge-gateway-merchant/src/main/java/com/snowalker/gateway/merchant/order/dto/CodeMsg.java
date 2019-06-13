package com.snowalker.gateway.merchant.order.dto;

/**
 * @author snowalker
 * 错误码封装
 */
public class CodeMsg {

    private String code;
    private String msg;

    /**通用的错误码*/
    public static CodeMsg SUCCESS = new CodeMsg("10000", "SUCCESS");
    public static CodeMsg UNKNOWN_ERROR = new CodeMsg("40004", "UNKNOWN_ERROR");
    public static CodeMsg SERVER_ERROR = new CodeMsg("40004", "服务端异常");
    public static CodeMsg BIND_ERROR = new CodeMsg("40006", "参数校验异常:s%");
    /**订单入库失败*/
    public static CodeMsg INSERT_ORDER_ERROR = new CodeMsg("40004", "订单入库异常");
    public static CodeMsg UPDATE_ORDER_ERROR = new CodeMsg("40004", "订单入库异常");
    public static CodeMsg CHARGE_ORDER_FAIL = new CodeMsg("20000", "下单失败");
    /**参数非法*/
    public static final String PARAM_ILLEGAL = "40006";
    /**产品不存在*/
    public static final String PRODUCT_NOT_EXIST = "40007";
    /**库存不足*/
    public static final String PRODUCT_STOCK_NOT_ENOUGH = "40008";

    public CodeMsg( ) {
    }

    public CodeMsg(String code, String msg ) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public CodeMsg setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public CodeMsg setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public CodeMsg fillArgs(Object ... args) {
        String code = this.code;
        String message = String.format(this.msg, args);
        return new CodeMsg(code, message);
    }

    @Override
    public String toString() {
        return "CodeMsg [code=" + code + ", msg=" + msg + "]";
    }

}