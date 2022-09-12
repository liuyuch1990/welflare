package com.unicorn.wsp.entity.exportvo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
* 
*
* @Author spark
* @Version 1.0
*/
@Data
@EqualsAndHashCode(callSuper = false)
public class SimpleDictETO {

    /**
    * 字典标识
    */
    private String dictCode;

    /**
    * 字典名
    */
    private String dictName;

    /**
    * 分类
    */
    private String dictType;

    /**
    * 
    */
    private Integer id;



}