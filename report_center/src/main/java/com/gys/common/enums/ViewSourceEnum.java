package com.gys.common.enums;

/**
 * @desc: 查看类型
 * @author: Ryan
 * @createTime: 2021/12/20 15:56
 */
public enum ViewSourceEnum {

    APP(1, ""),
    WEB(2, "");

    public final Integer type;
    public final String name;

    ViewSourceEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }
}
