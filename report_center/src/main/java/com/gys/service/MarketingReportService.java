package com.gys.service;

import com.gys.common.data.JsonResult;
import com.gys.entity.data.marketing.MarketingInDate;
import com.gys.entity.data.marketing.StartAndEndDayInData;

public interface MarketingReportService {

    JsonResult getMonthReport(StartAndEndDayInData inData);

    Object getMarketingInfoByPlanCode(MarketingInDate inData);

    Object listMarketingStores(MarketingInDate inData);

    Object listVIPInfoByPlanCode(MarketingInDate inData);

    Object listMidTOP(MarketingInDate inData);

    Object listSalesTop(MarketingInDate inData);

    Object listSalesQtyTop(MarketingInDate inData);

    Object listGoodsInfoByPlanCode(MarketingInDate inData);
}
