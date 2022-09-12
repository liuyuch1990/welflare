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
public class CommonCityETO {

    /**
    * 
    */
    private Integer id;

    /**
    * 
    */
    private String code;

    /**
    * 
    */
    private String name;

    /**
    * 
    */
    private String provincecode;



}