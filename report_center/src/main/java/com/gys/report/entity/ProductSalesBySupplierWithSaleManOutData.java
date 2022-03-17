package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSalesBySupplierWithSaleManOutData {
    @ApiModelProperty(value = "商品编码")
    private String proCode;
    @ApiModelProperty(value = "商品名称")
    private String proName;
    @ApiModelProperty(value = "通用名称")
    private String proCommonName;
    @ApiModelProperty(value = "规格")
    private String specs;
    @ApiModelProperty(value = "效期天数")
    private String expiryDay;
    @ApiModelProperty(value = "有效期")
    private String expiryDate;
    @ApiModelProperty(value = "单位")
    private String unit;
    @ApiModelProperty(value = "生产厂家")
    private String factoryName;
    @ApiModelProperty(value = "批次")
    private String batch;
    @ApiModelProperty(value = "供应商编码")
    private String supplierCode;
    @ApiModelProperty(value = "供应商名称")
    private String supplierName;
    @ApiModelProperty(value = "批次异动表数量")
    private BigDecimal qty;
    @ApiModelProperty(value = "销售数量占比 暂时不加")
    private BigDecimal qtyProportion;
    @ApiModelProperty(value = "应收金额")
    private BigDecimal amountReceivable;
    @ApiModelProperty(value = "实收金额")
    private BigDecimal amt;
    @ApiModelProperty(value = "成本额")
    private BigDecimal includeTaxSale;
    @ApiModelProperty(value = "毛利额")
    private BigDecimal grossProfitMargin;
    @ApiModelProperty(value = "毛利率")
    private BigDecimal grossProfitRate;
    @ApiModelProperty(value = "门店编码")
    private String stoCode;
    @ApiModelProperty(value = "门店名称")
    private String stoName;

    //商品自分类
    private String prosClass;
    //销售级别
    private String saleClass;
    //商品定位
    private String proPosition;
    //禁止采购
    private String purchase;
    //自定义1
    private String zdy1;
    //自定义2
    private String zdy2;
    //自定义3
    private String zdy3;
    //自定义4
    private String zdy4;
    //自定义5
    private String zdy5;
    @ApiModelProperty(value = "商品描述")
    private String proDepict;
    @ApiModelProperty(value = "供应商业务员编码")
    private String gssCode;
    @ApiModelProperty(value = "供应商业务员名称")
    private String gssName;
}
