package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.entity.GaiaTichengSaleplanZ;
import com.gys.entity.data.MonthPushMoney.EmpSaleDetailInData;
import com.gys.entity.data.MonthPushMoney.EmpSaleDetailInData;
import com.gys.entity.data.MonthPushMoney.SaleDateNumQueryCondition;
import com.gys.entity.data.MonthPushMoney.SaleDateNumRes;
import com.gys.entity.data.MonthPushMoney.V2.PushMoneyBySalespersonV2InData;
import com.gys.entity.data.MonthPushMoney.V2.PushMoneyByStoreV2InData;
import com.gys.entity.data.MonthPushMoney.V2.PushMoneyListV2OutData;
import com.gys.entity.data.MonthPushMoney.V2.TichengZInfoBO;
import com.gys.entity.data.commissionplan.StoreAvgAmt;
import com.gys.entity.data.commissionplan.StoreCommissionSummary;
import com.gys.entity.data.commissionplan.StoreCommissionSummaryDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface GaiaTichengSaleplanZMapper extends BaseMapper<GaiaTichengSaleplanZ> {
    List<TichengZInfoBO> selectAllBySale(Map map);

    /**
     * 销售提成汇总
     *
     * @param storeCommissionSummaryDO
     * @return
     */
    List<StoreCommissionSummary> selectSaleCommissionSummary(StoreCommissionSummaryDO storeCommissionSummaryDO);

    /**
     * 销售提成汇总优化
     *
     * @param storeCommissionSummaryDO
     * @return
     */
    List<StoreCommissionSummary> selectSaleCommissionSummaryOptimize(StoreCommissionSummaryDO storeCommissionSummaryDO);

    /**
     * 获取用户名
     *
     * @return List<Map < String, String>>
     */
    List<Map<String, String>> selectUserName(@Param("client") String client);


    List<TichengZInfoBO> selectAllBySaleByCondiTion(EmpSaleDetailInData inData);

    /**
     * 查询当天门店日均销售额
     *
     * @param client   client
     * @param today    today
     * @param stoCodes stoCodes
     * @return List<StoreAvgAmt>
     */
    List<StoreAvgAmt> selectAvgAmtByStore(@Param("client") String client,
                                          @Param("today") String today,
                                          @Param("stoCodes") List<String> stoCodes);

}