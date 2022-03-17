package com.gys.mapper;

import com.gys.entity.data.StoreBudgetPlanInData;

import java.util.List;
import java.util.Map;

public interface StoreBudgetPlanMapper {

    List<Map<String, Object>> findStoreBudgePlanByDate(StoreBudgetPlanInData inData);

    Map<String, Object> findStoreBudgePlanByTotal(StoreBudgetPlanInData inData);

    List<Map<String, Object>> findStoreBudgePlanByStoCodeAndDate(StoreBudgetPlanInData inData);

    List<Map<String, Object>> selectStoreDiscountSummary(StoreBudgetPlanInData budgetPlanInData);

}
