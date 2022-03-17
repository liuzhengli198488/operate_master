package com.gys.report.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "门店分税率销售明细查询")
public class StoreRateSellInData {
    @ApiModelProperty(value = "加盟商")
    private String client;
    @ApiModelProperty(value = "开始日期")
    private String startDate;
    @ApiModelProperty(value = "截至日期")
    private String endDate;
    @ApiModelProperty(value = "入库地点")
    private String siteCode;
    @ApiModelProperty(value = "门店编码")
    private String gsstBrId;
    @ApiModelProperty(value = "商品编码")
    private String proId;
    @ApiModelProperty(value = "税率编码")
    private String taxCode;
    @ApiModelProperty(value = "税率编码")
    private String taxCodeValue;
    @ApiModelProperty(value = "最大毛利率")
    private String grossProfitRateMax;
    @ApiModelProperty(value = "最小毛利率")
    private String grossProfitRateMin;
    @ApiModelProperty(value = "商品大类编码")
    private String bigClass;
    @ApiModelProperty(value = "商品中类编码")
    private String midClass;
    @ApiModelProperty(value = "商品分类编码")
    private String proClass;
    @ApiModelProperty(value = "营业员编码")
    private String userId;
    private Integer pageNum;
    private Integer pageSize;

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
}
