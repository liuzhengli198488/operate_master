package com.gys.entity;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "GAIA_TICHENG_PLAN_Z")
public class GaiaTichengPlanZ implements Serializable {
    /**
     * 主键ID
     */
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 加盟商
     */
    @Column(name = "CLIENT")
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
     * 适用月份
     */
    @Column(name = "PLAN_SUIT_MONTH")
    private String planSuitMonth;

    /**
     * 提成方式: 0 按销售额提成 1 按毛利额提成
     */
    @Column(name = "PLAN_AMT_WAY")
    private String planAmtWay;

    /**
     * 提成方式:0 按商品毛利率  1 按销售毛利率
     */
    @Column(name = "PLAN_RATE_WAY")
    private String planRateWay;

    /**
     * 负毛利率商品是否不参与销售提成 0 是 1 否
     */
    @Column(name = "PLAN_IF_NEGATIVE")
    private String planIfNegative;

    /**
     * 单品提成方式 0 不参与销售提成 1 参与销售提成
     */
    @Column(name = "PLAN_PRODUCT_WAY")
    private String planProductWay;

    /**
     * 审核状态 0 未审核 1 已审核
     */
    @Column(name = "PLAN_STATUS")
    private String planStatus;

    /**
     * 操作状态 0 未删除 1 已删除
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

    private static final long serialVersionUID = 1L;

    /**
     * 获取主键ID
     *
     * @return ID - 主键ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键ID
     *
     * @param id 主键ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取加盟商
     *
     * @return CLIENT - 加盟商
     */
    public String getClient() {
        return client;
    }

    /**
     * 设置加盟商
     *
     * @param client 加盟商
     */
    public void setClient(String client) {
        this.client = client;
    }

    /**
     * 获取方案编码
     *
     * @return PLAN_CODE - 方案编码
     */
    public String getPlanCode() {
        return planCode;
    }

    /**
     * 设置方案编码
     *
     * @param planCode 方案编码
     */
    public void setPlanCode(String planCode) {
        this.planCode = planCode;
    }

    /**
     * 获取方案名称
     *
     * @return PLAN_NAME - 方案名称
     */
    public String getPlanName() {
        return planName;
    }

    /**
     * 设置方案名称
     *
     * @param planName 方案名称
     */
    public void setPlanName(String planName) {
        this.planName = planName;
    }

    /**
     * 获取起始日期
     *
     * @return PLAN_START_DATE - 起始日期
     */
    public String getPlanStartDate() {
        return planStartDate;
    }

    /**
     * 设置起始日期
     *
     * @param planStartDate 起始日期
     */
    public void setPlanStartDate(String planStartDate) {
        this.planStartDate = planStartDate;
    }

    /**
     * 获取结束日期
     *
     * @return PLAN_END_DATE - 结束日期
     */
    public String getPlanEndDate() {
        return planEndDate;
    }

    /**
     * 设置结束日期
     *
     * @param planEndDate 结束日期
     */
    public void setPlanEndDate(String planEndDate) {
        this.planEndDate = planEndDate;
    }

    /**
     * 获取适用月份
     *
     * @return PLAN_SUIT_MONTH - 适用月份
     */
    public String getPlanSuitMonth() {
        return planSuitMonth;
    }

    /**
     * 设置适用月份
     *
     * @param planSuitMonth 适用月份
     */
    public void setPlanSuitMonth(String planSuitMonth) {
        this.planSuitMonth = planSuitMonth;
    }

    /**
     * 获取提成方式: 0 按销售额提成 1 按毛利额提成
     *
     * @return PLAN_AMT_WAY - 提成方式: 0 按销售额提成 1 按毛利额提成
     */
    public String getPlanAmtWay() {
        return planAmtWay;
    }

    /**
     * 设置提成方式: 0 按销售额提成 1 按毛利额提成
     *
     * @param planAmtWay 提成方式: 0 按销售额提成 1 按毛利额提成
     */
    public void setPlanAmtWay(String planAmtWay) {
        this.planAmtWay = planAmtWay;
    }

    /**
     * 获取提成方式:0 按商品毛利率  1 按销售毛利率
     *
     * @return PLAN_RATE_WAY - 提成方式:0 按商品毛利率  1 按销售毛利率
     */
    public String getPlanRateWay() {
        return planRateWay;
    }

    /**
     * 设置提成方式:0 按商品毛利率  1 按销售毛利率
     *
     * @param planRateWay 提成方式:0 按商品毛利率  1 按销售毛利率
     */
    public void setPlanRateWay(String planRateWay) {
        this.planRateWay = planRateWay;
    }

    /**
     * 获取负毛利率商品是否不参与销售提成 0 是 1 否
     *
     * @return PLAN_IF_NEGATIVE - 负毛利率商品是否不参与销售提成 0 是 1 否
     */
    public String getPlanIfNegative() {
        return planIfNegative;
    }

    /**
     * 设置负毛利率商品是否不参与销售提成 0 是 1 否
     *
     * @param planIfNegative 负毛利率商品是否不参与销售提成 0 是 1 否
     */
    public void setPlanIfNegative(String planIfNegative) {
        this.planIfNegative = planIfNegative;
    }

    /**
     * 获取单品提成方式 0 不参与销售提成 1 参与销售提成
     *
     * @return PLAN_PRODUCT_WAY - 单品提成方式 0 不参与销售提成 1 参与销售提成
     */
    public String getPlanProductWay() {
        return planProductWay;
    }

    /**
     * 设置单品提成方式 0 不参与销售提成 1 参与销售提成
     *
     * @param planProductWay 单品提成方式 0 不参与销售提成 1 参与销售提成
     */
    public void setPlanProductWay(String planProductWay) {
        this.planProductWay = planProductWay;
    }

    /**
     * 获取审核状态 0 未审核 1 已审核
     *
     * @return PLAN_STATUS - 审核状态 0 未审核 1 已审核
     */
    public String getPlanStatus() {
        return planStatus;
    }

    /**
     * 设置审核状态 0 未审核 1 已审核
     *
     * @param planStatus 审核状态 0 未审核 1 已审核
     */
    public void setPlanStatus(String planStatus) {
        this.planStatus = planStatus;
    }

    /**
     * 获取操作状态 0 未删除 1 已删除
     *
     * @return DELETE_FLAG - 操作状态 0 未删除 1 已删除
     */
    public String getDeleteFlag() {
        return deleteFlag;
    }

    /**
     * 设置操作状态 0 未删除 1 已删除
     *
     * @param deleteFlag 操作状态 0 未删除 1 已删除
     */
    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    /**
     * 获取创建人
     *
     * @return PLAN_CREATER - 创建人
     */
    public String getPlanCreater() {
        return planCreater;
    }

    /**
     * 设置创建人
     *
     * @param planCreater 创建人
     */
    public void setPlanCreater(String planCreater) {
        this.planCreater = planCreater;
    }

    /**
     * 获取创建人编码
     *
     * @return PLAN_CREATER_ID - 创建人编码
     */
    public String getPlanCreaterId() {
        return planCreaterId;
    }

    /**
     * 设置创建人编码
     *
     * @param planCreaterId 创建人编码
     */
    public void setPlanCreaterId(String planCreaterId) {
        this.planCreaterId = planCreaterId;
    }

    /**
     * 获取创建时间
     *
     * @return PLAN_CREATE_TIME - 创建时间
     */
    public String getPlanCreateTime() {
        return planCreateTime;
    }

    /**
     * 设置创建时间
     *
     * @param planCreateTime 创建时间
     */
    public void setPlanCreateTime(String planCreateTime) {
        this.planCreateTime = planCreateTime;
    }

    /**
     * 获取修改人编码
     *
     * @return PLAN_UPDATE_ID - 修改人编码
     */
    public String getPlanUpdateId() {
        return planUpdateId;
    }

    /**
     * 设置修改人编码
     *
     * @param planUpdateId 修改人编码
     */
    public void setPlanUpdateId(String planUpdateId) {
        this.planUpdateId = planUpdateId;
    }

    /**
     * 获取修改人
     *
     * @return PLAN_UPDATER - 修改人
     */
    public String getPlanUpdater() {
        return planUpdater;
    }

    /**
     * 设置修改人
     *
     * @param planUpdater 修改人
     */
    public void setPlanUpdater(String planUpdater) {
        this.planUpdater = planUpdater;
    }

    /**
     * 获取修改时间
     *
     * @return PLAN_UPDATE_DATETIME - 修改时间
     */
    public String getPlanUpdateDatetime() {
        return planUpdateDatetime;
    }

    /**
     * 设置修改时间
     *
     * @param planUpdateDatetime 修改时间
     */
    public void setPlanUpdateDatetime(String planUpdateDatetime) {
        this.planUpdateDatetime = planUpdateDatetime;
    }
}