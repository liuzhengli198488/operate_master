package com.gys.controller.app.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @desc:
 * @author: Ryan
 * @createTime: 2021/8/29 18:59
 */
@Data
public class BaseRateChart {
    /**日期：6月5日*/
    private String date;
    /**下货率：品项满足率*/
    private BigDecimal productRate;
    /**行业平均值*/
    private BigDecimal averageRate;

    public BaseRateChart() {
        this.productRate = BigDecimal.ZERO;
        this.averageRate = BigDecimal.ZERO;
    }
}
