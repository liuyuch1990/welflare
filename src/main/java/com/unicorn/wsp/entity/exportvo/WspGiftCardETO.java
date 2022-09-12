package com.unicorn.wsp.entity.exportvo;

import io.swagger.annotations.ApiModelProperty;
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
public class WspGiftCardETO {

    /**
    * id
    */
    private Integer giftCardId;

    /**
    * 礼品卡编号（兑换码）
    */
    private String giftCardNum;

    /**
    * 礼品卡名
    */
    private String cardTypeId;

    /**
    * 额度倍数
    */
    private Integer quotaMultiple;

    /**
    * 有效期
    */
    private String validDate;

    /**
    * 是否有效
    */
    private String isTrue;

    /**
    * 兑换状态（未绑定0，未兑换1，已兑换2）
    */
    private String isUse;

    /**
    * 绑定人
    */
    private String userId;


    private String userPhone;

    /**
    * 公司编号
    */
    private String comNum;

    /**
    * 是否删除
    */
    private String isDel;



}