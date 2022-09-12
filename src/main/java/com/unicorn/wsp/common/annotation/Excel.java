package com.unicorn.wsp.common.annotation;

import java.lang.annotation.*;

/**
 * descript
 *
 * @Author enilu
 * @Date 2021/9/18 16:47
 * @Version 1.0
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Excel {
    /**
     * excel导入列数
     * @return
     */
    int columnNum() default -1;


}
