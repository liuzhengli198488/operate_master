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
public class SaleDateNumQueryCondition implements Serializable {

    private String client;

    private String startDate;

    private String endDate;

    private List<String> stoCodes;

    private List<String> saleIds;

    private String type;//null表示查询加盟商级别销售天数 1表示查询门店级别销售天数 2表示查询营业员级别销售天数
}

