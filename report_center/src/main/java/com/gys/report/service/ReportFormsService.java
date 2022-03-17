package com.gys.report.service;

import com.gys.common.data.JsonResult;
import com.gys.common.data.PageInfo;
import com.gys.common.response.Result;
import com.gys.report.entity.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public interface ReportFormsService {
    PageInfo<SupplierDealOutData> selectSupplierDealPage(SupplierDealInData inData);

    PageInfo<SupplierDealOutData> selectSupplierDealDetailPage(SupplierDealInData inData);

    PageInfo<WholesaleSaleOutData> selectWholesaleSalePage(WholesaleSaleInData data);

    void selectWholesaleSalePageExport(HttpServletResponse response, WholesaleSaleInData data);

    PageInfo<WholesaleSaleOutData> selectWholesaleSaleDetailPage(WholesaleSaleInData data);

    void selectWholesaleSaleDetailPageExport(HttpServletResponse response, WholesaleSaleInData data);

    PageInfo<BusinessDocumentOutData> selectBusinessDocumentPage(BusinessDocumentInData data);

    PageInfo<BusinessDocumentOutData> selectBusinessDocumentDetailPage(BusinessDocumentInData data);

    PageInfo<StoreRateSellOutData> selectStoreRateSellDetailPage(StoreRateSellInData data);

    PageInfo<StoreRateSellOutData>  selectStoreRateSellPage(StoreRateSellInData data);

    PageInfo selectStoreSaleByDay(StoreSaleDayInData inData);

    PageInfo selectStoreSaleByDayAndClient(StoreSaleDayInData inData);

    PageInfo selectInventoryChangeSummary(InventoryChangeSummaryInData data);


    PageInfo selectProductSalesBySupplier(ProductSalesBySupplierInData inData);

    PageInfo selectProductSalesBySupplierByStore(ProductSalesBySupplierInData inData);


    PageInfo selectProductSaleByClient(StoreProductSaleClientInData data);

    PageInfo selectProductSaleByStore(StoreProductSaleStoreInData data);

    //标记开票
    Boolean updateStoreSaleInvoiced(List<UpdateInvoicedInData> data);

    PageInfo selectSupplierSummaryDealPage(SupplierDealInData data);

    PageInfo selectdeviveryDatasPage(WholesaleSaleInData data);

    void deviveryDataCollectSelectExport(HttpServletResponse response, WholesaleSaleInData data);

    PageInfo selectInventoryChangeSummaryWithStore(InventoryChangeSummaryInData data);

    PageInfo selectStoreSaleByDate(StoreSaleDateInData data);

    PageInfo selectInventoryChangeSummaryDetail(InventoryChangeSummaryInData data);

    PageInfo<InventoryChangeCheckOutData> selectInventoryStockCheckListByDc(InventoryChangeCheckInData data);

    void checkListDcExport(InventoryChangeCheckInData data, HttpServletResponse response);

    PageInfo<InventoryChangeCheckOutData> selectInventoryStockCheckListBySto(InventoryChangeCheckInData data);

    void checkListStoExport(InventoryChangeCheckInData data, HttpServletResponse response);

    /**
     * 供应商商品销售明细导出
     * @param data
     * @return
     */
    Result exportProductSalesBySupplier(ProductSalesBySupplierInData data) throws IOException;
}
