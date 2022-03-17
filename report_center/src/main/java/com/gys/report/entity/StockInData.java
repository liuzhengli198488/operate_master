package com.gys.report.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 库存ResVO
 * @author chenhao
 */
@Data
@ApiModel(value = "库存请求实体")
public class StockInData {
    @ApiModelProperty(value = "加盟商", required = true)
    private String clientId;

    @ApiModelProperty(value = "门店", required = true)
    private String gssBrId;

    @ApiModelProperty(value = "商品编码", required = true)
    private String gssProId;

    @ApiModelProperty(value = "批号")
    private String batchNo;

    @ApiModelProperty(value = "批次")
    private String batch;

    @ApiModelProperty(value = "数量", required = true)
    private BigDecimal num;

    @ApiModelProperty(value = "营业员id", required = true)
    private String empId;

    @ApiModelProperty(value = "单号", required = true)
    private String voucherId;

    @ApiModelProperty(value = "行号", required = true)
    private String serial;

    private String updateDate;

    @ApiModelProperty(value = "多门店", required = true)
    private String[] gssBrIds;

    @ApiModelProperty(value = "连锁公司ID", required = true)
    private String stoChainHead;

    @ApiModelProperty(value = "是否为中药代煎")
    private String crFlag;
}
