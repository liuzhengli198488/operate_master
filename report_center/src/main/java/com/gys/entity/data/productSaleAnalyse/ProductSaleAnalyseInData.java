package com.gys.entity.data.productSaleAnalyse;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProductSaleAnalyseInData {
    @ApiModelProperty(value = "计算日期")
    private String endDate;
    @ApiModelProperty(value = "倒推天数")
    private String analyseDay;
    @ApiModelProperty(value = "店型")
    private String storeType;
    @ApiModelProperty(value = "毛利额是否参与计算 0 否 1 是")
    private String isProfit;
    @ApiModelProperty(value = "计算维度 1 省级维度 2 市级维度 3 客户维度 4 门店维度")
    private String dimension;
    @ApiModelProperty(value = "省（多选）")
    private String[] provinces;
    @ApiModelProperty(value = "市（多选）")
    private String[] citise;
    @ApiModelProperty(value = "客户（多选）")
    private String[] clientIds;
    @ApiModelProperty(value = "客户与门店")
    private String[] storeIds;
    @ApiModelProperty(value = "数量查询")
    private String limitNum;
}
