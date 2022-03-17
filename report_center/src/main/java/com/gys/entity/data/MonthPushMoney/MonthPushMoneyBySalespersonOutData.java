package com.gys.entity.data.MonthPushMoney;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthPushMoneyBySalespersonOutData {
    private String client;
    //营业员编码")
    private String salerId;
    //营业员名称")
    private String salerName;
    //门店编码")
    private String stoCode;
    //门店名称")
    private String stoName;
    //销售天数")
    private BigDecimal days;
    //提成级别")
    private String saleClass;
    //实收金额")
    private BigDecimal amt;
    //提成合计")
    private BigDecimal deductionWage;
    //提成占比")
    private BigDecimal deductionRate;

//    public MonthPushMoneyBySalespersonOutData(String salerId, String salerName, String stoCode, String stoName, Integer days, String saleClass, BigDecimal amt, BigDecimal deductionWage) {
//        this.salerId = salerId;
//        this.salerName = salerName;
//        this.stoCode = stoCode;
//        this.stoName = stoName;
//        this.days = days;
//        this.saleClass = saleClass;
//        this.amt = amt;
//        this.deductionWage = deductionWage;
//    }

//    public MonthPushMoneyBySalespersonOutData(String salerId, String salerName, String stoCode, String stoName, String saleClass, BigDecimal amt, BigDecimal deductionWage) {
//        this.salerId = salerId;
//        this.salerName = salerName;
//        this.stoCode = stoCode;
//        this.stoName = stoName;
//        this.saleClass = saleClass;
//        this.amt = amt;
//        this.deductionWage = deductionWage;
//    }

//    public MonthPushMoneyBySalespersonOutData() {
//
//    }
}
