package com.gys.report.entity.dto;

import lombok.Data;

/**
 * 居民医保费用汇总入参
 */
@Data
public class MedicalInsturanceSummaryDTO {

    //加盟商id
    private String clientId;
    //开始时间
    private String startDate;
    //结束时间
    private String endDate;
    //医疗结构(门店编码)
    private String stoCode;
    //医疗机构名称
    private String stoName;
}
