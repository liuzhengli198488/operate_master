package com.gys.service;

import com.gys.common.data.*;
import com.gys.common.response.Result;
import com.gys.entity.Production;
import com.gys.entity.ProductionVo;
import com.gys.entity.data.MonthPushMoney.EmpSaleDetailInData;
import com.gys.entity.data.MonthPushMoney.MonthPushMoneyBySalespersonInData;
import com.gys.entity.data.MonthPushMoney.MonthPushMoneyByStoreInData;
import com.gys.entity.data.MonthPushMoney.V2.PushMoneyBySalespersonV2InData;
import com.gys.entity.data.MonthPushMoney.V2.PushMoneyByStoreV2InData;
import com.gys.entity.data.commissionplan.StoreCommissionSummaryDO;

import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface TichengPlanZService {
    PageInfo selectMonthPushMoneyBySalesperson(MonthPushMoneyBySalespersonInData inData);

    PageInfo selectMonthPushMoneyByStore(MonthPushMoneyByStoreInData data);

    PageInfo<ProductionVo> dismantledSale(Production production);

    PageInfo selectMonthPushListV2Page(PushMoneyByStoreV2InData inData);

    PageInfo selectMonthPushBySaleV2(PushMoneyByStoreV2InData inData);

    PageInfo selectMonthPushByProV2(PushMoneyByStoreV2InData inData);

    PageInfo selectMonthPushSalespersonPageV2(PushMoneyBySalespersonV2InData inData);

    PageInfo selectMonthPushSalespersonBySaleV2(PushMoneyBySalespersonV2InData inData);

    PageInfo selectMonthPushSalespersonByProV2(PushMoneyBySalespersonV2InData inData);

    PageInfo selectMonthPushListV2(PushMoneyByStoreV2InData inData);

    JsonResult getAdminFlag(GetLoginOutData userInfo);

    PageInfo selectMonthPushListV3Page(PushMoneyByStoreV2InData inData);

    PageInfo selectMonthPushSalespersonPageV3(PushMoneyBySalespersonV2InData inData);

    PageInfo selectMonthPushListV3(PushMoneyByStoreV2InData inData);

    @Deprecated
    PageInfo selectMonthPushListV5(PushMoneyByStoreV2InData inData);

    PageInfo selectMonthPushListV5Optimize(PushMoneyByStoreV2InData inData);

    /**
     * 导出方案提成汇总
     *
     * @param inData   inData
     * @param response response
     * @return Result
     */
    Result exportMonthPushListV5(PushMoneyByStoreV2InData inData, HttpServletResponse response);

    PageInfo selectPercentageStoreV3(PushMoneyByStoreV2InData inData);

    /**
     * 查询门店、员工提成汇总
     *
     * @param storeCommissionSummaryDO storeCommissionSummaryDO
     * @return PageInfo
     */
    PageInfo selectPercentageStoreV5(StoreCommissionSummaryDO storeCommissionSummaryDO);

    /**
     * 查询门店、员工提成汇总优化
     *
     * @param storeCommissionSummaryDO storeCommissionSummaryDO
     * @return PageInfo
     */
    PageInfo selectPercentageStoreV5Optimize(StoreCommissionSummaryDO storeCommissionSummaryDO);

    /**
     * 导出门店、员工提成汇总
     *
     * @param storeCommissionSummaryDO storeCommissionSummaryDO
     * @param response                 response
     * @return Result
     */
    Result exportPercentageStoreV5(StoreCommissionSummaryDO storeCommissionSummaryDO, HttpServletResponse response);

    PageInfo selectPercentageStoreSalerV3(PushMoneyBySalespersonV2InData inData);

    PageInfo selectEmpSaleDetailList(EmpSaleDetailInData inData, GetLoginOutData userInfo);

    PageInfo selectEmpSaleDetailListOptimize(EmpSaleDetailInData inData, GetLoginOutData userInfo);

    Map<String, Object> selectEmpSaleDetailListInit(GetLoginOutData userInfo);

    /**
     * 方案提成汇总列表，查看门店
     *
     * @param client client
     * @param planId 方案id
     * @return List<StoreSimpleInfo>
     */
    List<StoreSimpleInfoWithPlan> selectStoreListWithPlanCommission(String client, Integer planId);

    Result exportEmpSaleDetailListV5(EmpSaleDetailInData inData, GetLoginOutData userInfo, HttpServletResponse response);

    /**
     * 多条件查询营业员列表
     *
     * @param selectAssistantDTO selectAssistantDTO
     * @return  Map<String,Object>
     */
    Map<String,Object> selectAssistantByCondition(SelectAssistantDTO selectAssistantDTO);

}
