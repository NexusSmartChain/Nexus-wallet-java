package com.chain.common.exception.handler;

import com.chain.common.exception.BadRequestException;
import com.chain.common.exception.EntityExistException;
import com.chain.common.exception.EntityNotFoundException;
import com.chain.common.exception.ThrowableUtil;
import com.chain.common.ret.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * @author Zheng Jie
 * @date 2018-11-23
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理所有不可知的异常
     */
    @ExceptionHandler(Throwable.class)
    public JsonResult handleException(Throwable e){
        // 打印堆栈信息
        log.error(ThrowableUtil.getStackTrace(e));
        return buildResponseEntity(ApiError.error(e.getMessage()));
    }

//    /**
//     * BadCredentialsException
//     */
//    @ExceptionHandler(BadCredentialsException.class)
//    public JsonResult badCredentialsException(BadCredentialsException e){
//        // 打印堆栈信息
//        String message = "坏的凭证".equals(e.getMessage()) ? "用户名或密码不正确" : e.getMessage();
//        log.error(message);
//        return buildResponseEntity(ApiError.error(message));
//    }

    /**
     * 处理自定义异常
     */
	@ExceptionHandler(value = BadRequestException.class)
	public JsonResult badRequestException(BadRequestException e) {
        // 打印堆栈信息
        log.error(ThrowableUtil.getStackTrace(e));
        return buildResponseEntity(ApiError.error(e.getStatus(),e.getMessage()));
	}

    /**
     * 处理 EntityExist
     */
    @ExceptionHandler(value = EntityExistException.class)
    public JsonResult entityExistException(EntityExistException e) {
        // 打印堆栈信息
        log.error(ThrowableUtil.getStackTrace(e));
        return buildResponseEntity(ApiError.error(e.getMessage()));
    }

    /**
     * 处理 EntityNotFound
     */
    @ExceptionHandler(value = EntityNotFoundException.class)
    public JsonResult entityNotFoundException(EntityNotFoundException e) {
        // 打印堆栈信息
        log.error(ThrowableUtil.getStackTrace(e));
        return buildResponseEntity(ApiError.error(NOT_FOUND.value(),e.getMessage()));
    }

    /**
     * 处理所有接口数据验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public JsonResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        // 打印堆栈信息
        log.error(ThrowableUtil.getStackTrace(e));
        String[] str = Objects.requireNonNull(e.getBindingResult().getAllErrors().get(0).getCodes())[1].split("\\.");
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        String msg = "不能为空";
        if(msg.equals(message)){
            message = str[1] + ":" + message;
        }
        return buildResponseEntity(ApiError.error(message));
    }

    /**
     * 统一返回
     */
    private JsonResult buildResponseEntity(ApiError apiError) {
        return JsonResult.result(apiError.getData()+"",apiError.getCode(),apiError.getMsg());
    }
}
