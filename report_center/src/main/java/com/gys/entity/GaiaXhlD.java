package com.gys.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @desc:
 * @author: Ryan
 * @createTime: 2021/8/30 23:28
 */
@Data
public class GaiaXhlD {
    private BigDecimal id;

    private String client;

    private String storeId;

    private Date tjDate;
    //订单原数量
    private BigDecimal orderNum;

    //订单原数量(排除铺货)
    private BigDecimal orderNumLess;

    //开单配送数量
    private BigDecimal dnNum;

    //开单配送数量(排除铺货)
    private BigDecimal dnNumLess;

    //开单配送数量（已过账）
    private BigDecimal sendNum;

    //开单配送数量（已过账）(排除铺货)
    private BigDecimal sendNumLess;

    //过账数量
    private BigDecimal gzNum;

    //过账数量(排除铺货)
    private BigDecimal gzNumLess;

    //订单原数量（已过账）
    private BigDecimal finalNum;

    //订单原数量（已过账）(排除铺货)
    private BigDecimal finalNumLess;

    //订单品项数
    private BigDecimal orderProductNum;

    //订单品项数(排除铺货)
    private BigDecimal orderProductNumLess;

    //开单配送品项数
    private BigDecimal dnProductNum;

    //开单配送品项数(排除铺货)
    private BigDecimal dnProductNumLess;

    //开单配送品项数（已过账）
    private BigDecimal sendProductNum;

    //开单配送品项数（已过账）(排除铺货)
    private BigDecimal sendProductNumLess;

    //过账品项数
    private BigDecimal gzProductNum;

    //过账品项数(排除铺货)
    private BigDecimal gzProductNumLess;

    //订单品项数（已过账）
    private BigDecimal finalProductNum;

    //订单品项数（已过账）(排除铺货)
    private BigDecimal finalProductNumLess;

     //订单金额：出库价*订单数量
    private BigDecimal orderAmount;

    //订单金额：出库价*订单数量(排除铺货)
    private BigDecimal orderAmountLess;

     //开单配送金额
    private BigDecimal dnAmount;

    //开单配送金额(排除铺货)
    private BigDecimal dnAmountLess;

     //开单配送金额（已过账）
    private BigDecimal sendAmount;

    //开单配送金额（已过账）(排除铺货)
    private BigDecimal sendAmountLess;
    //过账金额
    private BigDecimal gzAmount;

    //过账金额(排除铺货)
    private BigDecimal gzAmountLess;

    //FINAL_ORDER_AMOUNT
    private BigDecimal finalOrderAmount;

    //(排除铺货)
    private BigDecimal finalOrderAmountLess;
    //配送单号
    private String wmPsdh;

    //配送单号(排除铺货)
    private String wmPsdhLess;

    private Integer deleteFlag;

    private Date createTime;

    private Date updateTime;

    private Integer version;
}
