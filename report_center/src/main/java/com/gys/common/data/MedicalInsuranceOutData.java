package com.gys.common.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MedicalInsuranceOutData {
    @ApiModelProperty(value = "盘点时间")
    private String inventoryDate;
    @ApiModelProperty(value = "商品编码")
    private String proCode;
    @ApiModelProperty(value = "商品名")
    private String proName;
    @ApiModelProperty(value = "通用名")
    private String proCommonName;
    @ApiModelProperty(value = "生产厂家")
    private String factoryName;
    @ApiModelProperty(value = "规格")
    private String proSpecs;
    @ApiModelProperty(value = "转换比")
    private String changeRatio;
    @ApiModelProperty(value = "包装材料")
    private String material;
    @ApiModelProperty(value = "两次盘点期间进货数量")
    private BigDecimal inputQty;
    @ApiModelProperty(value = "两次盘点期间调入数量")
    private BigDecimal callInQty;
    @ApiModelProperty(value = "两次盘点期间划卡销售数量")
    private BigDecimal cardQty;
    @ApiModelProperty(value = "两次盘点期间自费销售数量")
    private BigDecimal salePayQty;
    @ApiModelProperty(value = "两次盘点期间调出数量")
    private BigDecimal callOutQty;
    @ApiModelProperty(value = "两次盘点期间退货数量")
    private BigDecimal returnGoodsQty;
    @ApiModelProperty(value = "药品实时库存数量")
    private BigDecimal stockQty;
    @ApiModelProperty(value = "医保编码")
    private String proMedId;
    @ApiModelProperty(value = "商品描述")
    private String proDepict;
}
