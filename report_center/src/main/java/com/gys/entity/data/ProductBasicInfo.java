package com.gys.entity.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProductBasicInfo {
    @ApiModelProperty(value = "商品通用编码")
    private String proCode;
    @ApiModelProperty(value = "商品描述")
    private String proDepict;
    @ApiModelProperty(value = "商品规格")
    private String proSpecs;
    @ApiModelProperty(value = "生产厂家")
    private String factoryName;
    @ApiModelProperty(value = "单位")
    private String proUnit;
    @ApiModelProperty(value = "成分分类编码")
    private String proCompClass;
    @ApiModelProperty(value = "成分分类名称")
    private String proCompClassName;
    @ApiModelProperty(value = "成分分类编码")
    private String proBigCompClass;
    @ApiModelProperty(value = "成分分类名称")
    private String proBigCompClassName;
    @ApiModelProperty(value = "成分分类编码")
    private String proMidCompClass;
    @ApiModelProperty(value = "成分分类名称")
    private String proMidCompClassName;
    @ApiModelProperty(value = "商品分类编码")
    private String proBigClass;
    @ApiModelProperty(value = "商品分类")
    private String proBigClassName;
    @ApiModelProperty(value = "商品分类编码")
    private String proMidClass;
    @ApiModelProperty(value = "商品分类")
    private String proMidClassName;
    @ApiModelProperty(value = "商品分类编码")
    private String proClass;
    @ApiModelProperty(value = "商品分类")
    private String proClassName;
}
