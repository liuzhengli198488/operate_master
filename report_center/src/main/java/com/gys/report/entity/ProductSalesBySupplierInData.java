package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ProductSalesBySupplierInData {
    private String client;
    @ApiModelProperty(value = "商品编码")
    private String proCode;
    private List<String> proArr;
    @ApiModelProperty(value = "门店编码")
    private String brId;
    @ApiModelProperty(value = "地点批量查询")
    private String[] siteArr;
    @ApiModelProperty(value = "开始日期")
    private String startDate;
    @ApiModelProperty(value = "截至日期")
    private String endDate;
    @ApiModelProperty(name = "商品分类")
    private String proClass;
    @ApiModelProperty(value = "商品分类查询")
    private String[][] classArr;
    private List<String> classArrs;
    @ApiModelProperty(value = "供应商编码")
    private String supplierCode;
    @ApiModelProperty(value = "生产厂家")
    private String factory;
    @ApiModelProperty(value = "是否医保")
    private String medProdctStatus;
    @ApiModelProperty(value = "效期天数")
    private String expiryDay;
    @ApiModelProperty(value = "页数")
    private Integer pageNum;
    @ApiModelProperty(value = "行数")
    private Integer pageSize;

    
    //商品自分类
    private String[] prosClass;
    //销售级别
    private String[] saleClass;
    //商品定位
    private String[] proPosition;
    //禁止采购
    private String purchase;
    //自定义1
    private String[] zdy1;
    //自定义2
    private String[] zdy2;
    //自定义3
    private String[] zdy3;
    //自定义4
    private String[] zdy4;
    //自定义5
    private String[] zdy5;
}
