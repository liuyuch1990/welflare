package com.unicorn.wsp.vo;

import com.unicorn.wsp.entity.WspUser;
import com.unicorn.wsp.entity.zznvo.GiftCardVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public
class LoginResponseVo {
    Map<String,String> map;
    WspUser user;
    HashMap<String, Object> cardVos;

}
