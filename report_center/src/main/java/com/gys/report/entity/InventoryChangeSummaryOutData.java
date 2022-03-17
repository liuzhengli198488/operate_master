package com.gys.report.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
public class InventoryChangeSummaryOutData {
    @ApiModelProperty(value = "")
    private String client;
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
    @ApiModelProperty(value = "采购金额")
    private BigDecimal purchaseAmount;
    @ApiModelProperty(value = "退货金额")
    private BigDecimal returnAmount;
    @ApiModelProperty(value = "销售金额")
    private BigDecimal saleAmount;
    @ApiModelProperty(value = "损溢金额")
    private BigDecimal profitLossAmount;
    @ApiModelProperty(value = "采购数量")
    private BigDecimal purchaseQty;
    @ApiModelProperty(value = "退货数量")
    private BigDecimal returnQty;
    @ApiModelProperty(value = "销售数量")
    private BigDecimal saleQty;
    @ApiModelProperty(value = "损溢数量")
    private BigDecimal profitLossQty;
    @ApiModelProperty(value = "期末数量")
    private BigDecimal endQty;
    @ApiModelProperty(value = "期末金额")
    private BigDecimal endAmt;
    @ApiModelProperty(value = "期初数量")
    private BigDecimal startQty;
    @ApiModelProperty(value = "期初金额")
    private BigDecimal startAmt;
    @ApiModelProperty(value = "调剂数量")
    private BigDecimal adjustQty;
    @ApiModelProperty(value = "调剂金额")
    private BigDecimal adjustAmt;
    @ApiModelProperty(value = "配送数量")
    private BigDecimal distributionQty;
    @ApiModelProperty(value = "配送金额")
    private BigDecimal distributionAmt;
    @ApiModelProperty(value = "医保编码")
    private String medProdctCode;
    @ApiModelProperty(value = "商品大类编码")
    private String bigClass;
    @ApiModelProperty(value = "商品中类编码")
    private String midClass;
    @ApiModelProperty(value = "商品分类编码")
    private String proClass;
    @ApiModelProperty(value = "是否医保")
    private String ifMed;
    @ApiModelProperty(value = "毛利额")
    private BigDecimal grossProfitMargin;
    @ApiModelProperty(value = "毛利率")
    private BigDecimal grossProfitRate;
    @ApiModelProperty(value = "实收金额")
    private BigDecimal amt;

    @ApiModelProperty(value = "期间期初导入金额")
    private BigDecimal qcAmt;
    @ApiModelProperty(value = "期间期初导入数量")
    private BigDecimal qcQty;
}
