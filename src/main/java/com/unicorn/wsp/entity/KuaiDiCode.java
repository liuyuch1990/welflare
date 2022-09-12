package com.unicorn.wsp.entity;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;


/**
 * 快递100 智能识别返回结果 List<KuaiDiCode 按相似度排序（高的在前）
 * @author spark
 * @version 1.0.0
 */

@Data
@ToString
public class KuaiDiCode {

    private String comCode;
    private String id;
    private Integer noCount;
    private String noPre;
    private LocalDateTime startTime;

}


