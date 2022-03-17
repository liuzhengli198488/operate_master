package com.gys.entity.data.MonthPushMoney;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: TODO
 * @author: flynn
 * @date: 2021年12月14日 下午8:51
 */
@Data
public class TiChenProRes implements Serializable {

    private String brId;

    private String brName;

    private String billNo;

    private String proId;

    private String serial;

    private String ysAmt;

    private String amt;

    private String grossProfitAmt;

    private String grossProfitRate;

    private String zkAmt;

    private String zkl;

    private String costAmt;

    private String days;

    private String amtAvg;

    private String baseAmount;

    private String qyt;

    private String commissionAmt;

    private String realTime;

}

