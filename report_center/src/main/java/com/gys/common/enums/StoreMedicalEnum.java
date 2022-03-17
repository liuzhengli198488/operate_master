package com.gys.common.enums;

/**
 * @desc: 是否医保
 * @author: ZhangChi
 * @createTime: 2021/12/20 13:35
 */
public enum StoreMedicalEnum {

    YES("1", "是"),
    NO("2", "否"),

    ;

    public final String type;
    public final String name;

    StoreMedicalEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public static String getName(String type) {
        for (StoreMedicalEnum value : StoreMedicalEnum.values()) {
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
