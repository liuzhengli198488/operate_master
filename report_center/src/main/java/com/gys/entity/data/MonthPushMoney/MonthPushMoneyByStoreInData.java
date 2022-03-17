package com.gys.entity.data.MonthPushMoney;

import lombok.Data;

@Data
public class MonthPushMoneyByStoreInData {
    private String client;
    // "提成月份"
    private String suitMonth;
    // "门店编码"
    private String stoCode;
    // "开始时间"
    private String startDate;
    // "结束时间"
    private String endDate;
}
