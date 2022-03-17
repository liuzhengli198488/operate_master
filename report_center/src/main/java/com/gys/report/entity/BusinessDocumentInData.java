package com.gys.report.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "业务单据查询")
public class BusinessDocumentInData {
    @ApiModelProperty(value = "加盟商")
    private String client;
    @ApiModelProperty(value = "公司编码")
    private String siteCode;
    @ApiModelProperty(value = "公司编码")
    private String[] siteArr;
    @ApiModelProperty(value = "开始日期")
    private String startDate;
    @ApiModelProperty(value = "截至日期")
    private String endDate;
    @ApiModelProperty(value = "发生门店")
    private String store;
    @ApiModelProperty(value = "物流中心")
    private String dc;
    @ApiModelProperty(value = "业务类型")
    private String matType;
    @ApiModelProperty(value = "业务单据号")
    private String dnId;
    @ApiModelProperty(value = "合计金额")
    private String totalAmount;
    @ApiModelProperty(value = "入库地点")
    private String locationCode;
    @ApiModelProperty(value = "生产厂家")
    private String factory;
    @ApiModelProperty(value = "商品编号")
    private String proCodes;
    private String[] proArr;
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

    /**
     * 商品分类
     */
    private List<String> proClassCode;
}
