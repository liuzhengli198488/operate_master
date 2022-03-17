package com.gys.report.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "供应商往来单据查询")
public class SupplierDealOutData {
    @ApiModelProperty(value = "client")
    private String CLIENT;

    @ApiModelProperty(value = "商品编码")
    private String proCode;
    @ApiModelProperty(value = "商品名称")
    private String proName;
    @ApiModelProperty(value = "规格")
    private String specs;
    @ApiModelProperty(value = "单位")
    private String unit;
    @ApiModelProperty(value = "生产厂家")
    private String factoryName;
    @ApiModelProperty(value = "业务类型")
    private String matType;
    @ApiModelProperty(value = "业务名称")
    private String matName;
    @ApiModelProperty(value = "发生时间")
    private String postDate;
    @ApiModelProperty(value = "业务单据号")
    private String dnId;
    @ApiModelProperty(value = "去税金额")
    private BigDecimal batAmt;
    @ApiModelProperty(value = "税金")
    private BigDecimal rateBat;
    @ApiModelProperty(value = "税率")
    private String taxCodeValue;
    @ApiModelProperty(value = "合计金额")
    private BigDecimal totalAmount;
    @ApiModelProperty(value = "供应商编码")
    private String supCode;
    @ApiModelProperty(value = "供应商名称")
    private String supName;
    @ApiModelProperty(value = "入库地点编码")
    private String siteCode;
    @ApiModelProperty(value = "入库地点")
    private String siteName;
    @ApiModelProperty(value = "门店编码")
    private String stoCode;
    @ApiModelProperty(value = "门店名称")
    private String stoName;
    @ApiModelProperty(value = "数量")
    private BigDecimal qty;
    @ApiModelProperty(value = "单价")
    private BigDecimal price;
    @ApiModelProperty(value = "开单金额")
    private BigDecimal billingAmount;
    @ApiModelProperty(value = "批号")
    private String batchNo;
    @ApiModelProperty(value = "姓名")
    private String  userName;
    @ApiModelProperty(value = "编码")
    private String  userCode;
    @ApiModelProperty(value = "订单号")
    private String  poId;
    @ApiModelProperty(value = "营业员")
    private String  suppName;

    @ApiModelProperty("发票价")
    private String batFph;

    @ApiModelProperty("发票金额")
    private String batFpje;

    @ApiModelProperty("抬头备注")
    private String poHeadRemark;

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

    @ApiModelProperty(value = "供应商业务员")
    private String  poSupplierSalesman;

    @ApiModelProperty(value = "供应商业务员名称")
    private String  poSupplierSalesmanName;

    /**
     * 税率
     */
    private String taxRate;

    /**
     * 生产日期
     */
    private String  batProductDate;

    /**
     * 有效期
      */
    private String batExpiryDat;
}
