package com.gys.mapper;
import com.gys.common.base.BaseMapper;
import com.gys.entity.data.consignment.dto.RecommendedDocumentsDto;
import com.gys.entity.data.consignment.dto.StoreDto;
import com.gys.entity.data.consignment.dto.StoreRecommendedSaleDto;
import com.gys.entity.data.consignment.vo.RecommendedDocumentsVo;
import com.gys.entity.data.consignment.vo.StoreReportTotalVo;
import com.gys.entity.data.consignment.vo.StoreReportVo;
import com.gys.entity.data.consignment.vo.StoreVo;
import com.gys.entity.renhe.StoreProductSaleSummaryInData;
import com.gys.entity.renhe.StoreProductSaleSummaryOutData;
import com.gys.report.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.ResultHandler;

import java.util.List;
import java.util.Map;

@Mapper
public interface GaiaSdSaleDMapper extends BaseMapper<GaiaSdSaleD> {
    List<GetSalesSummaryOfSalesmenReportOutData> querySalesSummaryOfSalesmenReport(GetSalesSummaryOfSalesmenReportInData inData);
    List<SalespersonsSalesDetailsOutData> getSalespersonsSalesDetails(GetSalesSummaryOfSalesmenReportInData inData);

    void getSalespersonsSalesDetails(GetSalesSummaryOfSalesmenReportInData inData, ResultHandler<SalespersonsSalesDetailsOutData> resultHandler);

    List<SalespersonsSalesDetailsOutData> getSalespersonsSalesByPro(GetSalesSummaryOfSalesmenReportInData inData);

    List<SalespersonsSalesDetailsOutData> getSalespersonsSalesByUser(GetSalesSummaryOfSalesmenReportInData inData);
    GetSalesSummaryOfSalesmenReportOutData queryCountSalesSummar(GetSalesSummaryOfSalesmenReportInData inData);
    SalespersonsSalesDetailsOutTotal getSalespersonsSalesDetailsTotal(GetSalesSummaryOfSalesmenReportInData inData);
    List<SalespersonsSalesDetailsOutData> getSalespersonsSalesByDoctor(GetSalesSummaryOfSalesmenReportInData inData);
    List<GetSalesSummaryOfSalesmenReportOutDataUnion> querySalesSummaryOfSalesmenReportUnion(GetSalesSummaryOfSalesmenReportInData inData);
    List<GetSalesSummaryOfSalesmenReportOutDataUnion> selectUnionSaleCountAndAmtByUserId(RelatedSaleEjectInData relatedSaleEjectInData);
    List<GetSalesSummaryOfSalesmenReportOutDataUnion> selectUnionSaleOrderByUserId(RelatedSaleEjectInData relatedSaleEjectInData);
    List<StoreRateSellOutData> selectStoreRateSellDetailPage(StoreRateSellInData inData);
    List<StoreRateSellOutData> selectStoreRateSellPage(StoreRateSellInData inData);

    List<Map<String,Object>> selectStoreSaleByDay(StoreSaleDayInData inData);
    Map<String, Object> selectStoreSaleByDayTotal(StoreSaleDayInData inData);
    List<SalespersonsSalesDetailsOutData> getProductSalesBySupplier(ProductSalesBySupplierInData inData);

    // 门店商品销售数据根据批次汇总
    List<SalespersonsSalesDetailsOutData> getProductSalesBySupplierWithBatch(SupplierSummaryInData inData);

    List<StoreProductSaleClientOutData> selectProductSaleByClient(StoreProductSaleClientInData inData);
    StoreProductSaleClientOutTotal selectProductSaleByClientTatol(StoreProductSaleClientInData inData);
    List<StoreProductSaleStoreOutData> selectProductSaleByProAllStore(StoreProductSaleStoreInData inData);
    StoreProductSaleStoreOutTotal selectProductSaleByProAllStoreTatol(StoreProductSaleStoreInData inData);
    List<StoreProductSaleStoreOutData> selectProductSaleByStore(StoreProductSaleStoreInData inData);
    StoreProductSaleStoreOutTotal selectProductSaleByStoreTatol(StoreProductSaleStoreInData inData);
    List<SupplierInfoDTO> getSupByClient(@Param("client")String client);

    void getRecommendedSalesDetail(StoreRecommendedSaleDto dto,ResultHandler<StoreReportVo> resultHandler);
    List<StoreReportVo> getRecommendedSalesDetail(StoreRecommendedSaleDto dto);
    List<StoreReportVo> getUnRecommendedSalesDetail(StoreRecommendedSaleDto dto);
    void getUnRecommendedSalesDetail(StoreRecommendedSaleDto dto,ResultHandler<StoreReportVo> resultHandler);

    StoreReportTotalVo getRecommendedSalesDetailTotal(StoreRecommendedSaleDto dto);

    StoreReportTotalVo getUnRecommendedSalesDetailTotal(StoreRecommendedSaleDto dto);

    List<StoreVo> getRecommendedStores(StoreDto dto);

    List<StoreVo> getSaleStores(StoreDto dto);
    List<String> getProStatusList(String client);

    List<RecommendedDocumentsVo> getRecommendedDocuments(RecommendedDocumentsDto data);

    RecommendedDocumentsVo getRecommendedDocumentsTotal(RecommendedDocumentsDto data);

    List<StoreVo> getAllStores(StoreDto dto);

    List<GetMemberInfoOutData> queryMemberInfo(GetMemberInfoInData inData);

    List<Map<String, Object>> selectStoreSaleByDate(StoreSaleDateInData inData);

    Map<String,Object> selectStoreSaleByTatol(StoreSaleDateInData inData);

    List<SalespersonsSalesDetailsOutData> getSalespersonsSalesDetailsNew(GetSalesSummaryOfSalesmenReportInData inData);

    List<Map<String, Object>> selectRenHeStoreSaleByDate(StoreSaleDateInData inData);

    Map<String, Object> selectRenHeStoreSaleByDateTotal(StoreSaleDateInData inData);

    List<StoreProductSaleSummaryOutData> selectRenHeStoreProductSaleSummary(StoreProductSaleSummaryInData inData);

    StoreProductSaleStoreOutTotal selectRenHeStoreProductSaleSummaryTotal(StoreProductSaleSummaryInData inData);

    List<GaiaUserData> selectClientUser(@Param("client")String client);
}

