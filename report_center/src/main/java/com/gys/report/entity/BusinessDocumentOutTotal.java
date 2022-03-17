package com.gys.report.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@ApiModel(value = "业务单据查询")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BusinessDocumentOutTotal {

    @ApiModelProperty(value = "去税金额")
    private BigDecimal batAmt;
    @ApiModelProperty(value = "税金")
    private BigDecimal rateBat;
    @ApiModelProperty(value = "合计金额")
    private BigDecimal totalAmount;
    @ApiModelProperty(value = "数量")
    private BigDecimal qty;

    /**
     * 零售额
     */
    private BigDecimal proLSAmt;
}
