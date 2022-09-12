package com.unicorn.wsp.entity.vo;

import lombok.Data;

/**
 * @author spark
 * @date 2021/10/28
 */
@Data
public class BatchImportVo {

    private String courierNumber;

    /**
     * 物流公司
     */
    private String logisticsCompany;
}
