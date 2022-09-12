package com.unicorn.wsp.vo;

import com.unicorn.wsp.entity.WspUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVo extends WspUser {

    // 角色权限id
    int roleId;

}
