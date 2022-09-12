package com.unicorn.wsp.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubEditLogisticsVo {

    // 物流单号
    private String courierNumber;

    // 物流公司
    private String logisticsCompany;

}
