package com.unicorn.wsp.entity.zznvo;

import com.unicorn.wsp.entity.WspGiftCard;
import com.unicorn.wsp.entity.vo.WspGiftCardVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GiftCardPageVo extends WspGiftCardVO {

    Integer pageSize = 1;
    Integer pageNum = 20;

}
