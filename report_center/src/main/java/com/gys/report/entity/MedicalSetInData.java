package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class MedicalSetInData {
    @ApiModelProperty(value = "加盟商")
    private String clientId;
    @ApiModelProperty(value = "门店编码")
    private List<String> stoCode;
    @ApiModelProperty(value = "销售单号")
    private String billNo;
    @ApiModelProperty(value = "商品编号")
    private List<String> proId;
    @ApiModelProperty(value = "起始日期")
    private String startDate;
    @ApiModelProperty(value = "结束日期")
    private String endDate;

    @ApiModelProperty(value = "结算Id")
    private String setlId;
    @ApiModelProperty(value = "就诊ID")
    private String mdtrtId;

    @ApiModelProperty(value = "人员姓名")
    private String psnName;

   @ApiModelProperty(value = "人员证件类型")
    private String psnCertType;
    @ApiModelProperty(value = "证件编号")
    private String certno;

    private Integer pageNum;
    private Integer pageSize;
}
