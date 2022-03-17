package com.gys.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProductMatchOutData {
    @ApiModelProperty(value = "客户编码")
    private String clientId;
    @ApiModelProperty(value = "客户编码")
    private String clientName;
    @ApiModelProperty(value = "匹配状态")
    private String matchStatus;
    @ApiModelProperty(value = "匹配度")
    private String matchDegree;
    @ApiModelProperty(value = "商品编码")
    private String proCode;
    @ApiModelProperty(value = "商品名称")
    private String proName;
    @ApiModelProperty(value = "规格")
    private String proSpecs;
    @ApiModelProperty(value = "生产厂家")
    private String proFactoryName;
    @ApiModelProperty(value = "国际条码")
    private String proBarCode;
    @ApiModelProperty(value = "批准文号")
    private String proRegisterNo;
    @ApiModelProperty(value = "商品编码(G)")
    private String gssdProCode;
    @ApiModelProperty(value = "商品名称(G)")
    private String gssdProName;
    @ApiModelProperty(value = "规格(G)")
    private String gssdProSpecs;
    @ApiModelProperty(value = "生产厂家(G)")
    private String gssdProFactoryName;
    @ApiModelProperty(value = "国际条码(G)")
    private String gssdProBarCode;
    @ApiModelProperty(value = "批准文号(G)")
    private String gssdProRegisterNo;
    @ApiModelProperty(value = "商品名称是否匹配 Y 是 N 否")
    private String proNameFlag;
    @ApiModelProperty(value = "规格是否匹配 Y 是 N 否")
    private String proSpecsFlag;
    @ApiModelProperty(value = "生产厂家是否匹配 Y 是 N 否")
    private String proFactoryNameFlag;
    @ApiModelProperty(value = "国际条码是否匹配 Y 是 N 否")
    private String proBarCodeFlag;
    @ApiModelProperty(value = "批准文号是否匹配 Y 是 N 否")
    private String proRegisterNoFlag;
}
