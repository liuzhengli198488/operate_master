package com.gys.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class AreaOutData implements Serializable {

    /**
     * 地区编号
     */
    @ApiModelProperty(value="地区编号")
    private String areaId;

    /**
     * 地区名称
     */
    @ApiModelProperty(value="地区名称")
    private String areaName;

    /**
     * 省市标志 1-省 2-市
     */
    @ApiModelProperty(value="省市标志 1-省 2-市")
    private String level;

    /**
     * 父级地区编号
     */
    @ApiModelProperty(value="父级地区编号")
    private String parentId;
}
