package com.gys.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 地点信息(门店或者部门)
 * @Author huxinxin
 * @Date 2021/6/15 13:31
 * @Version 1.0.0
 **/
@Data
@ApiModel(value = "SiteBO",description = "地点信息")
public class SiteBO {
    @ApiModelProperty(value = "地点编码")
    private String siteCode;
    @ApiModelProperty(value = "地点名称")
    private String siteName;

}
