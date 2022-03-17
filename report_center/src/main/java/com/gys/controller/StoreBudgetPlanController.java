package com.gys.controller;

import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.response.Result;
import com.gys.entity.data.StoreBudgetPlanInData;
import com.gys.service.StoreBudgetPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/storeBudgetPlan")
public class StoreBudgetPlanController extends BaseController {
    @Autowired
    private StoreBudgetPlanService storeBudgetPlanService;

    @PostMapping("selectStoreBudgePlanByDate")
    public JsonResult selectStoreBudgePlanByDate(HttpServletRequest request, @Valid @RequestBody StoreBudgetPlanInData budgetPlanInData) {
        GetLoginOutData userInfo = getLoginUser(request);
        budgetPlanInData.setClient(userInfo.getClient());
        return JsonResult.success(storeBudgetPlanService.findStoreBudgePlanByDate(budgetPlanInData), "返回成功");
    }

    @PostMapping("exportStoreBudgePlanByDateDetails")
    public void exportStoreBudgePlanByDateDetails(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody StoreBudgetPlanInData inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClient(userInfo.getClient());
        storeBudgetPlanService.exportStoreBudgePlanByDateDetails(inData, response);
    }

    @PostMapping("selectStoreBudgePlanByStoCodeAndDate")
    public JsonResult selectStoreBudgePlanByStoCodeAndDate(HttpServletRequest request, @Valid @RequestBody StoreBudgetPlanInData budgetPlanInData) {
        GetLoginOutData userInfo = getLoginUser(request);
        budgetPlanInData.setClient(userInfo.getClient());
        return JsonResult.success(storeBudgetPlanService.findStoreBudgePlanByStoCodeAndDate(budgetPlanInData), "返回成功");
    }

    @PostMapping("exportStoreBudgePlanByStoCodeAndDateDetails")
    public void exportStoreBudgePlanByStoCodeAndDateDetails(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody StoreBudgetPlanInData inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClient(userInfo.getClient());
        storeBudgetPlanService.exportStoreBudgePlanByStoCodeAndDateDetails(inData, response);
    }

    @PostMapping("selectStoreDiscountSummary")
    public JsonResult selectStoreDiscountSummary(HttpServletRequest request, @Valid @RequestBody StoreBudgetPlanInData budgetPlanInData) {
        GetLoginOutData userInfo = getLoginUser(request);
        budgetPlanInData.setClient(userInfo.getClient());
        return JsonResult.success(storeBudgetPlanService.selectStoreDiscountSummary(budgetPlanInData), "返回成功");
    }

    @PostMapping("exportStoreDiscountSummary")
    public Result exportStoreDiscountSummary(HttpServletRequest request, @Valid @RequestBody StoreBudgetPlanInData inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClient(userInfo.getClient());
        return storeBudgetPlanService.exportStoreDiscountSummary(inData);
    }


}
