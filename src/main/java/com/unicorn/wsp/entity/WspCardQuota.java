package com.unicorn.wsp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName(value = "wsp_card_quota", autoResultMap = true)
public class WspCardQuota extends BaseEntity {


    @TableId("quota_id")
    @Excel(columnNum = 0)
    private Integer quotaId;

    @Excel(columnNum = 1)
    private String cardTypeId;

    @Excel(columnNum = 2)
    private Integer typeQuota;

    @Excel(columnNum = 3)
    private String goodsType;

    @Excel(columnNum = 4)
    private String isDel;



}