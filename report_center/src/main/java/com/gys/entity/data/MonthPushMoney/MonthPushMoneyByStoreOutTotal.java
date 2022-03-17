package com.gys.entity.data.MonthPushMoney;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MonthPushMoneyByStoreOutTotal {

    //  "销售天数"
    private BigDecimal days;
    //  "实收金额"
    private BigDecimal amt;
    //  "销售提成"
    private BigDecimal deductionWageSales;
    //  "单品提成"
    private BigDecimal deductionWagePro;
    //  "提成合计"
    private BigDecimal deductionWage;
    //  "销售提成占比"
    private String deductionWageSalesRate;
    //  "单品提成占比"
    private String deductionWageProRade;
    //  "提成占比"
    private String deductionWageRate;

}
