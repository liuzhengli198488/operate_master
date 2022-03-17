package com.gys.common.enums;

import com.gys.util.CommonUtil;

/**
 * 单号生成规则相关
 * @author chenhao
 */
public enum OrderRuleEnum {
    MEN_DIAN_XIAO_SHOU("销售", "SD"),
    CANG_KU_SHOU_HUO("仓库收货", "PS"),
    GONG_YING_SHANG_SHOU_HUO("供应商收货", "CS"),
    TUI_KU("退库", "TD"),
    TUI_GONG_YING_SHANG_DING_DAN_HAO("退供应商", "GD"),
    TIAO_JI("调剂", "TJ"),
    SUN_YI("损溢", "SYS"),
    TUI_GONG_YING_SHANG_DAN_HAO("退供应商", "GY"),
    YAN_SHOU("验收", "YS"),
    BU_HUO("补货", "PD"),
    CAI_GOU("采购", "CD"),
    QING_DOU("清斗", "QD"),
    ZHUANG_DOU("装斗", "ZD"),
    RI_DUI("日对账", "DZ"),
    XIAN_JIN("现金缴款", "JK"),
    YANG_HU("养护任务", "YH"),
    JI_FEN_DUI_HUAN("设置积分兑换", "JFHD"),
    JI_FEN_DI_XIAO("设置积分抵现", "JFHG"),
    PAN_DIAN("盘点单", "DD"),
    PAN_DIAN_CHA_YI("盘点差异", "DDF"),
    WEN_SHI_DU("温湿度记录", "TH"),
    SHE_BEI_WEI_HU("设备维护记录", "DC"),
    CU_XIAO("促销", "CX"),
    MEN_DIAN_BAO_SUN("报损", "SYBS"),
    WEI_TUO_PEI_SONG("委托配送", "WT"),
    MEN_DIAN_LING_YONG("领用", "SYLY"),
    MEN_DIAN_BAO_YI("报溢", "SYBY");

    /**
     * 根据单号解析单号规则，获得订单类型
     * @param orderNo
     * @return
     */
    public static String getOrderRule(String orderNo){
        String flag = CommonUtil.extractEnglishStr(orderNo);
        for(OrderRuleEnum orderRuleEnum : OrderRuleEnum.values()){
            if(orderRuleEnum.getOrderNoRule().equalsIgnoreCase(flag)){
                return orderRuleEnum.getOrderName();
            }
        }
        return "";
    }

    OrderRuleEnum(String orderName, String orderNoRule) {
        this.orderName = orderName;
        this.orderNoRule = orderNoRule;
    }

    private String orderName;

    private String orderNoRule;

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getOrderNoRule() {
        return orderNoRule;
    }

    public void setOrderNoRule(String orderNoRule) {
        this.orderNoRule = orderNoRule;
    }
}
