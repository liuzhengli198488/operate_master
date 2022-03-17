package com.gys.entity.wk.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * GAIA_QUALITY_MANAGEMENT_SYSTEM_REPORT
 * @author 
 */
@Data
public class QualityManagementSystemReportDto implements Serializable {
    /**
     * 主键
     */
    private Long id;

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
     * 不合格项统计
     */
    private String unqualified;

    /**
     * 结论
     */
    private String conclusion;

    /**
     * 批准人
     */
    private String approveUser;

    /**
     * 批准时间
     */
    private Date approveTime;

    /**
     * 审核
     */
    private String audit;

    /**
     * 审核过程综述
     */
    private String auditContent;

    /**
     * 审核目的
     */
    private String auditTarget;

    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 审核依据
     */
    private String auditBase;

    /**
     * 审核员
     */
    private String auditUser;

    /**
     * 审核组长
     */
    private String auditLeader;

    /**
     * 纠正要求
     */
    private String correctContent;

    /**
     * 删除标记
     */
    private Integer isDelete;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建者
     */
    private String createUser;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 更新者
     */
    private String updateUser;

    private static final long serialVersionUID = 1L;
}