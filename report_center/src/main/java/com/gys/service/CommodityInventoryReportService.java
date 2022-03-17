package com.gys.service;

import com.gys.common.response.Result;
import com.gys.entity.data.commodityInventoryReport.CommoditySummaryInData;
import com.gys.entity.data.commodityInventoryReport.CommoditySummaryOutData;

import java.util.List;

/**
 * @desc:
 * @author: RULAISZ
 * @createTime: 2021/11/26 15:41
 */
public interface CommodityInventoryReportService {
    List<CommoditySummaryOutData> summaryList(CommoditySummaryInData inData);

    Result summaryExport(CommoditySummaryInData inData);
}
