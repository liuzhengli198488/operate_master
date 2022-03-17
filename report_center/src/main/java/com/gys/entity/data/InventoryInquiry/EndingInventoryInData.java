package com.gys.entity.data.InventoryInquiry;

import com.gys.report.entity.GaiaStoreCategoryType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description 期末库存查询 入参
 * @Author huxinxin
 * @Date 2021/5/26 10:22
 * @Version 1.0.0
 **/
@Data
public class EndingInventoryInData {
    private String clientId;

    @ApiModelProperty(value = "商品编码")
    private String proCode;

    @ApiModelProperty(value = "商品编码批量查询")
    private String[] proArr;

    @ApiModelProperty(value = "商品分类查询")
    private String[][] classArr;
    private List<String> classArrs;

    @ApiModelProperty(value = "生产厂家")
    private String factory;

    @ApiModelProperty(value = "效期天数")
    private String expiryData;

    @ApiModelProperty(value = "月末时间")
    private String  endMonth;

    @ApiModelProperty(value = "仓库月初时间")
    private String  dcDate;

    @ApiModelProperty(value = "日期")
    private String recordDate;

    @ApiModelProperty(value = "地点批量查询")
    private String[] siteArr;

    @ApiModelProperty(value = "供应商编码")
    private String supplierCode;

    @ApiModelProperty(value = "是否批号查询 0:是 / 1:否")
    private String type;

    @ApiModelProperty(value = "是否查询0库存 0:是 / 1:否")
    private String noQtyZero;

    @ApiModelProperty(value = "是否查询0成本 0:是 / 1:否")
    private String noPriceZero;

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
