package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.common.data.CommonVo;
import com.gys.common.data.EmpSaleDetailResVo;
import com.gys.common.data.SelectAssistantDTO;
import com.gys.entity.*;
import com.gys.entity.data.MonthPushMoney.*;
import com.gys.entity.data.commissionplan.StoreCommissionSummary;
import com.gys.entity.data.commissionplan.StoreCommissionSummaryDO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface GaiaTichengPlanZMapper extends BaseMapper<GaiaTichengPlanZ> {
    List<MonthPushMoneyBySalespersonOutData> selectMonthSalesBySales(MonthPushMoneyBySalespersonInData inData);

    List<MonthPushMoneyBySalespersonOutData> selectMonthSalesByPro(MonthPushMoneyBySalespersonInData inData);

    List<MonthPushMoneyByStoreOutData> selectMonthSalesByStoreAndSales(MonthPushMoneyByStoreInData inData);

    List<MonthPushMoneyByStoreOutData> selectMonthSalesByStoreAndPro(MonthPushMoneyByStoreInData inData);

    List<ProductionVo> dismantledSale(Production production);

    int getAdminFlag(@Param("client") String client, @Param("userId") String userId);

    GaiaTichengSaleplanZ selectPlanByClientAndDayAndPlanType(@Param("client") String client, @Param("stoCode") String stoCode, @Param("day") String dayStr, @Param("planType") String planType);

    List<GaiaTichengProplanZ> selectTimeInterleavingProPlan(@Param("client") String client, @Param("stoCode") String stoCode, @Param("startDate") String planStartDate, @Param("endDate") String planEndDate);

    List<String> getProCodeByClientAndPid(@Param("client") String client, @Param("pid") Long pid);

    List<String> getRejectProCodeByClientAndPid(@Param("client") String client, @Param("pid") Long pid);

    List<String> getRejectClassByPid(@Param("pid") Long pid);

    List<GaiaTichengSaleplanM> getTiChengScale(@Param("client") String client, @Param("pid") Long pid, @Param("avgAmt") BigDecimal avgAmt);

    Map<String, String> getDate(@Param("date") String dayStr, @Param("selectTime") String selectTime);

    GaiaTichengProplanZ selectProPlanByClientAndDayAndPlanType(@Param("client") String client, @Param("day") String dayStr, @Param("type") String type);

    GaiaTichengSaleplanZ selectSalePlanByClientAndDay(@Param("client") String client, @Param("day") String dayStr);

    List<GaiaTichengSaleplanZ> selectSalePlanByClientAndStartDayAndEndDay(@Param("client") String client, @Param("startDay") String startDateStr, @Param("endDay") String endDateStr);

    List<String> getDaySaleStoCode(@Param("client") String client, @Param("userId") String userId, @Param("day") String dayStr);

    List<GaiaTichengProplanZ> selectProPlanByClientAndDay(@Param("client") String client, @Param("day") String dayStr);

    GaiaTichengProplanZ selectProPlanByClientAndStoCodeAndDay(@Param("client") String client, @Param("stoCode") String stoCode, @Param("day") String dayStr);

    List<GaiaTichengProplanPro> getProListByClientAndPid(@Param("client") String client, @Param("pid") Long pid);

    List<String> getSaleClass(String client);

    List<String> getProPosition(String client);

    List<CommonVo> getSaleUserInfo(String client, String startDate, String endDate);

    List<CommonVo> getDoctorUserInfo(String client, String startDate, String endDate);

    List<CommonVo> getEmpUserInfo(String client, String startDate, String endDate);

    List<CommonVo> selectStoInfo(String client);

    List<CommonVo> selectStoreByStoreCodes(@Param(value = "stoArr") List<String> stoArr, @Param(value = "client") String client);

    List<Map<String, Object>> selectAssistantByCondition(SelectAssistantDTO selectAssistantDTO);

    List<SaleDateNumRes> selecrSaleDaysByCondition(SaleDateNumQueryCondition condition);

    List<EmpSaleDetailResVo> getToDayAllEmpSaleDetailList(EmpSaleDetailInData inData);

    /**
     * 获取实时的主方案销售汇总
     *
     * @param client client
     * @param nowDate                  nowDate
     * @param isSaleManSummary         isSaleManSummary
     * @param showStore                showStore
     * @param isSaleDateSummary        isSaleDateSummary
     * @return List<StoreCommissionSummary>
     */
    List<StoreCommissionSummary> getRealTimeSaleSummary(@Param("client") String client,
                                                        @Param("nowDate") String nowDate,
                                                        @Param("isSaleManSummary") boolean isSaleManSummary,
                                                        @Param("showStore") boolean showStore,
                                                        @Param("isSaleDateSummary") boolean isSaleDateSummary);

}