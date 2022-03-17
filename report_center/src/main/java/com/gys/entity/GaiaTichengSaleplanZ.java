package com.gys.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.*;

// 销售提成方案主表
@Data
@Table(name = "GAIA_TICHENG_SALEPLAN_Z")
public class GaiaTichengSaleplanZ implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键ID")
    @Id
    @Column(name = "ID")
    private Integer id;

    @ApiModelProperty(value = "加盟商")
    @Column(name = "CLIENT")
    private String client;

    @ApiModelProperty(value = "方案编码")
    @Column(name = "PLAN_CODE")
    private String planCode;

    @ApiModelProperty(value = "方案名称")
    @Column(name = "PLAN_NAME")
    private String planName;

    @ApiModelProperty(value = "起始日期")
    @Column(name = "PLAN_START_DATE")
    private String planStartDate;

    @ApiModelProperty(value = "结束日期")
    @Column(name = "PLAN_END_DATE")
    private String planEndDate;

    @ApiModelProperty(value = "提成方式: 0 按销售额提成 1 按毛利额提成")
    @Column(name = "PLAN_AMT_WAY")
    private String planAmtWay;

    @ApiModelProperty(value = "提成方式:0 按商品毛利率  1 按销售毛利率")
    @Column(name = "PLAN_RATE_WAY")
    private String planRateWay;

    @ApiModelProperty(value = "负毛利率商品是否不参与销售提成 0 是 1 否")
    @Column(name = "PLAN_IF_NEGATIVE")
    private String planIfNegative;

    @ApiModelProperty(value = "剔除商品分类json字符串")
    @Column(name = "PLAN_REJECT_CLASS")
    private String planRejectClass;

    @ApiModelProperty(value = "剔除商品设置：0 否 1 是")
    @Column(name = "PLAN_REJECT_PRO")
    private String planRejectPro;

    @ApiModelProperty(value = "提成类型 1 销售提成 2 单品提成")
    @Column(name = "PLAN_TYPE")
    private String planType;

    @ApiModelProperty(value = "门店分配比例")
    @Column(name = "PLAN_SCALE_STO")
    private String planScaleSto;

    @ApiModelProperty(value = "员工分配比例")
    @Column(name = "PLAN_SCALE_SALER")
    private String planScaleSaler;

    @ApiModelProperty(value = "审核状态 0 已保存 1 已审核 2 已停用")
    @Column(name = "PLAN_STATUS")
    private String planStatus;

    @ApiModelProperty(value = "操作状态 0 未删除 1 已删除 2 暂存（试算）")
    @Column(name = "DELETE_FLAG")
    private String deleteFlag;

    @ApiModelProperty(value = "创建人")
    @Column(name = "PLAN_CREATER")
    private String planCreater;

    @ApiModelProperty(value = "创建人编码")
    @Column(name = "PLAN_CREATER_ID")
    private String planCreaterId;

    @ApiModelProperty(value = "创建时间")
    @Column(name = "PLAN_CREATE_TIME")
    private String planCreateTime;

    @ApiModelProperty(value = "修改人编码")
    @Column(name = "PLAN_UPDATE_ID")
    private String planUpdateId;

    @ApiModelProperty(value = "修改人")
    @Column(name = "PLAN_UPDATER")
    private String planUpdater;

    @ApiModelProperty(value = "修改时间")
    @Column(name = "PLAN_UPDATE_DATETIME")
    private String planUpdateDatetime;

    @Column(name = "LAST_UPDATE_TIME")
    private Date lastUpdateTime;

    @ApiModelProperty(value = "停用时间")
    @Column(name = "PLAN_STOP_DATE")
    private String planStopDate;

    @ApiModelProperty(value = "已审核提醒内容/修改原因提醒/停用原因")
    @Column(name = "PLAN_REASON")
    private String planReason;

    @ApiModelProperty(value = "剔除折扣率 操作符号 =、＞、＞＝、＜、＜＝")
    @Column(name = "PLAN_REJECT_DISCOUNT_RATE_SYMBOL")
    private String planRejectDiscountRateSymbol;

    @ApiModelProperty(value = "剔除折扣率 数值")
    @Column(name = "PLAN_REJECT_DISCOUNT_RATE")
    private String planRejectDiscountRate;


}