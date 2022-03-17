package com.gys.entity.data.MonthPushMoney;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class SalespersonSaleDetailDTO {
    private String userCode;
    private String userName;
    private String stoCode;
    private String stoName;
    private String gssdDate;
    private BigDecimal amt;
    private BigDecimal prices;
    private BigDecimal grossProfitRate;

}
