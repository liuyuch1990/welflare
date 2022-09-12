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
public class WspComETO {

    /**
    * 
    */
    private Integer id;

    /**
    * 公司编号
    */
    private String comNum;

    /**
    * 公司名
    */
    private String comName;


    private String goodsType;



}