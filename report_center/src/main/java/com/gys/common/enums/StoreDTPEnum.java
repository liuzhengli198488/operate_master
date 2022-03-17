package com.gys.common.enums;

/**
 * @desc: DTP
 * @author: ZhangChi
 * @createTime: 2021/12/20 13:39
 */
public enum StoreDTPEnum {
    YES("1", "是"),
    NO("2", "否"),

    ;

    public final String type;
    public final String name;

    StoreDTPEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public static String getName(String type) {
        for (StoreDTPEnum value : StoreDTPEnum.values()) {
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
