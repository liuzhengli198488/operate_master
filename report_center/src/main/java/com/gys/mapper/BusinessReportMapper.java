package com.gys.mapper;

import com.gys.entity.data.ProductBasicInfo;
import com.gys.entity.data.businessReport.*;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface BusinessReportMapper {


    BusinessReportResponse getSalesQtyAndSalesDay(BusinessReportRequest inData);

    OverallSales getAmtAndGrossAndGrossRate(@Param("client") String client, @Param("stoCode") String stoCode, @Param("date") String date);

    VIPInfo getVIPInfo(@Param("client") String client, @Param("stoCode") String stoCode, @Param("date") String date);

    MedicalInsuranceSales getMedicalSalesInfo(@Param("client") String client, @Param("stoCode") String stoCode, @Param("date") String date);

    List<MonthData> listMonthSalesInfo(@Param("date") String date, @Param("client") String client, @Param("stoCode") String stoCode);

    List<MonthData> listVipSalesInfo(@Param("date") String date, @Param("client") String client, @Param("stoCode") String stoCode);

    List<MonthData> listMedicalSalesInfo(@Param("date") String date, @Param("client") String client, @Param("stoCode") String stoCode);

    List<MonthData> listSalesDay(@Param("date") String date, @Param("client") String client, @Param("stoCode") String stoCode);

    HashMap<String, Integer> getMaxWeek(WeeklySalesReportInfo thisReportDatum);

    HashMap<String, String> getStartTimeAndEndTime(@Param("yearList") List<Integer> yearList,@Param("weekList") List<Integer> weekList, @Param("monthList") List<Integer> monthList);

    List<HashMap<String, String>> listBigCode(BusinessReportRequest inData);

    List<HashMap<String, String>> listClientOrStores(BusinessReportRequest inData);

    List<GoodsClassResponseDTO> getClassList();

    List<HashMap<String, String>> listStoreType();

    List<ProductBasicInfo>selectProductBasicList(List<String> list);

    List<HashMap<String, String>> listWeek(BusinessReportRequest inData);

    Object getNowWeekNum();

    /**
     * 获取公司/门店 计划营业额、计划毛利额、计划毛利率
     * @param client 公司
     * @param stoCode 门店
     * @param date 日期
     * @return
     */
    OverallSalesPlanData getPlanData(@Param("client") String client,@Param("stoCode") String stoCode,@Param("date") String date);
}
