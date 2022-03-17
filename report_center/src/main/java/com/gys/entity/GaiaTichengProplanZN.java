package com.gys.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "GAIA_TICHENG_PROPLAN_Z_N")
@Data
public class GaiaTichengProplanZN implements Serializable {
    /**
     * 主键ID
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 加盟商
     */
    @Column(name = "CLIENT")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT LAST_INSERT_ID()")
    private String client;

    /**
     * 方案编码
     */
    @Column(name = "PLAN_CODE")
    private String planCode;

    /**
     * 方案名称
     */
    @Column(name = "PLAN_NAME")
    private String planName;

    /**
     * 起始日期
     */
    @Column(name = "PLAN_START_DATE")
    private String planStartDate;

    /**
     * 结束日期
     */
    @Column(name = "PLAN_END_DATE")
    private String planEndDate;

    /**
     * 单品提成方式 0 不参与销售提成 1 参与销售提成
     */
    @Column(name = "PLAN_PRODUCT_WAY")
    private String planProductWay;

    /**
     * 提成方式 1 按零售额提成 2 按销售额提成 3 按毛利额提成
     */
    @Column(name = "PLIANT_PERCENTAGE_TYPE")
    private String planPercentageType;

    /**
     * 提成类型 1 销售提成 2 单品提成
     */
    @Column(name = "PLAN_TYPE")
    private String planType;

    /**
     * 门店分配比例
     */
    @Column(name = "PLAN_SCALE_STO")
    private String planScaleSto;

    /**
     * 员工分配比例
     */
    @Column(name = "PLAN_SCALE_SALER")
    private String planScaleSaler;

    /**
     * 审核状态 0 未审核 1 已审核 2 已停用
     */
    @Column(name = "PLAN_STATUS")
    private String planStatus;

    /**
     * 操作状态 0 未删除 1 已删除 2 暂存（试算）
     */
    @Column(name = "DELETE_FLAG")
    private String deleteFlag;

    /**
     * 创建人
     */
    @Column(name = "PLAN_CREATER")
    private String planCreater;

    /**
     * 创建人编码
     */
    @Column(name = "PLAN_CREATER_ID")
    private String planCreaterId;

    /**
     * 创建时间
     */
    @Column(name = "PLAN_CREATE_TIME")
    private String planCreateTime;

    /**
     * 修改人编码
     */
    @Column(name = "PLAN_UPDATE_ID")
    private String planUpdateId;

    /**
     * 修改人
     */
    @Column(name = "PLAN_UPDATER")
    private String planUpdater;

    /**
     * 修改时间
     */
    @Column(name = "PLAN_UPDATE_DATETIME")
    private String planUpdateDatetime;

    /**
     * 已审核提醒内容/修改原因提醒/停用原因
     */
    @Column(name = "PLAN_REASON")
    private String planReason;

    /**
     * 停用时间
     */
    @Column(name = "PLAN_STOP_DATE")
    private String planStopDate;

    @Column(name = "LAST_UPDATE_TIME")
    private Date lastUpdateTime;

    private static final long serialVersionUID = 1L;
}
