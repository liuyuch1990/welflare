package com.unicorn.wsp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.unicorn.wsp.common.annotation.Excel;
import com.unicorn.wsp.common.base.BaseEntity;

/**
* 
*
* @Author spark
* @Version 1.0
*/
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "wsp_gift_card", autoResultMap = true)
public class WspGiftCard extends BaseEntity {


    @TableId("gift_card_id")
    @Excel(columnNum = 0)
    private Integer giftCardId;

    @Excel(columnNum = 1)
    private String giftCardNum;

    @Excel(columnNum = 2)
    private String cardTypeId;

    @Excel(columnNum = 3)
    private Integer quotaMultiple;

    @Excel(columnNum = 4)
    private String validDate;

    @Excel(columnNum = 5)
    private String isTrue;

    @Excel(columnNum = 6)
    private String isUse;

    @Excel(columnNum = 7)
    private String userId;

    @Excel(columnNum = 8)
    private String userPhone;

    @Excel(columnNum = 9)
    private String comNum;

    @Excel(columnNum = 10)
    private String isDel;



}