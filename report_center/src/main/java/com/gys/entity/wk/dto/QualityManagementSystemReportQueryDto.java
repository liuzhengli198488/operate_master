package com.gys.entity.wk.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * GAIA_QUALITY_MANAGEMENT_SYSTEM_REPORT
 * @author 
 */
@Data
public class QualityManagementSystemReportQueryDto implements Serializable {

    /**
     * 加盟商
     */
    private String client;

    /**
     * GSP编号
     */
    private String voucherId;

    /**
     * 标题
     */
    private String title;

    /**
     * 审核范围
     */
    private String reviewRange;


    /**
     * 审核
     */
    private String audit;


    /**
     * 审核目的
     */
    private String auditTarget;


    /**
     * 审核员
     */
    private String auditUser;


    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 开始时间
     */
    private Date  beginDate;


    /**
     * 结束时间
     */
    private Date  endDate;


    /**
     * 页数
     */
    private Integer pageNum;
    /**
     *  页大小
     */
    private Integer pageSize;


    private static final long serialVersionUID = 1L;
}