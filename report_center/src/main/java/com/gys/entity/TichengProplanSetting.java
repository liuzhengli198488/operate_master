package com.gys.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author flynn
 * @since 2021-11-15
 */
@Data
@Table( name = "GAIA_TICHENG_PROPLAN_SETTING")
public class TichengProplanSetting implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键ID")
    @Id
    @Column(name = "ID")
    private Long id;

    @ApiModelProperty(value = "加盟商")
    @Column(name = "CLIENT")
    private String client;

    @ApiModelProperty(value = "主方案id")
    @Column(name = "PID")
    private Long pid;

    @ApiModelProperty(value = "子方案名称")
    @Column(name = "C_PLAN_NAME")
    private String cPlanName;

    @ApiModelProperty(value = "单品提成方式 0 不参与销售提成 1 参与销售提成")
    @Column(name = "PLAN_PRODUCT_WAY")
    private String planProductWay;

    @ApiModelProperty(value = "提成方式 1 按零售额提成 2 按销售额提成 3 按毛利提成")
    @Column(name = "PLIANT_PERCENTAGE_TYPE")
    private String pliantPercentageType;

    @ApiModelProperty(value = "提成类型 1 销售提成 2 单品提成")
    @Column(name = "PLAN_TYPE")
    private String planType;

    @ApiModelProperty(value = "剔除折扣率 操作符号 =、＞、＞＝、＜、＜＝")
    @Column(name = "PLAN_REJECT_DISCOUNT_RATE_SYMBOL")
    private String planRejectDiscountRateSymbol;

    @ApiModelProperty(value = "剔除折扣率 数值")
    @Column(name = "PLAN_REJECT_DISCOUNT_RATE")
    private String planRejectDiscountRate;

    @ApiModelProperty(value = "负毛利率商品是否不参与销售提成 0 是 1 否")
    @Column(name = "PLAN_IF_NEGATIVE")
    private String planIfNegative;

    @ApiModelProperty(value = "操作状态 0 未删除 1 已删除 2 暂存（试算）")
    @Column(name = "DELETE_FLAG")
    private String deleteFlag;

    @ApiModelProperty(value = "创建人")
    @Column(name = "CREATER")
    private String creater;

    @ApiModelProperty(value = "创建人编码")
    @Column(name = "CREATER_ID")
    private String createrId;

    @ApiModelProperty(value = "创建时间")
    @Column(name = "CREATE_TIME")
    private String createTime;

    @ApiModelProperty(value = "修改人编码")
    @Column(name = "UPDATE_ID")
    private String updateId;

    @ApiModelProperty(value = "修改人")
    @Column(name = "UPDATER")
    private String updater;

    @ApiModelProperty(value = "修改时间")
    @Column(name = "UPDATE_DATETIME")
    private String updateDatetime;

    @Column(name = "LAST_UPDATE_TIME")
    private Date lastUpdateTime;


}
