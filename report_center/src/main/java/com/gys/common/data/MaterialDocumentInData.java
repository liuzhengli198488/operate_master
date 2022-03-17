package com.gys.common.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class MaterialDocumentInData {
    @ApiModelProperty(value = "业务类型 1：已调用接口成功，未生成凭证 2：采购  3：")
    private String type;
    @ApiModelProperty(value = "成功标识   1：成功  2：失败")
    private String successFlag;
    @ApiModelProperty(value = "加盟商")
    private String francName;
    @ApiModelProperty(value = "加盟商地址")
    private String francAddr;
    @ApiModelProperty(value = "订单号")
    private String matPoId;
    @ApiModelProperty(value = "业务单号")
    private String matDnId;
    @ApiModelProperty(value = "过账日期起")
    private String startTime;
    @ApiModelProperty(value = "过账日期止")
    private String endTime;

    private String[] matPoIdList;

    private String[] matDnIdList;

    private String cliEnt;

    private Integer pageNum;
    private Integer pageSize;
}
