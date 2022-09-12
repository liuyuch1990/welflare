package com.unicorn.wsp.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

@Slf4j
public class ObjectFieldUtils {

    private ObjectFieldUtils() {
    }

    /**
     * 判断类中每个属性是否都为空
     * （对父类无效）
     */
    public static boolean allFieldIsNULL(Object o){
        try {
            for (Field field : o.getClass().getDeclaredFields()) {
                field.setAccessible(true);

                Object object = field.get(o);
                if (object instanceof CharSequence) {
                    if (!org.springframework.util.ObjectUtils.isEmpty(object)) {
                        return false;
                    }
                } else {
                    if (null != object) {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            log.error("判断对象属性为空异常", e);

        }
        return true;
    }


    /**
     * 判断类中每个属性是否都为空(排除指定字段)
     * （对父类无效）
     */
    public static boolean allFieldIsNULL(Object o, String ...excludeFieldNames){
        try {
            a:for (Field field : o.getClass().getDeclaredFields()) {
                field.setAccessible(true);

                // 排除字段
                for(String name:excludeFieldNames){
                    if(field.getName().equals(name)) continue a;
                }

                Object object = field.get(o);
                if (object instanceof CharSequence) {
                    if (!org.springframework.util.ObjectUtils.isEmpty(object)) {
                        return false;
                    }
                } else {
                    if (null != object) {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            log.error("判断对象属性为空异常", e);
        }
        return true;
    }


    /**
     * 判断类中指定字段是否全部为空
     * （对父类无效）
     */
    public static boolean allSpecifiedFieldIsEmpty(Object o, String ...specifiedFieldNames){
        try {
            for (Field field : o.getClass().getDeclaredFields()) {
                field.setAccessible(true);

                // 如果该字段不在指定字段中，跳至下一循环
                int count = 0;
                for(String name:specifiedFieldNames){
                    if(field.getName().equals(name)) count++;
                }
                if(count==0) continue;

                Object object = field.get(o);
                if (object instanceof CharSequence) {
                    if (!org.springframework.util.ObjectUtils.isEmpty(object)) {
                        return false;
                    }
                } else {
                    if (null != object) {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            log.error("判断对象属性为空异常", e);
        }
        return true;
    }


    /**
     * 判断类中是否存在空属性
     *（对父类无效）
     */
    public static boolean isExistEmptyField(Object o){
        try {
            for (Field field : o.getClass().getDeclaredFields()) {
                field.setAccessible(true);

                Object object = field.get(o);
                if (object instanceof CharSequence) {
                    if (org.springframework.util.ObjectUtils.isEmpty(object)) {
                        return true;
                    }
                } else {
                    if (null == object) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            log.error("判断对象属性为空异常", e);
        }
        return false;
    }

    /**
     * 判断类中是否存在空属性(排除指定字段)
     * （对父类无效）
     */
    public static boolean isExistEmptyField(Object o, String ...excludeFieldNames){
        try {
            a:for (Field field : o.getClass().getDeclaredFields()) {
                field.setAccessible(true);

                // 排除字段
                for(String name:excludeFieldNames){
                    if(field.getName().equals(name)) continue a;
                }

                Object object = field.get(o);
                if (object instanceof CharSequence) {
                    if (org.springframework.util.ObjectUtils.isEmpty(object)) {
                        return true;
                    }
                } else {
                    if (null == object) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            log.error("判断对象属性为空异常", e);
        }
        return false;
    }

    /**
     * 判断类中指定字段中是否存在空值
     * （对父类无效）
     */
    public static boolean isExistSpecifiedFieldIsEmpty(Object o, String ...specifiedFieldNames){
        try {
            for (Field field : o.getClass().getDeclaredFields()) {
                field.setAccessible(true);

                // 如果该字段不在指定字段中，跳至下一循环
                int count = 0;
                for(String name:specifiedFieldNames){
                    if(field.getName().equals(name)) count++;
                }
                if(count==0) continue;

                Object object = field.get(o);
                if (object instanceof CharSequence) {
                    if (org.springframework.util.ObjectUtils.isEmpty(object)) {
                        return true;
                    }
                } else {
                    if (null == object) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            log.error("判断对象属性为空异常", e);
        }
        return false;
    }

}