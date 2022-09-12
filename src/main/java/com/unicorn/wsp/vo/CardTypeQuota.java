package com.unicorn.wsp.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardTypeQuota {

    /**
     * 礼品卡种类id
     */
    String cardTypeId;

    /**
     * 公司码
     */
    String comNum;

    /**
     * 商品类型code
     */
    String goodsType;

    /**
     * 商品类型额度
     */
    Integer typeQuota;

    /**
     * 商品类型名
     */
    String dictName;

    /**
     * 礼品卡名
     * */
    String cardTypeName;

}
