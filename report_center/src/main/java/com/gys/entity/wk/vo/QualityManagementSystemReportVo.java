package com.gys.entity.wk.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * GAIA_QUALITY_MANAGEMENT_SYSTEM_REPORT
 * @author 
 */
@Data
public class QualityManagementSystemReportVo implements Serializable {

    /**
     * GSP编号
     */
    @ExcelProperty(value = "编号")
    private String voucherId;

    /**
     * 标题
     */
    @ExcelProperty(value = "标题")
    private String title;

    /**
     * 审核范围
     */
    @ExcelProperty(value = "审核范围")
    private String reviewRange;

    /**
     * 不合格项统计
     */
    @ExcelProperty(value = "不合格项统计")
    private String unqualified;

    /**
     * 结论
     */
    @ExcelProperty(value = "结论")
    private String conclusion;

    /**
     * 批准人
     */
    @ExcelProperty(value = "批准人")
    private String approveUser;

    /**
     * 批准时间
     */
    @ExcelProperty(value = "批准时间")
    private String  approveTime;

    /**
     * 审核
     */
    @ExcelProperty(value = "审核")
    private String audit;

    /**
     * 审核过程综述
     */
    @ExcelProperty(value = "审核过程综述")
    private String auditContent;

    /**
     * 审核目的
     */
    @ExcelProperty(value = "审核目的")
    private String auditTarget;

    /**
     * 审核时间
     */
    @ExcelProperty(value = "审核时间")
    private String  auditTime;

    /**
     * 审核依据
     */
    @ExcelProperty(value = "标题")
    private String auditBase;

    /**
     * 审核员
     */
    @ExcelProperty(value = "审核员")
    private String auditUser;

    /**
     * 审核组长
     */
    @ExcelProperty(value = "审核组长")
    private String auditLeader;

    /**
     * 创建者
     */
    @ExcelProperty(value = "制单人")
    private String createUser;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "制单日期")
    private Date createTime;

    /**
     * 纠正要求
     */
    @ExcelProperty(value = "纠正要求")
    private String correctContent;





    private static final long serialVersionUID = 1L;
}