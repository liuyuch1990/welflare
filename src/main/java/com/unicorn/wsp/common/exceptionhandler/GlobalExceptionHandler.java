package com.unicorn.wsp.common.exceptionhandler;


import com.unicorn.wsp.common.exception.BusinessRollbackException;
import com.unicorn.wsp.common.exception.ParamException;
import com.unicorn.wsp.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;



/**
 * 全局异常处理
 * */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @Value("${exceptionHandler}")
    private int exceptionHandler;

    /**
     * 表单校验抛出ParamException
     */
    @ExceptionHandler(ParamException.class)
    @ResponseBody
    public ResultVo handleValidation(ParamException e){
      // log.error(e.getMessage(), e);
      return ResultVo.argsError(e.getMessage());
    }

    /**
     * 用于业务流程的回滚抛出的异常
     */
    @ExceptionHandler(BusinessRollbackException.class)
    @ResponseBody
    public ResultVo handleBusinessRollback(BusinessRollbackException e){
        // log.error(e.getMessage(), e);
        return ResultVo.failed(e.getMessage());
    }


    /**
     * 尝试所有运行时异常都在这处理
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResultVo handleRuntimeException(Exception e, Throwable cause){

        StackTraceElement stackTraceElement = e.getStackTrace()[0];
        String errorMsg =
                "\r\n异常： " + e.getClass().getName()+
                "\r\n文件名："+stackTraceElement.getFileName()+
                "\r\n类名："+stackTraceElement.getClassName()+
                "\r\n方法名："+stackTraceElement.getMethodName()+
                "\r\n抛出异常行号："+stackTraceElement.getLineNumber()+
                "\r\n异常信息："+ e.getMessage();

        log.error(errorMsg);
        if(exceptionHandler == 1) e.printStackTrace();

        return ResultVo.handleException("发生了异常: " + errorMsg);
    }



}
