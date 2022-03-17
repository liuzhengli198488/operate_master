package com.gys.report.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class InventoryChangeSummaryInData {
    @ApiModelProperty(value = "开始日期")
    private String startDate;
    @ApiModelProperty(value = "截至日期")
    private String endDate;
    @ApiModelProperty(value = "发生地点")
    private String siteCode;
    @ApiModelProperty(value = "地点批量查询")
    private String[] siteArr;
    @ApiModelProperty(value = "商品分类查询")
    private String[][] classArr;
    private List<String> classArrs;
    @ApiModelProperty(value = "商品编码")
    private String proCode;
    @ApiModelProperty(value = "商品编码批量查询")
    private String[] proArr;
    @ApiModelProperty(value = "单据类型")
    private String type;
    @ApiModelProperty(value = "批号")
    private String batchNo;
    @ApiModelProperty(value = "商品分类")
    private String proClass;
    @ApiModelProperty(value = "是否医保")
    private String medProdctStatus;
    @ApiModelProperty(value = "生产厂家")
    private String factoryName;
    //期初日期
    private String qcDate;
    private String client;
    private Integer pageNum;
    private Integer pageSize;
}
