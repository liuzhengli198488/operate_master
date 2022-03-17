package com.gys.report.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
public class InventoryChangeSummaryDetailOutData {
    @ApiModelProperty(value = "")
    private String client;
    @ApiModelProperty(value = "供应商/客户编码")
    private String supCusCode;
    @ApiModelProperty(value = "供应商/客户名称")
    private String supCusName;
    @ApiModelProperty(value = "业务单号")
    private String dnId;
    @ApiModelProperty(value = "批号")
    private String batchNo;
    @ApiModelProperty(value = "商品编码")
    private String proCode;
    @ApiModelProperty(value = "商品名称")
    private String proName;
    @ApiModelProperty(value = "商品通用名称")
    private String proCommonName;
    @ApiModelProperty(value = "规格")
    private String specs;
    @ApiModelProperty(value = "单位")
    private String unit;
    @ApiModelProperty(value = "生产厂家")
    private String factoryName;
    @ApiModelProperty(value = "地点编码")
    private String siteCode;
    @ApiModelProperty(value = "地点名称")
    private String siteName;
    @ApiModelProperty(value = "单据类型")
    private String typeName;
    @ApiModelProperty(value = "金额")
    private BigDecimal amount;
    @ApiModelProperty(value = "加点含税成本金额")
    private BigDecimal addAmount;
    @ApiModelProperty(value = "数量")
    private BigDecimal qty;
    @ApiModelProperty(value = "时间")
    private String docDate;
    @ApiModelProperty(value = "商品大类编码")
    private String bigClass;
    @ApiModelProperty(value = "商品中类编码")
    private String midClass;
    @ApiModelProperty(value = "商品分类编码")
    private String proClass;
    @ApiModelProperty(value = "医保编码")
    private String medProdctCode;
    @ApiModelProperty(value = "是否医保")
    private String ifMed;
    @ApiModelProperty(value = "零售额")
    private String retailPrice;
    @ApiModelProperty(value = "期初数量")
    private String startCount;
    @ApiModelProperty(value = "期间数量")
    private String betweenCount;
    @ApiModelProperty(value = "期末数量")
    private String endCount;
}
