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

import javax.validation.constraints.NotEmpty;

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
@TableName(value = "wsp_index_page", autoResultMap = true)
public class WspIndexPage extends BaseEntity {


    @Excel(columnNum = 0)
    private String indexId;

    @NotEmpty(message = "文字描述不能为空")
    @Excel(columnNum = 1)
    private String indexText;

    @NotEmpty(message = "登录图不能为空")
    @Excel(columnNum = 2)
    private String loginPic;

    @NotEmpty(message = "轮播图不能为空")
    @Excel(columnNum = 3)
    private String indexPic1;

    @Excel(columnNum = 4)
    private String indexPic2;

    @Excel(columnNum = 5)
    private String indexPic3;

    @Excel(columnNum = 6)
    private String indexPic4;

    @Excel(columnNum = 7)
    private String indexPic5;

    @Excel(columnNum = 8)
    private String comNum;



}