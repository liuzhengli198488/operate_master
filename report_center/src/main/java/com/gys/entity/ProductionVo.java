package com.gys.entity;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProductionVo {

    @ApiModelProperty(value = "销售日期")
    private String gssDate;
    @ApiModelProperty(value = "销售单号")
    private String gssBillNo;
    @ApiModelProperty(value = "销售人员")
    private String userName;
    @ApiModelProperty(value = "商品编码")
    private String proCode;
    @ApiModelProperty(value = "通用名")
    private String proCommonName;
    @ApiModelProperty(value = "商品名称")
    private String proName;
    @ApiModelProperty(value = "规格")
    private String proSpecs;
    @ApiModelProperty(value = "剂型")
    private String proForm;
    @ApiModelProperty(value = "厂家")
    private String proFactoryName;
    @ApiModelProperty(value = "批号")
    private String gssBatchNo;
    @ApiModelProperty(value = "有效期")
    private String gssdValidDate;
    @ApiModelProperty(value = "拆零数量")
    private String proPartUint;
    @ApiModelProperty(value = "单位")
    private String proUnit;
    @ApiModelProperty(value = "批准文号")
    private String proRegisterNo;
    @ApiModelProperty(value = "门店编码")
    private String stoCode;
    @ApiModelProperty(value = "门店名称")
    private String stoShortName;
    @ApiModelProperty(value = "会员姓名")
    private String memberName;
}
