package com.chain.common.exception.handler;

import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

/**
 * @author Zheng Jie
 * @date 2018-11-23
 */
@Data
class ApiError {

    private T data;
    private String code = "400";
    private String msg;

    private ApiError() {
    }

    public static ApiError error(String message){
        ApiError apiError = new ApiError();
        apiError.setMsg(message);
        return apiError;
    }

    public static ApiError error(Integer status, String message){
        ApiError apiError = new ApiError();
        apiError.setCode(status+"");
        apiError.setMsg(message);
        return apiError;
    }
}


