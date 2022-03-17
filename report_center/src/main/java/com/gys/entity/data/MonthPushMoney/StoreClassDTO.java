package com.gys.entity.data.MonthPushMoney;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StoreClassDTO {
    // 门店编码
    private  String storeCode;
    // 最小毛利率
    private BigDecimal minProMll;
    // 最大毛利率
    private BigDecimal maxProMll;
    // 提成比例
    private BigDecimal tichengScale;
    //提成级别
    private String saleClass;

}
