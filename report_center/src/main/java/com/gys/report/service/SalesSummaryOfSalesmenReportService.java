package com.gys.report.service;

import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.PageInfo;
import com.gys.common.response.Result;
import com.gys.report.entity.*;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author wavesen.shen
 */
public interface SalesSummaryOfSalesmenReportService {



    /***
     *
     * @param inData
     * @return
     */
    PageInfo<GetSalesSummaryOfSalesmenReportOutData> queryReport(@RequestBody GetSalesSummaryOfSalesmenReportInData inData);

    PageInfo unionQueryReport(GetSalesSummaryOfSalesmenReportInData inData);

    Result unionQueryExport(GetSalesSummaryOfSalesmenReportInData inData);

    PageInfo getSalespersonsSalesDetails(GetSalesSummaryOfSalesmenReportInData inData);

    /**
     * 商品销售明细表导出
     * @param inData
     * @param response
     * @return
     */
    void exportSalespersonsSalesDetails(GetSalesSummaryOfSalesmenReportInData inData, HttpServletResponse response);

    PageInfo getSalespersonsSalesDetailsByClient(GetSalesSummaryOfSalesmenReportInData inData);

    List<SalespersonsSalesDetailsOutData> getSalespersonsSalesByPro(GetSalesSummaryOfSalesmenReportInData inData);

    List<SalespersonsSalesDetailsOutData> getSalespersonsSalesByUser(GetSalesSummaryOfSalesmenReportInData inData);
    List<SalespersonsSalesDetailsOutData> getSalespersonsSalesByDoctor(GetSalesSummaryOfSalesmenReportInData inData);

    List<SupplierInfoDTO> getSupByClient(String client);

    List<String> getProStatusList(GetLoginOutData userInfo);

    PageInfo  queryMemberInfo(GetMemberInfoInData inData);

    List<ProductTssxOutData> getProTssx(String client);

    List<GetUserOutData> selectClientUser(String client);
}
