package com.unicorn.wsp.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryCardTypeVo {

    // 礼品卡种类名
    String cardTypeName;
    // 公司num
    String comNum;
    // 礼品卡种类id
    String cardTypeId;
    // 商品种类额度
    String typeQuota;
    // 商品种类
    String goodsType;

}
