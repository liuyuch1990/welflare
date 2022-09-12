package com.unicorn.wsp.vo;

import com.unicorn.wsp.entity.WspCardType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardTypePageVo extends WspCardType {

    Integer pageNum = 1;
    Integer pageSize = 20;

}
