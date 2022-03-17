package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * 返回实体
 *
 * @author xiaoyuan on 2020/10/14
 */
@Data
public class DifferenceResultDetailedQueryOutData implements Serializable {
    private static final long serialVersionUID = 3063264879910809470L;

    @ApiModelProperty(value = "序号")
    private Integer index;

    @ApiModelProperty(value = "门店编码")
    private String brId;

    @ApiModelProperty(value = "门店名称")
    private String brName;

    @ApiModelProperty(value = "纳税属性")
    private String taxAttributes;

    @ApiModelProperty(value = "盘点单号")
    private String inventoryOrderNumber;

    @ApiModelProperty(value = "商品编码")
    private String proCode;

    @ApiModelProperty(value = "商品名称")
    private String proName;

    @ApiModelProperty(value = "零售价格")
    private String retailPrice;

    @ApiModelProperty(value = "税额")
    private String tax;

    @ApiModelProperty(value = "差异数量")
    private String numberOfDifferences;

    @ApiModelProperty(value = "差异成本额")
    private String varianceCost;

    @ApiModelProperty(value = "差异零售额")
    private String differentialRetailSales;

    @ApiModelProperty(value = "过账日期")
    private String postingDate;

    @ApiModelProperty(value = "中药")
    private String CMNAME;
    @ApiModelProperty(value = "规格")
    private String proSpecs;
    @ApiModelProperty(value = "通用名称")
    private String  proCommonname;
    @ApiModelProperty(value = "厂家")
    private String proFactoryName;
    @ApiModelProperty(value = "审核日期")
    private String gspcExamineDate;
}
