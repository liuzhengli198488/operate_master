package com.gys.entity.data.MonthPushMoney.V2;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PushMoneyByStoreV2OutTotal {

    //  "销售天数"
    private BigDecimal days;
    //  "实收金额"
    private BigDecimal amt;
    //  "提成合计"
    private BigDecimal deductionWage;
    //  "提成占比"
    private BigDecimal deductionWageRate;

}
