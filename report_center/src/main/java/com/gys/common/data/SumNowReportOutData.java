package com.gys.common.data;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SumNowReportOutData {
    private BigDecimal billCount;
    private BigDecimal grossProfitAmt;
    private BigDecimal gssdAmt;
}
