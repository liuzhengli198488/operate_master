package com.gys.common.enums;

/**
 * @desc: 纳税属性
 * @author: ZhangChi
 * @createTime: 2021/12/20 13:41
 */
public enum StoreTaxClassEnum {
    GENERAL_TAXPAYER("1", "一般纳税人"),
    SMALL_TAXPAYER("2", "小规模纳税人"),
    INDIVIDUAL_INDUSTRIAL_AND_COMMERCIAL_HOUSEHOLDS("3", "个体工商户"),

    ;

    public final String type;
    public final String name;

    StoreTaxClassEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public static String getName(String type) {
        for (StoreTaxClassEnum value : StoreTaxClassEnum.values()) {
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
