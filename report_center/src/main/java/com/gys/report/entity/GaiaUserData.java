package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@Data
public class GaiaUserData implements Serializable {
    private static final long serialVersionUID = -6011181432816261853L;

    @ApiModelProperty(value = "员工编号")
    private String userId;

    @ApiModelProperty(value = "加盟商")
    private String client;

    @ApiModelProperty(value = "姓名")
    private String userNam;

    @ApiModelProperty(value = "在职状态:0，在职；1离职；2停用，默认0")
    private String userSta;
}
