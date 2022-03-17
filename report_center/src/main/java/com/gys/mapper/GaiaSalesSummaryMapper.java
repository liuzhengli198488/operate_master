package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.entity.data.salesSummary.*;
import com.gys.entity.renhe.RenHePersonSales;
import org.apache.ibatis.annotations.Param;
import com.gys.entity.renhe.StoreSaleSummaryInData;

import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Author huxinxin
 * @Date 2021/4/29 13:48
 * @Version 1.0.0
 **/
public interface GaiaSalesSummaryMapper extends BaseMapper<GaiaSalesSummary> {
    /**
     * 根据条件查询结果
     *
     * @param summaryData
     * @return
     */
    List<GaiaSalesSummary> selectSalesByProduct(SalesSummaryData summaryData);

    GaiaSalesSummaryTotal selectSalesByProductTotal(SalesSummaryData summaryData);

    List<PersonSalesOutData> selectPersonSales(PersonSalesInData inData);

    PersonSalesOutDataTotal selectPersonSalesTotal(PersonSalesInData inData);

    List<PersonSalesDetailOutData> selectPersonSalesDetail(PersonSalesDetaiInlData inData);

    PersonSalesDetailOutDataTotal selectPersonSalesDetailTotal(PersonSalesDetaiInlData inData);

    List<Map<String,Object>>findSalesSummaryByBrId(SalesSummaryDataReport summaryData);

    List<Map<String, Object>> findSalesSummaryByDate(StoreSaleDateInData summaryData);

    /**
     * 山西仁和专用
     */
    List<Map<String, Object>> selectStoreSalesSummaryByDate(StoreSaleDateInData summaryData);

    Map<String, Object> findSalesSummaryByTotal(StoreSaleDateInData inData);

    List<String> selectAuthStoreList(@Param("client")String client, @Param("userId")String userId);

    List<Map<String, Object>> selectProductUnit(@Param("brIdList")List<String> brIdList, @Param("startDate")String startDate,@Param("endDate") String endDate,@Param("client")String client);

    /**
     * 获取弹出率和弹出次数
     * @return
     */
    List<Map<String, Object>> selectPopUpData(@Param("brIdList") List<String> brIdList,@Param("startDate")String startDate, @Param("endDate")String endDate,@Param("client") String client);

    /**
     * 获取关联成交次数 成交率 关联销售额 关联销售占比
     * @param brIds
     * @param startDate
     * @param endDate
     * @param client
     * @return
     */
    List<Map<String, Object>> selectPopUpBrData(@Param("brIdList")List<String> brIds,@Param("queryStartDate") String startDate, @Param("queryEndDate")String endDate, @Param("client")String client);

    /**
     * 获取成交率
     * @param brIdList
     * @param startDate
     * @param endDate
     * @param client
     * @return
     */
    List<Map<String, Object>> selectClosingData(@Param("brIdList")List<String> brIdList, @Param("queryStartDate")String startDate, @Param("queryEndDate")String endDate, @Param("client")String client);

    /**
     * 门店销售报表汇总
     * 山西仁和
     *
     * @param summaryInData
     * @return
     */
    List<Map<String, Object>> selectStoreSaleSummaryByBrId(StoreSaleSummaryInData summaryInData);

    List<RenHePersonSales> selectRenHePersonSales(PersonSalesInData inData);

    PersonSalesOutDataTotal selectRenHePersonSalesTotal(PersonSalesInData inData);
}
