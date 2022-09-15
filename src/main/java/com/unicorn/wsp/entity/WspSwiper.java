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
@TableName(value = "wsp_swiper", autoResultMap = true)
public class WspSwiper extends BaseEntity {

    @Excel(columnNum = 0)
    private String id;

    @Excel(columnNum = 1)
    private String image;

    @Excel(columnNum = 2)
    private String imagePath;

    @Excel(columnNum = 3)
    private Integer sort;
}
