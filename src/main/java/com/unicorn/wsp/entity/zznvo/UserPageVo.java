package com.unicorn.wsp.entity.zznvo;

import com.unicorn.wsp.entity.WspUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPageVo extends WspUser {

    Integer pageNum = 1;
    Integer pageSize = 20;
}
