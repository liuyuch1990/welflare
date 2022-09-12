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
public class WspIndexPageETO {

    /**
    * id
    */
    private String indexId;

    /**
    * 首页文字
    */
    private String indexText;



}