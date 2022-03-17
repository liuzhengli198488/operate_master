package com.gys.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProductMatchInData {
    @ApiModelProperty(value = "客户编码")
    private String clientId;
    @ApiModelProperty(value = "门店编码")
    private String stoCode;
    @ApiModelProperty(value = "连锁总部")
    private String chainHead;
    @ApiModelProperty(value = "数据来源")
    private String fileName;
    @ApiModelProperty(value = "匹配状态 0 未匹配 1 部分匹配 2 完全匹配")
    private String matchStatus;
    @ApiModelProperty(value = "抽取比例")
    private String matchScale;
    @ApiModelProperty(value = "导入开始时间")
    private String matchStartDate;
    @ApiModelProperty(value = "导入结束时间")
    private String matchEndDate;
    @ApiModelProperty(value = "导入时间数组 例如：[matchStartDate,matchStartDate]")
    private String[] matchDateArr;
}
