package com.gys.mapper;

import com.gys.common.data.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AppReportMapper {
    /**
     * 查询年份列表
     * @return
     */
    List<String> selectYearList();

    /**
     * 根据年份查询当前时间以前的周数
     * @param year
     * @return
     */
    List<Map<String,String>> selectWeekListByYear(@Param("year") String year);

    /**
     * 根据年份查询当前时间以前的月
     * @param year
     * @return
     */
    List<Map<String,String>> selectMonthListByYear(@Param("year") String year);

    /**
     * 查询周/月的起止时间，已转换时间格式，麒麟可直接使用
     * @param year
     * @param month
     * @param week
     * @return
     */
    Map<String,String> selectDateTime(@Param("year") Integer year,@Param("month") Integer month,@Param("week") Integer week);

    List<OverStockOutData> selectOverStockList(ReportInData inData);

    RelevancyOutData selectRelevancyTotal(ReportInData inData);

    List<CompClassRelevancyOutData> selectCompClassRelevancyOutData(ReportInData inData);

    SumNowReportOutData sumNowReport(ReportInData inData);

    //SaleBreedOutData listSaleBreedOutData(@Param("proCode") List<String> proCode,@Param("client") String client,@Param("brId") String brId);

    List<ReportOutData> ingredientClassList(@Param("clientId") String clientId, @Param("stoCode") String stoCode, @Param("startDate") String startDate, @Param("endDate") String endDate,@Param("categoryClass") String categoryClass);

    List<ReportOutData> ingredientProCountClassList(@Param("clientId") String clientId, @Param("stoCode") String stoCode, @Param("startDate") String startDate, @Param("endDate") String endDate,@Param("categoryClass") String categoryClass);

    List<SaleBreedOutData> listSaleBreedOutData(@Param("proCodeList") List<String> proCodeList,@Param("client") String client,@Param("brId") String brId);
}
