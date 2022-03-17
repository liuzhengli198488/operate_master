package com.gys.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * VIEW实体类
 *
 * @author Blade
 * @since 2020-05-20
 */
@Data
@Table(name = "GAIA_WMS_VIEW_TJ_XHL")
@ApiModel(value = "GaiaViewWmsTjXhl对象", description = "VIEW")
public class GaiaViewWmsTjXhl implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "加盟商")
    @Column(name = "CLIENT")
    private String client;

    @ApiModelProperty(value = "地点")
    @Column(name = "PRO_SITE")
    private String porsite;

    @ApiModelProperty(value = "客户号")
    @Column(name = "CUSTOMERNO")
    private String customerno;

    @ApiModelProperty(value = "客户名称")
    @Column(name = "CUSTOMERNAME")
    private String customername;

    @ApiModelProperty(value = "请货日期")
    @Column(name = "PLEASEORDERDATE")
    private String pleaseorderdate;

    @ApiModelProperty(value = "订单号")
    @Column(name = "DELIVERYORDERNO")
    private String deliveryorderno;

    @ApiModelProperty(value = "开单日期")
    @Column(name = "OPENORDERDATE")
    private String openorderdate;

    @ApiModelProperty(value = "过账日期")
    @Column(name = "POSTINGDATE")
    private String postingdate;

    @ApiModelProperty(value = "配送单号")
    @Column(name = "DELIVERYNO")
    private String deliveryno;

    @ApiModelProperty(value = "是否过账")
    @Column(name = "ISPOST")
    private String ispost;

    @ApiModelProperty(value = "商品编码")
    @Column(name = "COMMODITYCODE")
    private String commoditycode;

    @ApiModelProperty(value = "商品大类")
    @Column(name = "COMMODITYCATEGORY")
    private String commoditycategory;

    @ApiModelProperty(value = "商品名称")
    @Column(name = "COMMODITYNAME")
    private String commodityname;

    @ApiModelProperty(value = "规格")
    @Column(name = "SPECIFICATIONS")
    private String specifications;

    @ApiModelProperty(value = "生产企业")
    @Column(name = "MANUFACTURER")
    private String manufacturer;

    @ApiModelProperty(value = "计量单位")
    @Column(name = "UNIT")
    private String unit;

    @ApiModelProperty(value = "订单数量")
    @Column(name = "ORDERQUANTITY")
    private BigDecimal orderquantity;

    @ApiModelProperty(value = "配货单原数量")
    @Column(name = "DELIVERYQUANTITY")
    private BigDecimal deliveryquantity;

    @ApiModelProperty(value = "配送单过账数量")
    @Column(name = "DELIVERYPOSTEDQUANTITYRATE")
    private BigDecimal deliverypostedquantityrate;

    @ApiModelProperty(value = "移动平均价")
    @Column(name = "MATMOVPRICE")
    private BigDecimal matmovprice;

    @ApiModelProperty(value = "订单价")
    @Column(name = "ORDER_PRICE")
    private BigDecimal orderPrice;

    private BigDecimal lineNo;

    //药德客户id
    private String clientId;

    //药德客户名称
    private String clientName;

    //配送中心id
    private String dcId;

    //配送中心名称
    private String dcName;

    //补货方式id
    private String replenishStyle;

    //补货方式中文名称
    private String replenishStyleStr;
}
