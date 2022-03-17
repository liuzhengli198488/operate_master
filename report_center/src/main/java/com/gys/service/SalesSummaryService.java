package com.gys.service;

import com.gys.common.data.PageInfo;
import com.gys.common.response.Result;
import com.gys.entity.data.salesSummary.*;
import com.gys.entity.renhe.StoreSaleSummaryInData;

import java.util.List;
import java.util.Map;

/**
 * 销售管理汇总查询
 *
 * @author xiaoyuan on 2020/7/24
 */
public interface SalesSummaryService {

    /**
     * 根据条件查询结果
     * @param summaryData
     * @return
     */
    PageInfo selectSalesByProduct(SalesSummaryData summaryData);

    PageInfo selectPersonSales(PersonSalesInData inData);

    List<PersonSalesOutData> selectPersonSalesByCSV(PersonSalesInData inData);

    PageInfo selectPersonSalesDetail(PersonSalesDetaiInlData inData);

    List<PersonSalesDetailOutData> selectPersonSalesDetailByCSV(PersonSalesDetaiInlData inData);

    Map<String,Object> findSalesSummaryByBrId(SalesSummaryDataReport summaryData);

    PageInfo findSalesSummaryByDate(StoreSaleDateInData summaryData);

    /**
     * WEB端报表(用户)
     *
     * @param data 请求参数
     * @return 返回类型List<Map < String, Object>>
     */
    PageInfo selectWebSalesSummaryByDate(WebStoreSaleDateInData data);

    /**
     * WEB端报表导出
     * @param data
     * @return
     */
    Result exportSalesSummary(WebStoreSaleDateInData data);
}
