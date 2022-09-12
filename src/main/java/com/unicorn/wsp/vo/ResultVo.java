package com.unicorn.wsp.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultVo<T> {

    private int resultCode;
    private String message;
    private T data;



    private static final int SUCCESS_CODE = 20000;      // 成功
    private static final int NO_AUTH_CODE = 30000;      // 没有权限
    private static final int ARGS_ERROR_CODE = 40000;   // 参数错误
    private static final int ERROR_CODE = 50000;        // 系统错误
    private static final int EXCEPTION_CODE  = 50001;    // 异常

    private static final int BALANCE_BE_ENOUGH_CODE = 70000;   // 余额富余


    private static final String SUCCESS_MSG = "操作成功";
    private static final String FAIL_MSG = "操作失败";
    private static final String EXCEPTION_MSG = "An exception occurred";



    // 成功
    public static <T> ResultVo<T> success(T data) {
        return new ResultVo<>(SUCCESS_CODE, SUCCESS_MSG, data);
    }
    // 成功默認
    public static <T> ResultVo<T> success() {
        return new ResultVo(SUCCESS_CODE, SUCCESS_MSG, "");
    }

    // 失败
    public static ResultVo failed(String message) {
        return new ResultVo(ERROR_CODE, message, "");
    }
    // 失敗默認
    public static ResultVo failed() {
        return new ResultVo(ERROR_CODE, FAIL_MSG, "");
    }

    // 异常
    public static <T> ResultVo handleException(String message) {
        return new ResultVo(EXCEPTION_CODE, message, "");
    }
    // 異常默認
    public static <T> ResultVo handleException() {
        return new ResultVo(EXCEPTION_CODE, EXCEPTION_MSG, "");
    }

    // 参数错误
    public static ResultVo argsError(String message) {
        return new ResultVo(ARGS_ERROR_CODE, message, "");
    }

    // 没有权限
    public static ResultVo noAuth(String message){
        return new ResultVo(NO_AUTH_CODE, message, "");
    }


    // 余额富余
    public static <T>ResultVo BALANCE_BE_ENOUGH(String message) {
        return new ResultVo(BALANCE_BE_ENOUGH_CODE, message,"");
    }






}
