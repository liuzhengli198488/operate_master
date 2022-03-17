package com.gys.report.entity.vo;


import lombok.Data;

import java.math.BigDecimal;

/**
 * 居民医保费用汇总出厂类
 */
@Data
public class MedicalInsturanceSummaryVO {

    //默认门诊
    private String outpatient;

    //人员类别
    private String personType;

    //人次
    private Integer visits;

    //医疗总费用
    private BigDecimal totalMedicalExpenses;

    //基本基金支付
    private BigDecimal basicFundPayments;

    //补充基金支付
    private BigDecimal supplementaryFundPayments;

    //大病基金支付
    private BigDecimal criticalIllnessFundPayments;

    //门诊补偿支付
    private BigDecimal outpatientCompensationPayments;

    //医院承担金额  数据库中没有   取值为0
    private BigDecimal hospitalPayments;

    //医疗救助金额
    private BigDecimal medicalAidAmount;

}
