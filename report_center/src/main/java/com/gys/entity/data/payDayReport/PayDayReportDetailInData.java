package com.gys.entity.data.payDayReport;

import lombok.Data;

@Data
public class PayDayReportDetailInData {
    /*
    加盟商
     */
    private String client;
    /*
    门店
   */
    private String stoCode;
    /*
    对账单号
     */
    private String voucherId;
}
