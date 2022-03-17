package com.gys.controller.app.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @desc:
 * @author: Ryan
 * @createTime: 2021/8/29 18:55
 */
@Data
public class BaseRate {
    /**星期：周五*/
    private String week;
    /**日期：8月5日*/
    private String date;
    /**月份：08月*/
    private String month;
    /**数量满足率*/
    private BigDecimal quantityRate;
    /**金额满足率*/
    private BigDecimal amountRate;
    /**品项满足率*/
    private BigDecimal productRate;

    public BaseRate() {
        this.quantityRate = BigDecimal.ZERO;
        this.amountRate = BigDecimal.ZERO;
        this.productRate = BigDecimal.ZERO;
    }
}
