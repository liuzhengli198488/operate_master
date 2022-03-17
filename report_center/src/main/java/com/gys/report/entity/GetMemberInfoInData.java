package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GetMemberInfoInData {
    private static final long serialVersionUID = -8599651801018827232L;
    @ApiModelProperty(value = "加盟商")
    private String client;
    @ApiModelProperty(value = "门店")
    private String brId;
    @ApiModelProperty(value = "会员卡号")
    private String memberCardId;
    @ApiModelProperty(value = "会员卡号集合")
    private String[] memberCarArr;
    @ApiModelProperty(value = "匹配类型 0 模糊匹配 1 精确匹配")
    private String mateType;
    private Integer pageNum;
    private Integer pageSize;

}
