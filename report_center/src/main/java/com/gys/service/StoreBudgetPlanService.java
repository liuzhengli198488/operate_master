package com.gys.service;

import com.gys.common.data.PageInfo;
import com.gys.common.response.Result;
import com.gys.entity.data.StoreBudgetPlanInData;

import javax.servlet.http.HttpServletResponse;

public interface StoreBudgetPlanService {

    PageInfo findStoreBudgePlanByDate(StoreBudgetPlanInData budgetPlanInData);

    void exportStoreBudgePlanByDateDetails(StoreBudgetPlanInData inData, HttpServletResponse response);

    PageInfo findStoreBudgePlanByStoCodeAndDate(StoreBudgetPlanInData budgetPlanInData);

    void exportStoreBudgePlanByStoCodeAndDateDetails(StoreBudgetPlanInData inData, HttpServletResponse response);

    PageInfo selectStoreDiscountSummary(StoreBudgetPlanInData budgetPlanInData);

    Result exportStoreDiscountSummary(StoreBudgetPlanInData inData);
}
