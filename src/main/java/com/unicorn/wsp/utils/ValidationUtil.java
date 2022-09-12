package com.unicorn.wsp.utils;

import com.unicorn.wsp.common.exception.ParamException;
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

}
