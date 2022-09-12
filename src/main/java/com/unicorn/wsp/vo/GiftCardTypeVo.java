package com.unicorn.wsp.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GiftCardTypeVo {

    String cardTypeId;

    String cardTypeName;

    List<TypeQuoteVo> typeQuotaVoList;

    String comNum;
}
