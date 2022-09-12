package com.unicorn.wsp.vo;

import com.unicorn.wsp.entity.WspOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditLogisticsVo extends WspOrder {

    List<SubEditLogisticsVo> logisticList;

}

