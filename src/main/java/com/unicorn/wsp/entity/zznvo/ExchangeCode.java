package com.unicorn.wsp.entity.zznvo;



import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeCode {

    @Excel(name = "兑换码", orderNum = "0")
    String exchangeCode;

    @Excel(name = "有效期", orderNum = "1",importFormat = "yyyy/MM/dd")
    Date valid_date;

    @Excel(name = "额度系数", orderNum = "2")
    Integer quotaMultiple;


}
