package com.gys.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LoginMessageInData {
    @ApiModelProperty(value = "加盟商")
    private String clientId;
    @ApiModelProperty(value = "登录用户")
    private String userId;
    @ApiModelProperty(value = "门店编码")
    private String stoCode;
    @ApiModelProperty(value = "每页数量")
    private Integer pageSize;
    @ApiModelProperty(value = "页数")
    private Integer pageNum;
}
