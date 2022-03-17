package com.gys.report.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "供应商往来单据查询")
public class SupplierDealInData {
    @ApiModelProperty(value = "加盟商")
    private String client;
    @ApiModelProperty(value = "业务类型")
    private String matType;
    @ApiModelProperty(value = "供应商编码")
    private String supCode;
    @ApiModelProperty(value = "生产厂家")
    private String factory;
    @ApiModelProperty(value = "开始日期")
    private String startDate;
    @ApiModelProperty(value = "截至日期")
    private String endDate;
    @ApiModelProperty(value = "业务单据号")
    private String dnId;
    @NotBlank(message = "入库地点不能为空")
    @ApiModelProperty(value = "入库地点")
    private String siteCode;
    @ApiModelProperty(value = "入库门店")
    private String stoCode;
    @ApiModelProperty(value = "地点批量查询")
    private String[] siteArr;
    @ApiModelProperty(value = "合计金额")
    private String totalAmount;
    @ApiModelProperty(value = "商品编码")
    private String proCode;
    @ApiModelProperty(value = "商品集合")
    private String[] proArr;

    private Integer pageNum;
    private Integer pageSize;

    //商品自分类
    private String[] prosClass;
    //销售级别
    private String[] saleClass;
    //商品定位
    private String[] proPosition;
    //禁止采购
    private String purchase;
    //自定义1
    private String[] zdy1;
    //自定义2
    private String[] zdy2;
    //自定义3
    private String[] zdy3;
    //自定义4
    private String[] zdy4;
    //自定义5
    private String[] zdy5;

    @ApiModelProperty(value = "供应商业务员")
    private String  poSupplierSalesman;

    @ApiModelProperty(value = "抬头备注")
    private String  poHeadRemark;

    @ApiModelProperty(value = "税率编码")
    private String taxCode;

    @ApiModelProperty(value = "业务控制开关 0:关闭 1: 打开")
    private String isDefault;

    /**
     * 税率按钮开关0：关、1：开
     */
    private String isOpenRate;

    /**
     * 税率
     */
    private String taxRate;

}
