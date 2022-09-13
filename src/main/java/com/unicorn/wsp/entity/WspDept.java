package com.unicorn.wsp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.unicorn.wsp.common.annotation.Excel;
import com.unicorn.wsp.common.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "wsp_dept", autoResultMap = true)
public class WspDept extends BaseEntity {

    @Excel(columnNum = 0)
    private Integer id;

    @Excel(columnNum = 1)
    private String departmentName;

    @Excel(columnNum = 2)
    private String companyName;
}
