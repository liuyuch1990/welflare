package com.unicorn.wsp.utils;

import com.unicorn.wsp.common.exception.ParamException;
import com.unicorn.wsp.vo.OrderQueryVo;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class ValidationUtil {

    /*
    * 静态代码块初始化ValidatorFactory，得到validator
    * */
    private static Validator validator;
    static{
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


    public static  void validateObject(Object object){
        Set<ConstraintViolation<Object>> violations = validator.validate(object);
        if(!CollectionUtils.isEmpty(violations)){

            StringBuffer errorMsgBuf = new StringBuffer();

            // 遍历拼接校验报错信息
            for(ConstraintViolation violation : violations){

                // 错误的校验消息
                String message = violation.getMessage();

                errorMsgBuf.append(message).append(";");
            }
            throw new ParamException(errorMsgBuf.toString());
        }
    }

    public static String transferStatus(String status){
        String temp = "";
        if (status.equals("0")){
           temp = "待发货";
        }
        if (status.equals("1")){
           temp = "已发货";
        }
        if (status.equals("2")){
           temp = "已取消";
        }
        if (status.equals("3")){
           temp = "待退货";
        }
        if (status.equals("4")){
           temp = "待换货";
        }
        if (status.equals("5")){
           temp = "已退货";

        }
        if (status.equals("6")){
           temp = "已换货";
        }
        return temp;
    }


}
