package com.gys.entity.data.MonthPushMoney;

import lombok.Data;

@Data
public class MonthPushMoneyBySalespersonInData {
    private String client;
    private String suitMonth;
    private String salerId;
    private String stoCode;
    private String startDate;
    private String endDate;
    private Integer pageNum;
    private Integer pageSize;
}
