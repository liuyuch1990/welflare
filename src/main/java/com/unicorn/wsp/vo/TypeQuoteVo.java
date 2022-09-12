package com.unicorn.wsp.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeQuoteVo {

    // 前端传参 -> goodType
    private String dictCode;

    private String goodsType;
    private int typeQuota;

}
