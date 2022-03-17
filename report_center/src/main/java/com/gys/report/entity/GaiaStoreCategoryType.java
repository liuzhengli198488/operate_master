package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @desc:
 * @author: ZhangChi
 * @createTime: 2021/12/20 11:01
 */
@Data
public class GaiaStoreCategoryType implements Serializable {
    @ApiModelProperty(value = "门店id")
    private String gssgBrId;

    @ApiModelProperty(value = "分类类型")
    private String gssgType;

    @ApiModelProperty(value = "分类类型名称")
    private String gssgTypeName;

    @ApiModelProperty(value = "分类id")
    private String gssgId;

    @ApiModelProperty(value = "分类名称")
    private String gssgIdName;

    @ApiModelProperty(value = "门店code")
    private String stoCode;

    @ApiModelProperty(value = "门店名称")
    private String stoName;
}
