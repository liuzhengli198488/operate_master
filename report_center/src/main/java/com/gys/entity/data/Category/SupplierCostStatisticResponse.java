package com.gys.entity.data.Category;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author li_haixia@gov-info.cn
 * @date 2021/3/4 15:29
 */
@Data
@AllArgsConstructor
public class SupplierCostStatisticResponse {
    @ApiModelProperty(value = "供应商编码")
    /**供应商编码*/
    private String supplierCode;
    @ApiModelProperty(value = "供应商名称")
    /**供应商名称*/
    private String supplierName;
    @ApiModelProperty(value = "进货数量")
    /**进货数量*/
    private Integer count;
    @ApiModelProperty(value = "进货成本额")
    /**进货成本额*/
    private String cost;
    @ApiModelProperty(value = "进货品种数")
    private Integer proCount;
}
