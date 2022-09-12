package com.unicorn.wsp.common.consts;

/**
 * 正则匹配常量
 * @author zy
 */
public interface PatternConst {

    String REGEX_PHONE =
            "^(1[1-9][0-9]|14[5|7]|15[0|1|2|3|4|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$";

    String REGEX_PASSWORD =
            "^(?=.*\\d)(?=.*[a-zA-Z]).{8,}$";
}
