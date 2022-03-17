package com.gys.entity.data.salesSummary;

import lombok.Data;

/**
 * @Description 人员销售明细查询 入参
 * @Author huxinxin
 * @Date 2021/5/20 15:38
 * @Version 1.0.0
 **/
@Data
public class PersonSalesDetaiInlData {
    private String client;
    private String startDate;
    private String endDate;
    private String userCode;
//    private String[] stoCode;
    private String[] stoArr;

    /*
     * 1 收银员 2 营业员 3 医生
     **/

    private String type;
    /*
     * 当然只有退货是否计算为销售天数 Y.是  N 否
     **/
//    private String notSales;
    /*
     * 分门店汇总 Y.是  N 否
     **/
    private String notSto;
}
