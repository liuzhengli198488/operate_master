//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPayTypeOutData {
    @ApiModelProperty(value = "加盟商")
    private String clientId;
    @ApiModelProperty(value = "门店")
    private String gspmBrId;
    @ApiModelProperty(value = "编号")
    private String gspmId;
    @ApiModelProperty(value = "编号字段")
    private String gspmKey;
    @ApiModelProperty(value = "名称")
    private String gspmName;
    @ApiModelProperty(value = "类型")
    private String gspmType;
    private BigDecimal payAmount;
    @ApiModelProperty(value = "储值卡充值是否可用")
    private String gspmRecharge;
    @ApiModelProperty(value = "财务客户编码")
    private String gspmFiId;
    @ApiModelProperty(value = "备注")
    private String gspmRemark;
    @ApiModelProperty(value = "备注1")
    private String gspmRemark1;
    @ApiModelProperty(value = "是否可修改 1: 可改 0:不可改")
    private String isUpdate;
}
