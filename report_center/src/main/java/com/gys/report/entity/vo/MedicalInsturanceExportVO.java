package com.gys.report.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class MedicalInsturanceExportVO {
    /**
     * 制表人名
     */
    private String userName;

    /**
     * 表头开始时间
      */
    private String startDate;

    /**
     * 表头结束时间
     */
    private String endDate;

    /**
     * 医疗机构
     */
    private String stoName;

    /**
     * 汇总单数据
     */
    private List<MedicalInsturanceSummaryVO>  summaryVOS;

}
