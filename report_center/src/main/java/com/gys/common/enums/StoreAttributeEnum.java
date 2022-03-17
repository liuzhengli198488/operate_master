package com.gys.common.enums;

/**
 * @desc: 门店属性
 * @author: ZhangChi
 * @createTime: 2021/12/20 13:30
 */
public enum StoreAttributeEnum {
    SINGLE("1", "单体店"),
    LINKAGE("2", "连锁店"),
    JOIN("3", "加盟店"),
    OUTPATIENTS("4", "门诊");

    public final String type;
    public final String name;

    StoreAttributeEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public static String getName(String type) {
        for (StoreAttributeEnum value : StoreAttributeEnum.values()) {
            if (value.type.equals(type)) {
                return value.name;
            }
        }
        return "";
    }

    public String getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }
}
