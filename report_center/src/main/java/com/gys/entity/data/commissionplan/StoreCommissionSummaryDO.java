package com.gys.entity.data.commissionplan;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author wu mao yin
 * @Title: 门店提成汇总入参
 * @date 2021/11/2214:28
 */
@Data
public class StoreCommissionSummaryDO implements Serializable {

    private Integer pageSize = 100;

    private Integer pageNum = 1;

    @ApiModelProperty("加盟商")
    private String client;

    @ApiModelProperty("方案开始时间")
    private String startDate;

    @ApiModelProperty("方案结束时间")
    private String endDate;

    @ApiModelProperty("方案id")
    private Integer planId;

    @ApiModelProperty("方案名称")
    private String planName;

    @ApiModelProperty("提成方案类型 1:销售提成 2：单品提成")
    private String type;

    @ApiModelProperty("子方案名称")
    private String subPlanName;

    @ApiModelProperty("是否显示子方案，默认false")
    private Boolean showSubPlan = false;

    @ApiModelProperty("是否显示门店，默认false")
    private Boolean showStore = false;

    @ApiModelProperty("门店编码")
    private List<String> stoCodes;

    @ApiModelProperty("营业员 员工提成汇总时传")
    private List<String> saleName;

    @ApiModelProperty("显示粒度 1:期间汇总 2:日期粒度， 默认 1")
    private Integer displayGranularity;

    @ApiModelProperty("1:门店提成汇总 2:员工提成汇总")
    private Integer summaryType;

    @ApiModelProperty(value = "门店编码", hidden = true)
    private String stoCode;

    @ApiModelProperty(value = "是否显示成本配置", hidden = true)
    private Map<String, Boolean> costAmtShowConfigMap;

    @ApiModelProperty(value = "是否管理员 true 是 , false 否")
    private Boolean admin = false;

    @ApiModelProperty("员工id")
    private String userId;

    @ApiModelProperty(value = "默认门店编码", hidden = true)
    private String deptId;

}
