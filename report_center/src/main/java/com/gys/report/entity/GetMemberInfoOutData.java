package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class GetMemberInfoOutData implements Serializable {
    @ApiModelProperty(value = "会员卡号")
    private String memberCardId;
    @ApiModelProperty(value = "会员姓名")
    private String memberName;
    @ApiModelProperty(value = "手机号")
    private String mobile;
    @ApiModelProperty(value = "性别")
    private String sex;
    @ApiModelProperty(value = "年龄")
    private String age;
}
