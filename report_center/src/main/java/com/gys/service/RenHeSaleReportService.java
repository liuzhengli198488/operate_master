package com.gys.service;

import com.gys.common.data.PageInfo;
import com.gys.entity.data.salesSummary.PersonSalesInData;
import com.gys.entity.renhe.StoreProductSaleSummaryInData;
import com.gys.entity.renhe.StoreSaleSummaryInData;
import com.gys.report.entity.StoreSaleDateInData;

import java.util.Map;

/**
 * 仁和报表优化service
 */
public interface RenHeSaleReportService {

    /**
     * 门店销售汇总报表
     */
    Map<String, Object> selectStoreSaleSummaryByBrId(StoreSaleSummaryInData summaryData);

    PageInfo selectStoreSaleByDate(StoreSaleDateInData data);

    PageInfo selectStoreSalesSummaryByDate(com.gys.entity.data.salesSummary.StoreSaleDateInData summaryData);

    PageInfo selectStoreProductSaleSummary(StoreProductSaleSummaryInData data);

    PageInfo selectPersonSales(PersonSalesInData inData);
}
