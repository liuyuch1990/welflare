package com.unicorn.wsp.common.result;

import lombok.Getter;

@Getter
public enum ResultEnum {
    //结果
    success(0),fail(1),error(2);

    private final Integer code;

    ResultEnum(Integer code) {
        this.code = code;
    }

}
