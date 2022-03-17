package com.gys.entity.renhe;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
public class StoreProductSaleSummaryOutData {
    @ApiModelProperty(value = "门店编码")
    private String stoCode;
    @ApiModelProperty(value = "门店名称")
    private String stoName;
    @ApiModelProperty(value = "批号")
    private String batchNo;
    @ApiModelProperty(value = "商品编码")
    private String proCode;
    @ApiModelProperty(value = "商品名称")
    private String proName;
    @ApiModelProperty(value = "商品通用名称")
    private String proCommonName;
    @ApiModelProperty(value = "商品规格")
    private String specs;
    @ApiModelProperty(value = "商品厂家")
    private String factoryName;
    @ApiModelProperty(value = "商品产地")
    private String place;
    @ApiModelProperty(value = "单位")
    private String unit;
    @ApiModelProperty(value = "是否医保")
    private String ifMed;
    @ApiModelProperty(value = "医保编码")
    private String proMedId;

    @ApiModelProperty(value = "线上销售数量")
    private BigDecimal onlineSaleQty;
    @ApiModelProperty(value = "线上实收金额")
    private BigDecimal onlineAmt;
    @ApiModelProperty(value = "线上毛利额")
    private BigDecimal onlineGrossAmt;
    @ApiModelProperty(value = "线上成本额")
    private BigDecimal onlineMov;
    @ApiModelProperty(value = "线上毛利率")
    private BigDecimal onlineGrossRate;

    @ApiModelProperty(value = "商品大类编码")
    private String bigClass;
    @ApiModelProperty(value = "商品中类编码")
    private String midClass;
    @ApiModelProperty(value = "商品分类编码")
    private String proClass;
    @ApiModelProperty(value = "销项税率")
    private String proOutputTax;
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
    private BigDecimal grossProfitRate;
    @ApiModelProperty(value = "加点后去税成本额")
    private BigDecimal addMovPrices;
    @ApiModelProperty(value = "加点后含税成本额")
    private BigDecimal addIncludeTaxSale;
    @ApiModelProperty(value = "加点后毛利额")
    private BigDecimal addGrossProfitMargin;
    @ApiModelProperty(value = "加点后毛利率")
    private BigDecimal addGrossProfitRate;
    @ApiModelProperty(value = "是否批发")
    private String noWholesale;

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

    /**
     * 是否直营管理
     */
    private String directManaged;

    /**
     *纳税属性
     */
    private String stoTaxClass;

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

    /**
     * 特殊属性
     */
    private String proTssx;

    /**
     * 国际条形码1
     */
    private String proBarcode;

    //平均销售价
    private BigDecimal averagePrice;

    //零售价
    private BigDecimal retailPrice;

    //会员价
    private BigDecimal memberPrice;

    //会员日价
    private BigDecimal memberDayPrice;

    //最新成本价
    private BigDecimal newestPrice;

    //移动平均价
    private BigDecimal movePrice;

    //库存量
    private BigDecimal sumStock;

    //周转天数
    private BigDecimal turnoverDays;
}
