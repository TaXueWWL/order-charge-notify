package com.snowalker.gateway.merchant.order.exception;

import com.snowalker.gateway.merchant.order.dto.CodeMsg;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/10 10:15
 * @className ApiException
 * @desc 自定义接口异常
 */
public class ApiException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private CodeMsg codeMsg;

    public ApiException(CodeMsg codeMsg) {
        super(codeMsg.toString());
        this.codeMsg = codeMsg;
    }

    public ApiException() {}

    public CodeMsg getCodeMsg() {
        return codeMsg;
    }
}
