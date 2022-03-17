package com.gys.entity.data.MonthPushMoney;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: TODO
 * @author: flynn
 * @date: 2021年12月12日 上午1:07
 */
@Data
public class SaleDateNumRes implements Serializable {

    private String client;

    private String stoCode;

    private String saleId;

    private String days;

}

