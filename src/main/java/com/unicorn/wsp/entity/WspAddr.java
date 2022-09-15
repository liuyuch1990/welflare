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
@TableName(value = "wsp_addr", autoResultMap = true)
public class WspAddr extends BaseEntity {

    @TableId("addr_id")
    @Excel(columnNum = 0)
    private Integer addrId;

    @Excel(columnNum = 1)
    private String addrName;

    @Excel(columnNum = 2)
    private String receiverName;

    @Excel(columnNum = 3)
    private String receiverPhone;

    @Excel(columnNum = 4)
    private String provinceCode;

    @Excel(columnNum = 5)
    private String cityCode;

    @Excel(columnNum = 6)
    private String areaCode;

    @Excel(columnNum = 7)
    private String areaName;

    @Excel(columnNum = 8)
    private String addrContent;

    @Excel(columnNum = 9)
    private String isDef;

    @Excel(columnNum = 10)
    private String userId;

    @Excel(columnNum = 11)
    private String userNo;

    @Excel(columnNum = 12)
    private String userName;



}