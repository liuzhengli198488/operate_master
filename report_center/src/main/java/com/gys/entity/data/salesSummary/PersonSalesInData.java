package com.gys.entity.data.salesSummary;

import lombok.Data;

import java.util.List;

/**
 * @Description 人员销售汇总查询 入参
 * @Author huxinxin
 * @Date 2021/5/20 15:38
 * @Version 1.0.0
 **/
@Data
public class PersonSalesInData {
    private String client;
    private String startDate;
    private String endDate;
    private int count;
    private String userCode;

    /*
     * 1 收银员 2 营业员 3 医生
     **/

    private String type;
    /*
     * 当然只有退货是否计算为销售天数 Y.是  N 否
     **/
    private String notSales;
    /*
     * 分门店汇总 Y.是  N 否
     **/
    private String notSto;
    /**
     *  asc 正序  desc 倒叙
     */
    private String order;
    /**
     * 关键字查询
     */
    private String keyWords;
    /**
     * 所选门店code
     */
    private List<String> stoCodeList;

    //人员代码集合
    List<String> userCodes;

    //开始时间
    private String statDatePart;

    //结束时间
    private String endDatePart;
}
