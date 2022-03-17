package com.gys.report.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "进销存明细请求实体")
public class InvoicingInData {
    @ApiModelProperty(value = "商品编码")
    private String gssbProId;

    @ApiModelProperty(value = "商品编码批量查询")
    private String[] proArr;

    @ApiModelProperty(value = "批号")
    private String gssbBatchNo;

    @ApiModelProperty(value = "起始日期")
    private String gssbStartDate;

    @ApiModelProperty(value = "异动类型", allowableValues = "销售:SD, 验收:YS, 退库:TD, 退供应商:GD, 调剂:TJ, 损溢:SYS")
    private String type;

    @ApiModelProperty(value = "库存数量")
    private BigDecimal stockNum;

    @ApiModelProperty(value = "是否医保")
    private String ifMed;

    @ApiModelProperty(value = "结束日期")
    private String gssbFinishDate;

    @ApiModelProperty(hidden = true)
    private String clientId;

    @ApiModelProperty(hidden = true)
    private String gssbBrId;
    private String flag; // 0 开启 1 不开启 权限

    List<String> inventoryList;
}
