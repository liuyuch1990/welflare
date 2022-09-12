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
@TableName(value = "wsp_goods_pic", autoResultMap = true)
public class WspGoodsPic extends BaseEntity {


    @Excel(columnNum = 0)
    @TableId(value = "pic_id", type = IdType.INPUT)//非自增
    private String picId;

    @Excel(columnNum = 1)
    private String picSavepath;

    @Excel(columnNum = 2)
    private String picRealname;

    @Excel(columnNum = 3)
    private String isCover;

    @Excel(columnNum = 4)
    private String picSort;

    @Excel(columnNum = 5)
    private String goodsId;



}