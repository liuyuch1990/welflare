package com.unicorn.wsp.entity.zznvo;

import com.unicorn.wsp.entity.vo.WspCardQuotaVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NegativeOrZero;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WspCardQuotaPageVo extends WspCardQuotaVO {
    Integer PageNum;
    Integer PageSize;
    String comNum;
    String cardTypeName;
}