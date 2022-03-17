package com.gys.entity.wk.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GetProductThirdlyInData {
    private static final long serialVersionUID = -8599651801018827232L;
    @ApiModelProperty(value = "加盟商")
    private String client;
    @ApiModelProperty(value = "门店")
    private String proSite;
    @ApiModelProperty(value = "查询内容")
    private String content;
    @ApiModelProperty(value = "商品集合")
    private String[] proArr;
    @NotNull
    @ApiModelProperty(value = "类型")
    private String type;
    @ApiModelProperty(value = "是否特殊商品 1")
    private String isSpecial;
    @ApiModelProperty(value = "匹配类型 0 模糊匹配 1 精确匹配")
    private String mateType;
    private Integer pageNum;
    private Integer pageSize;
//    @ApiModelProperty(value = "查询条件")
//    private List<ProSearchConditionInfo> searchConditionInfos;

//    @ApiModelProperty(value = "查询条件")
//    private List<StringBuilder> strSqls;
//    @ApiModelProperty(value = "需要查询的列")
//    private List<String> lableArr;

}
