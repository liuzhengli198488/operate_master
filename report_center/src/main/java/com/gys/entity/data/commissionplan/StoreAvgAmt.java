package com.gys.entity.data.commissionplan;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author wu mao yin
 * @Title: 门店日均销售额
 * @date 2021/11/2910:10
 */
@Data
public class StoreAvgAmt implements Serializable {

    @ApiModelProperty("门店编号")
    private String stoCode;

    @ApiModelProperty("销售额")
    private BigDecimal amt;

    @ApiModelProperty("销售天数")
    private Integer klDate;

}
