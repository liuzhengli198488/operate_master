package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.report.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface GaiaMaterialDocMapper extends BaseMapper<GaiaMaterialDoc> {

    List<SupplierDealOutData> selectSupplierDealPageBySiteCode(SupplierDealInData inData);

    List<WholesaleSaleOutData> selectWholesaleSalePage(WholesaleSaleInData inData);

    List<WholesaleSaleOutData> selectWholesaleSaleDetailPage(WholesaleSaleInData inData);

    List<BusinessDocumentOutData> selectBusinessDocumentPage(BusinessDocumentInData inData);

    List<BusinessDocumentOutData> selectBusinessDocumentDetailPage(BusinessDocumentInData inData);

    SupplierDealOutTotal selectSupplierDealByTotal(SupplierDealInData inData);

    SupplierDealOutTotal selectSupplierDealDetailByTotal(SupplierDealInData inData);

    List<InventoryChangeSummaryOutData> selectInventoryChangeSummary(InventoryChangeSummaryInData inData);

    BusinessDocumentOutTotal selectBusinessDocumentTotal(BusinessDocumentInData inData);

    BusinessDocumentOutTotal selectBusinessDocumentDetailTotal(BusinessDocumentInData inData);

    InventoryChangeSummaryOutTotal selectInventoryChangeSummaryTotal(InventoryChangeSummaryInData inData);

    List<SupplierDealOutData> selectSupplierDealDetailPage(SupplierDealInData inData);

    List<InventoryChangeSummaryOutData> getStartDataBySto(InventoryChangeSummaryInData inData);

    List<InventoryChangeSummaryWithStoreOutData> getStartDataByStoWithStore(InventoryChangeSummaryInData inData);

    List<InventoryChangeSummaryOutData> getStartDataByDc(InventoryChangeSummaryInData inData);

    List<WholesaleSaleOutData> selectWholesaleSalemanList(WholesaleSaleInData inData);

    List<WholesaleSaleOutData> selectWholesaleDetailSalemanList(WholesaleSaleInData inData);

    List<InventoryChangeSummaryOutData> getStartDataAll(InventoryChangeSummaryInData inData);

    List<InventoryChangeSummaryOutData> selectDistributionAndAdjustSummaryData(InventoryChangeSummaryInData inData);

    List<Map<String, Object>> selectSupplierName(@Param("inData")SupplierDealInData inData);

    List<SupplierDealOutData> selectSupplierSummaryDealPage(SupplierDealInData data);

    List<WholesaleSaleOutData> selectdeviveryDatas(WholesaleSaleInData inData);

    List<String> getStoCode(@Param("client") String client);

    List<InventoryChangeSummaryWithStoreOutData> selectInventoryChangeSummaryWithStore(InventoryChangeSummaryInData stoInData);

    List<InventoryChangeSummaryWithStoreOutData> selectDistributionAndAdjustSummaryDataWithStore(InventoryChangeSummaryInData inData);

    InventoryChangeCheckOutData selectInventoryStockCheckAmtDcStartList(InventoryChangeCheckInData data);

    InventoryChangeCheckOutData selectInventoryStockCheckDcBetweenAmt(InventoryChangeCheckInData data);

    String findDcNameByDcCode(@Param("client")String client,@Param("dcCode") String dcCode);

    List<InventoryChangeSummaryDetailOutData> selectInventoryChangeSummaryDetail(InventoryChangeSummaryInData inData);

    List<InventoryChangeCheckOutData> selectInventoryStockCheckAmtStoStartList(InventoryChangeCheckInData data);

    List<InventoryChangeCheckOutData> selectInventoryStockCheckStoBetweenAmt(InventoryChangeCheckInData data);

    String getQcDate(String client);
}