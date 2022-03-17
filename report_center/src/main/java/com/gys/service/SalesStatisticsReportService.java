package com.gys.service;

import com.gys.common.data.JsonResult;
import com.gys.common.data.PageInfo;
import com.gys.common.data.SalesStatisticsReportInData;
import com.gys.common.data.SalesStatisticsReportOutData;

import javax.servlet.http.HttpServletResponse;

public interface SalesStatisticsReportService {

    PageInfo<SalesStatisticsReportOutData> query (SalesStatisticsReportInData inData);

    JsonResult export (SalesStatisticsReportInData inData, HttpServletResponse response);

    JsonResult queryDefaultDate ();

}
