package com.gys.report.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class StoreProductSaleClientInData {
    private String client;
    @ApiModelProperty(value = "商品编码")
    private String proId;
    @ApiModelProperty(value = "商品编码")
    private List<String> proArr;
    @ApiModelProperty(value = "门店编码")
    private String gsstBrId;
    @ApiModelProperty(value = "地点批量查询")
    private String[] siteArr;
    @ApiModelProperty(value = "开始时间")
    private String startDate;
    @ApiModelProperty(value = "结束时间")
    private String endDate;
    @ApiModelProperty(value = "厂家")
    private String factory;
    @ApiModelProperty(value = "税率")
    private String taxCodeValue;
    @ApiModelProperty(value = "最大毛利率")
    private String grossProfitRateMax;
    @ApiModelProperty(value = "最小毛利率")
    private String grossProfitRateMin;
    @ApiModelProperty(value = "商品分类查询")
    private String[][] classArr;
    @ApiModelProperty(value = "商品分类查询")
    private List<String> classArrs;
    @ApiModelProperty(value = "是否批发")
    private String noWholesale;
    private Integer pageNum;
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

    @ApiModelProperty(value = "分类id")
    private String gssgId;

    @ApiModelProperty(hidden = true)
    private List<String> gssgIds;

    @ApiModelProperty(value = "分类类型")
    private String stoGssgType;

    @ApiModelProperty(hidden = true)
    private List<GaiaStoreCategoryType> stoGssgTypes;

    @ApiModelProperty(value = "门店属性")
    private String stoAttribute;

    @ApiModelProperty(hidden = true)
    private List<String> stoAttributes;

    @ApiModelProperty(value = "是否医保店")
    private String stoIfMedical;

    @ApiModelProperty(hidden = true)
    private List<String> stoIfMedicals;

    @ApiModelProperty(value = "纳税属性")
    private String stoTaxClass;

    @ApiModelProperty(hidden = true)
    private List<String> stoTaxClasss;

    @ApiModelProperty(value = "DTP")
    private String stoIfDtp;

    @ApiModelProperty(hidden = true)
    private List<String> stoIfDtps;

    /**
     * 特殊属性
     */
    private List<String> proTssx;

    //库存量过滤类型 1:总库存量 2:门店库存 3:仓库库存
    private Integer countType;

    //库存量开始
    private Double countStart;

    //库存量结束
    private Double countEnd;
}
