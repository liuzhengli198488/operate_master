package com.gys.common.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MaterialDocumentOutData {
    @ApiModelProperty(value = "加盟商")
    private String francName;
    @ApiModelProperty(value = "税金")
    private BigDecimal matRateMov;
    @ApiModelProperty(value = "厂家")
    private String proFactoryName;
    @ApiModelProperty(value = "业务单号")
    private String matDnId;
    @ApiModelProperty(value = "规格")
    private String proSpecs;
    @ApiModelProperty(value = "订单号")
    private String matPoId;
    @ApiModelProperty(value = "金额")
    private BigDecimal matMovAmt;
    @ApiModelProperty(value = "地点")
    private String francAddr;
    @ApiModelProperty(value = "单位")
    private String proUnit;
    @ApiModelProperty(value = "发生数量")
    private BigDecimal matQty;
    @ApiModelProperty(value = "商品名称")
    private String proName;
    @ApiModelProperty(value = "商品编码")
    private String matProCode;
    @ApiModelProperty(value = "发生日期")
    private String createDate;
    @ApiModelProperty(value = "加点后金额")
    private BigDecimal matAddAmt;
    @ApiModelProperty(value = "加点后税额")
    private BigDecimal matAddTax;
    @ApiModelProperty(value = "税率")
    private String taxValue;
    @ApiModelProperty(value = "发生时间")
    private String matCreateTime;
    @ApiModelProperty(value = "正负")
    private String matDebitCredit;

}
