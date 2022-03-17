package com.gys.entity.data.salesSummary;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 销售管理汇总实体
 *
 * @author xiaoyuan on 2020/7/24
 */
@Data
public class GaiaSalesSummaryTotal implements Serializable {
    private static final long serialVersionUID = -3771261720088233548L;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    private BigDecimal proQty;

    /**
     * 应收金额
     */
    @ApiModelProperty(value = "应收金额")
    private BigDecimal gssdnormalAmt;

    /**
     * 实收金额
     */
    @ApiModelProperty(value = "实收金额")
    private BigDecimal gssdAmt;

    /**
     * 折扣金额
     */
    @ApiModelProperty(value = "折扣金额")
    private BigDecimal discountAmt;

    /**
     * 折扣率
     */
    @ApiModelProperty(value = "折扣率")
    private String discountRate;

    /**
     * 成本额
     */
    @ApiModelProperty(value = "成本额")
    private BigDecimal costAmt;

    /**
     * 毛利额
     */
    @ApiModelProperty(value = "毛利额")
    private BigDecimal grossProfitAmt;

    /**
     * 毛利率
     */
    @ApiModelProperty(value = "毛利率")
    private String grossProfitRate;

    /**
     * 库存数量
     */
    @ApiModelProperty(value = "库存数量")
    private BigDecimal gsisdQty;

    private String flag;

}
