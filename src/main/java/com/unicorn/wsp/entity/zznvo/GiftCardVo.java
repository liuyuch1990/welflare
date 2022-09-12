package com.unicorn.wsp.entity.zznvo;

import com.unicorn.wsp.entity.WspGiftCard;
import com.unicorn.wsp.entity.vo.WspGiftCardVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class GiftCardVo extends WspGiftCardVO {

    @ApiModelProperty(value="礼品卡种类")
    private String cardName;

}
