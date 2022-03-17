package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProCampaignsOutData {
    @ApiModelProperty(value = "门店编码")
    private String stoCode;
    @ApiModelProperty(value = "门店名")
    private String stoName;
    @ApiModelProperty(value = "员工编码")
    private String userId;
    @ApiModelProperty(value = "员工姓名")
    private String userName;
    @ApiModelProperty(value = "活动类型")
    private String proActiveType;
    @ApiModelProperty(value = "销售日期")
    private String gssdDate;
    @ApiModelProperty(value = "销售单号")
    private String billNo;
    @ApiModelProperty(value = "商品编码")
    private String proCode;
    @ApiModelProperty(value = "商品名")
    private String proName;
    @ApiModelProperty(value = "数量")
    private String gssdQty;
    @ApiModelProperty(value = "应收金额")
    private String gssdnormalAmt;
    @ApiModelProperty(value = "实收金额")
    private String gssdAmt;
    @ApiModelProperty(value = "折扣额")
    private String discountAmt;
    @ApiModelProperty(value = "生产厂家")
    private String proFactoryName;
    @ApiModelProperty(value = "规格")
    private String proSpecs;
    @ApiModelProperty(value = "商品大类编码")
    private String bigClass;
    @ApiModelProperty(value = "商品中类编码")
    private String midClass;
    @ApiModelProperty(value = "商品分类编码")
    private String proClass;
    @ApiModelProperty(value = "医保编码")
    private String medProdctCode;
    @ApiModelProperty(value = "商品通用名")
    private String proCommonname;
    @ApiModelProperty(value = "商品描述")
    private String proDepict;
}
