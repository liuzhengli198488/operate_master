package com.gys.service;

import com.gys.common.data.*;

import java.util.List;
import java.util.Map;

public interface AppReportService {
    List<String> selectYearlist();

    List<Map<String,String>> selectWeekOrMonthListByYear(Map<String,String> inData);

    List<ReportOutData>categoryClassList(ReportInData inData);

    List<CompClassOutData> compClassList(ReportInData inData);

    PageInfo saleBreedList(ReportInData inData);

    List<OverStockOutData>overStockList(ReportInData inData);

    RelevancyOutData selectRelevancyTotal(ReportInData inData);

    CompClassRelevancyTotal selectCompClassRelevancyList(ReportInData inData);

    List<ReportOutData> ingredientClassList(ReportInData inData);
}
