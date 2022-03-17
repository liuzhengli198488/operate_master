package com.gys.common.enums;

/**
 * @desc:
 * @author: Ryan
 * @createTime: 2021/9/1 17:54
 */
public enum XhlTypeEnum {

    SEND_XHL(1, "配货下货率"),
    GZ_XHL(2, "过账下货率"),
    FINAL_XHL(3, "最终下货率"),;

    public final Integer type;
    public final String name;

    XhlTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }
}
