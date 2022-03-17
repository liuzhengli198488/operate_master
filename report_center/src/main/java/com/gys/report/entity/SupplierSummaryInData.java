package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SupplierSummaryInData {
    private String client;

    private List<String> proArr;

    @ApiModelProperty(value = "开始日期")
    private String startDate;

    @ApiModelProperty(value = "截至日期")
    private String endDate;

    @ApiModelProperty(value = "门店编码")
    private String brId;

    @ApiModelProperty(value = "商品编码")
    private String proCode;

    @ApiModelProperty(value = "供应商编码")
    private String supplierCode;

    @ApiModelProperty(value = "地点批量查询")
    private String[] siteArr;

    // 按业务员条件
    @ApiModelProperty(name = "按业务员(0:否，1：是)")
    private String withSaleMan;

    @ApiModelProperty(name = "业务员code列表")
    private List<String> gssCodeList;

    // 按商品条件
    @ApiModelProperty(name = "按商品(0:否，1：是)")
    private String withPro;

    @ApiModelProperty(name = "商品分类")
    private String proClass;

    @ApiModelProperty(value = "商品分类查询")
    private String[][] classArr;
    private List<String> classArrs;

    @ApiModelProperty(value = "生产厂家")
    private String factory;

    // 按门店条件
    @ApiModelProperty(name = "按门店(0:否，1：是)")
    private String withSto;

    @ApiModelProperty(value = "门店属性")
    private String stoAttribute;

    @ApiModelProperty(hidden = true)
    private List<String> stoAttributes;

    @ApiModelProperty(value = "是否医保店")
    private String stoIfMedical;

    @ApiModelProperty(hidden = true)
    private List<String> stoIfMedicals;

    @ApiModelProperty(value = "DTP")
    private String stoIfDtp;

    @ApiModelProperty(hidden = true)
    private List<String> stoIfDtps;

    @ApiModelProperty(value = "纳税属性")
    private String stoTaxClass;

    @ApiModelProperty(hidden = true)
    private List<String> stoTaxClasss;

    @ApiModelProperty(value = "分类类型(铺货店型,店效级别,是否直营管理,管理区域)")
    private String stoGssgType;

    @ApiModelProperty(hidden = true)
    private List<GaiaStoreCategoryType> stoGssgTypes;



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
