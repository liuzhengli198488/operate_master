package com.gys.common.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class NoSaleItemOutData {
    @ApiModelProperty(value = "商品编码")
    private String proCode;
    @ApiModelProperty(value = "商品名称")
    private String proName;
    @ApiModelProperty(value = "通用名称")
    private String proCommonName;
    @ApiModelProperty(value = "规格")
    private String proSpecs;
    @ApiModelProperty(value = "生产厂家")
    private String factoryName;
    @ApiModelProperty(value = "单位")
    private String proUnit;
    @ApiModelProperty(value = "是否医保")
    private String isMedOrNot;
    @ApiModelProperty(value = "库存数量")
    private BigDecimal stockQty;
    @ApiModelProperty(value = "库存成本额")
    private BigDecimal costAmt;
    @ApiModelProperty(value = "期间销售数量")
    private BigDecimal gssdQty;

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
    //入库日期
    private String inDate;
    //入库天数
    private String days;

}
