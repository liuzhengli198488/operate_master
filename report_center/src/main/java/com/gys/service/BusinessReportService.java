package com.gys.service;

import com.gys.entity.data.businessReport.BusinessReportRequest;
import com.gys.entity.data.businessReport.BusinessReportResponse;
import com.gys.entity.data.businessReport.SalesReportInfo;
import com.gys.entity.data.businessReport.WeeklySalesReportInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface BusinessReportService {


    BusinessReportResponse businessSalesReport(BusinessReportRequest inData);

    BusinessReportResponse listMonthSalesInfo(BusinessReportRequest inData);

    BusinessReportResponse listSingleSalesInfo(BusinessReportRequest inData);

    BusinessReportResponse listVipSalesInfo(BusinessReportRequest inData);

    BusinessReportResponse listMedicalSalesInfo(BusinessReportRequest inData);

    BusinessReportResponse listMonthSalesInfoByKL(BusinessReportRequest inData);

    BusinessReportResponse listSingleSalesInfoByKL(BusinessReportRequest inData);

    SalesReportInfo listCompanyWeeklySalesReport(BusinessReportRequest inData);

    List<HashMap<String,String>> listBigCode(BusinessReportRequest inData);

    List<HashMap<String,String>> listClientOrStores(BusinessReportRequest inData);

    Object listClassCode(BusinessReportRequest inData);

    List<HashMap<String,String>> listStoreType(BusinessReportRequest inData);

    Object exportWeeklySalesReport(BusinessReportRequest inData) throws IOException;

    Object listWeek(BusinessReportRequest inData);

    Object getNowWeekNum(BusinessReportRequest inData);
}
