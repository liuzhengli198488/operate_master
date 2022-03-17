package com.gys.report.entity;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "门店分税率销售明细查询")
public class StoreRateSellOutData {
    @ApiModelProperty(value = "业务类型")
    private String matType;
    @ApiModelProperty(value = "门店编码")
    private String brId;
    @ApiModelProperty(value = "门店名称")
    private String stoName;
    @ApiModelProperty(value = "销售单号")
    private String billNo;
    @ApiModelProperty(value = "纳税性质")
    private String stoTaxClass;
    @ApiModelProperty(value = "商品编码")
    private String proId;
    @ApiModelProperty(value = "商品名称")
    private String proCommonName;
    @ApiModelProperty(value = "税率")
    private BigDecimal taxCodeValue;
    @ApiModelProperty(value = "数量")
    private BigDecimal qty;
    @ApiModelProperty(value = "应收金额")
    private BigDecimal amountReceivable;
    @ApiModelProperty(value = "实收金额")
    private BigDecimal amt;
    @ApiModelProperty(value = "折扣额")
    private BigDecimal deduction;
    @ApiModelProperty(value = "去税销售额")
    private BigDecimal removeTaxSale;
    @ApiModelProperty(value = "去税成本额")
    private BigDecimal movPrices;
    @ApiModelProperty(value = "含税成本额")
    private BigDecimal includeTaxSale;
    @ApiModelProperty(value = "毛利额")
    private BigDecimal grossProfitMargin;
    @ApiModelProperty(value = "毛利率")
    private String grossProfitRate;
    @ApiModelProperty(value = "加点后去税成本额")
    private BigDecimal addMovPrices;
    @ApiModelProperty(value = "加点后含税成本额")
    private BigDecimal addIncludeTaxSale;
    @ApiModelProperty(value = "加点后毛利额")
    private BigDecimal addGrossProfitMargin;
    @ApiModelProperty(value = "加点后毛利率")
    private String addGrossProfitRate;
    @ApiModelProperty(value = "销售时间")
    private String saleDate;
    @ApiModelProperty(value = "商品规格")
    private String specs;
    @ApiModelProperty(value = "商品厂家")
    private String factoryName;
    @ApiModelProperty(value = "商品产地")
    private String place;
    @ApiModelProperty(value = "商品大类编码")
    private String bigClass;
    @ApiModelProperty(value = "商品中类编码")
    private String midClass;
    @ApiModelProperty(value = "商品分类编码")
    private String proClass;
    @ApiModelProperty(value = "营业员编码")
    private String userId;
    @ApiModelProperty(value = "营业员姓名")
    private String userName;

    /**
     * 门店属性
     */
    private String stoAttribute;

    /**
     * 店效级别
     */
    private String storeEfficiencyLevel;

    /**
     * 是否医保店
     */
    private String stoIfMedical;

//    /**
//     * 纳税属性
//     */
//    private String stoTaxClass;

    /**
     * 是否直营管理
     */
    private String directManaged;

    /**
     * DTP
     */
    private String stoIfDtp;

    /**
     *管理区域
     */
    private String managementArea;

    /**
     * 店型
     */
    private String shopType;
}
