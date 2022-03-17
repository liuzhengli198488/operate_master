package com.gys.common.enums;

public enum ReplenishStyleEnum {
    NORMAL_REPLENISHMENT("0", "正常补货"),

    EMERGENCY_REPLENISHMENT("1", "紧急补货"),

    SHOP_GOODS("2", "铺货"),

    INTERMODULATION("3", "互调"),

    DIRECT_DELIVERY("4", "直配"),
    ;

    public final String type;
    public final String name;

    ReplenishStyleEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }
}
