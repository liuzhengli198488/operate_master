package com.gys.entity.data.businessReport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author XiaoZY
 * @date 2021/4/1
 */
@Data
public class BusinessReportResponse {
    @ApiModelProperty(value  = "门店数")
    /**门店数*/
    private BigDecimal stoQty;
    @ApiModelProperty(value  = "销售天数")
    /**销售天数*/
    private int salesDay;
    //整体销售
    private OverallSales overallSales;
    //单店日均
    private SingleStoreInfo singleInfo;
    //会员销售
    private VIPInfo vipInfo;
    //医保销售
    private MedicalInsuranceSales medicalInfo;
    //今年每月数据
    List<MonthData> thisYearList;
    //去年每月数据
    List<MonthData> lastYearList;


}
