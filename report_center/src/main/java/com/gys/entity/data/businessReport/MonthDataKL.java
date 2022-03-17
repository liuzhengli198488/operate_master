package com.gys.entity.data.businessReport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员销售
 * @author XiaoZY
 * @date 2021/4/1
 */
@Data
public class MonthDataKL {
    @ApiModelProperty(value = "本月会员销售额")
    private Double salesAmt;
    @ApiModelProperty(value = "日期")
    private String GSSDDATE;
    @ApiModelProperty(value = "交易次数")
    private Double transactionQty;


}
