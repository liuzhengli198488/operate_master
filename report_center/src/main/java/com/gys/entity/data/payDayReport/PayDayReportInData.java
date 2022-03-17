package com.gys.entity.data.payDayReport;

import lombok.Data;

@Data
public class PayDayReportInData {
    /*
    加盟商
     */
    private String client;
    /*
    开始日期
     */
    private String startDate;
    /*
    门店
    */
    private String stoCode;
    /*
    结束日期
     */
    private String endDate;
    /*
    对账单号
     */
    private String voucherId;
}
