package com.snowalker.gateway.merchant.order.handler;

import com.alibaba.fastjson.JSON;
import com.snowalker.gateway.merchant.order.dto.CodeMsg;
import com.snowalker.gateway.merchant.order.dto.Result;
import com.snowalker.gateway.merchant.order.exception.ApiException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author snowalker
 * 全局异常解析
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public Result<String> exceptionHandler(Exception e){
        if (e instanceof ApiException) {
            ApiException apiException = (ApiException) e;
            return Result.error(apiException.getCodeMsg());
        } else if(e instanceof BindException) {
            BindException ex = (BindException)e;
            List<ObjectError> errors = ex.getAllErrors();
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
        } else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }
}