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
public class DemandConfigETO {

    /**
    * 1:北方华创，礼品卡下单只能选一个类型
    */
    private Integer configType;

    /**
    * 
    */
    private Integer id;



}