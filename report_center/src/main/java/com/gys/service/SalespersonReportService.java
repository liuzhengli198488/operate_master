package com.gys.service;

import com.gys.common.data.EmployeesData;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.PersonalSaleInData;
import com.gys.common.data.SalesRankOutData;
import com.gys.entity.data.salesSummary.PersonSalesInData;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface SalespersonReportService {

    Map<String, Object> personalSales(PersonalSaleInData inData) throws ParseException;

    Map<String, Object> personalSaleReportV2(PersonalSaleInData inData) throws ParseException;

    //boolean judgment(PersonalSaleInData userInfo);
    List<SalesRankOutData> listEmployeeSalesRank(PersonSalesInData inData);

    /**
     * 首页看板_员工
     *
     * @param request
     * @return
     */
    EmployeesData getPersonnelData(GetLoginOutData userInfo, Integer dayType) throws ParseException;

}
