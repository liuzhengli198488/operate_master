package com.gys.controller;


import cn.hutool.core.util.ObjectUtil;
import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.data.PageInfo;
import com.gys.common.data.SelectAssistantDTO;
import com.gys.common.log.Log;
import com.gys.common.response.Result;
import com.gys.entity.Production;
import com.gys.entity.ProductionVo;
import com.gys.entity.data.MonthPushMoney.EmpSaleDetailInData;
import com.gys.entity.data.MonthPushMoney.MonthPushMoneyBySalespersonInData;
import com.gys.entity.data.MonthPushMoney.MonthPushMoneyByStoreInData;
import com.gys.entity.data.MonthPushMoney.V2.*;
import com.gys.entity.data.commissionplan.StoreCommissionSummaryDO;
import com.gys.service.TichengPlanZService;
import com.gys.util.UtilMessage;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping({"/TichengPlanZ/"})
public class TichengPlanController extends BaseController {

    @Autowired
    private TichengPlanZService tichengPlanZService;

    @Log("营业员提成报表")
    @PostMapping("selectMonthPushMoneyBySalesperson")
    public JsonResult selectProductInfo(HttpServletRequest request, @RequestBody MonthPushMoneyBySalespersonInData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        return JsonResult.success(tichengPlanZService.selectMonthPushMoneyBySalesperson(inData), UtilMessage.OK);
    }

    @Log("门店月提成报表")
    @PostMapping({"selectMonthPushMoneyByStore"})
    public JsonResult selectMonthPushMoneyByStore(HttpServletRequest request, @RequestBody MonthPushMoneyByStoreInData data) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        return JsonResult.success(tichengPlanZService.selectMonthPushMoneyByStore(data), "提示：获取数据成功！");
    }

    @ApiOperation(value = "拆零销售明细", response = ProductionVo.class)
    @PostMapping({"dismantledSale"})
    public JsonResult dismantledSale(HttpServletRequest request, @RequestBody Production production) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        production.setClientId(userInfo.getClient());
        production.setGsahBrId(userInfo.getDepId());
        return JsonResult.success(this.tichengPlanZService.dismantledSale(production), "提示：获取成功！");
    }


    @Log("门店提成报表")
    @ApiOperation(value = "门店销售提成新", response = PushMoneyByStoreV2OutData.class)
    @PostMapping({"selectMonthPushByStoreV2"})
    public JsonResult selectMonthPushByStoreV2(HttpServletRequest request, @RequestBody(required = false) PushMoneyByStoreV2InData inData) {
        if (ObjectUtil.isEmpty(inData)) {
            inData = new PushMoneyByStoreV2InData();
        }
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        PageInfo pageInfo = tichengPlanZService.selectMonthPushListV2Page(inData);

        return JsonResult.success(pageInfo, "提示：获取数据成功！");
    }

    @Log("营业员提成报表新")
    @ApiOperation(value = "营业员提成报表新", response = PushMoneyByStoreSalepersonV2OutData.class)
    @PostMapping({"selectMonthPushByStoreANDSalespersonV2"})
    public JsonResult selectMonthPushByStoreANDSalespersonV2(HttpServletRequest request, @RequestBody PushMoneyBySalespersonV2InData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        PageInfo pageInfo = tichengPlanZService.selectMonthPushSalespersonPageV2(inData);

        return JsonResult.success(pageInfo, "提示：获取数据成功！");
    }

    @Log("提成列表新")
    @ApiOperation(value = "提成列表新", response = PushMoneyListV2OutData.class)
    @PostMapping({"selectMonthPushListV2"})
    public JsonResult selectMonthPushListV2(HttpServletRequest request, @RequestBody PushMoneyByStoreV2InData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        return JsonResult.success(tichengPlanZService.selectMonthPushListV2(inData), "提示：获取数据成功！");
    }

    @Log("查询是否后台人员")
    @ApiOperation(value = "查询是否后台人员")
    @PostMapping({"getAdminFlag"})
    public JsonResult getAdminFlag(HttpServletRequest request) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        return tichengPlanZService.getAdminFlag(userInfo);
    }


//    @Log("营业员提成报表")
//    @ApiOperation(value = "营业员提成报表新",response = PushMoneyByStoreV2OutData.class)
//    @PostMapping({"selectMonthPushBySalespersonV2"})
//    public JsonResult selectMonthPushBySalespersonV2(HttpServletRequest request,@RequestBody PushMoneyBySalespersonV2InData inData) {
////        GetLoginOutData userInfo = this.getLoginUser(request);
////        inData.setClient(userInfo.getClient());
//        return JsonResult.success(tichengPlanZService.selectMonthPushBySalespersonV2(inData), "提示：获取数据成功！");
//    }

    @Log("门店提成报v3")
    @ApiOperation(value = "门店销售提成v3", response = PushMoneyByStoreV2OutData.class)
    @PostMapping({"selectMonthPushByStoreV3"})
    public JsonResult selectMonthPushByStoreV3(HttpServletRequest request, @RequestBody(required = false) PushMoneyByStoreV2InData inData) {
        if (ObjectUtil.isEmpty(inData)) {
            inData = new PushMoneyByStoreV2InData();
        }
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        PageInfo pageInfo = tichengPlanZService.selectMonthPushListV3Page(inData);

        return JsonResult.success(pageInfo, "提示：获取数据成功！");
    }

    @Log("营业员提成报表v3")
    @ApiOperation(value = "营业员提成报表v3", response = PushMoneyByStoreSalepersonV2OutData.class)
    @PostMapping({"selectMonthPushByStoreANDSalespersonV3"})
    public JsonResult selectMonthPushByStoreANDSalespersonV3(HttpServletRequest request, @RequestBody PushMoneyBySalespersonV2InData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        PageInfo pageInfo = tichengPlanZService.selectMonthPushSalespersonPageV3(inData);

        return JsonResult.success(pageInfo, "提示：获取数据成功！");
    }

    @Log("提成列表v3")
    @ApiOperation(value = "提成列表v3", response = PushMoneyListV2OutData.class)
    @PostMapping({"selectMonthPushListV3"})
    public JsonResult selectMonthPushListV3(HttpServletRequest request, @RequestBody PushMoneyByStoreV2InData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        return JsonResult.success(tichengPlanZService.selectMonthPushListV3(inData), "提示：获取数据成功！");
    }

    // =========================================  v5  =========================================

    @Log("方案提成汇总列表，查看门店v5")
    @ApiOperation(value = "方案提成汇总列表，查看门店v5", response = PushMoneyListV2OutData.class)
    @GetMapping({"selectStoreListWithPlanCommission"})
    public JsonResult selectStoreListWithPlanCommission(HttpServletRequest request, Integer planId) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        return JsonResult.success(tichengPlanZService.selectStoreListWithPlanCommission(userInfo.getClient(), planId), "提示：获取数据成功！");
    }

    @Log("方案提成汇总提成列表v5")
    @ApiOperation(value = "方案提成汇总提成列表v5", response = PushMoneyListV2OutData.class)
    @PostMapping({"selectMonthPushListV5"})
    public JsonResult selectMonthPushListV5(HttpServletRequest request, @RequestBody PushMoneyByStoreV2InData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        return JsonResult.success(tichengPlanZService.selectMonthPushListV5Optimize(inData), "提示：获取数据成功！");
    }

    @Log("方案提成汇总提成列表v5导出")
    @ApiOperation(value = "方案提成汇总提成列表v5导出")
    @PostMapping({"exportMonthPushListV5"})
    public Result exportMonthPushListV5(HttpServletRequest request, @RequestBody PushMoneyByStoreV2InData inData, HttpServletResponse response) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        return tichengPlanZService.exportMonthPushListV5(inData, response);
    }

    @Log("当前门店提成报表v3")
    @ApiOperation(value = "当前门店提成报表v3", response = PushMoneyByStoreV2OutData.class)
    @PostMapping({"selectPercentageStoreV3"})
    public JsonResult selectPercentageStoreV3(HttpServletRequest request, @RequestBody(required = false) PushMoneyByStoreV2InData inData) {
        if (ObjectUtil.isEmpty(inData)) {
            inData = new PushMoneyByStoreV2InData();
        }
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        inData.setStoCode(userInfo.getDepId());
        PageInfo pageInfo = tichengPlanZService.selectPercentageStoreV3(inData);
        return JsonResult.success(pageInfo, "提示：获取数据成功！");
    }

    @Log("当前门店/员工提成报表v5")
    @ApiOperation(value = "当前门店/员工提成报表v5")
    @PostMapping({"selectPercentageStoreV5"})
    public JsonResult selectPercentageStoreV5(HttpServletRequest request, @RequestBody StoreCommissionSummaryDO storeCommissionSummaryDO) {
        if (ObjectUtil.isEmpty(storeCommissionSummaryDO)) {
            storeCommissionSummaryDO = new StoreCommissionSummaryDO();
        }
        GetLoginOutData loginUser = this.getLoginUser(request);
        storeCommissionSummaryDO.setClient(loginUser.getClient());
        PageInfo pageInfo = tichengPlanZService.selectPercentageStoreV5Optimize(storeCommissionSummaryDO);
        return JsonResult.success(pageInfo, "提示：获取数据成功！");
    }

    @Log("导出当前门店/员工提成报表v5")
    @ApiOperation(value = "导出当前门店/员工提成报表v5")
    @PostMapping({"exportPercentageStoreV5"})
    public Result exportPercentageStoreV5(HttpServletRequest request, @RequestBody StoreCommissionSummaryDO storeCommissionSummaryDO, HttpServletResponse response) {
        if (ObjectUtil.isEmpty(storeCommissionSummaryDO)) {
            storeCommissionSummaryDO = new StoreCommissionSummaryDO();
        }
        GetLoginOutData loginUser = this.getLoginUser(request);
        storeCommissionSummaryDO.setClient(loginUser.getClient());
        return tichengPlanZService.exportPercentageStoreV5(storeCommissionSummaryDO, response);
    }

    @Log("当前门店营业员提成报表v3")
    @ApiOperation(value = "当前门店营业员提成报表v3", response = PushMoneyByStoreSalepersonV2OutData.class)
    @PostMapping({"selectPercentageStoreSalerV3"})
    public JsonResult selectPercentageStoreSalerV3(HttpServletRequest request, @RequestBody PushMoneyBySalespersonV2InData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        inData.setStoCode(userInfo.getDepId());
        PageInfo pageInfo = tichengPlanZService.selectPercentageStoreSalerV3(inData);
        return JsonResult.success(pageInfo, "提示：获取数据成功！");
    }


    @Log("提成方案统计查询——员工提成明细初始化")
    @ApiOperation(value = "提成方案统计查询——员工提成明细初始化")
    @GetMapping({"selectEmpSaleDetailListInit"})
    public JsonResult selectEmpSaleDetailListInit(HttpServletRequest request) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        return JsonResult.success(tichengPlanZService.selectEmpSaleDetailListInit(userInfo), "提示：获取数据成功！");
    }

    @Log("提成方案统计查询——员工提成明细v5")
    @ApiOperation(value = "提成方案统计查询——员工提成明细")
    @PostMapping({"selectEmpSaleDetailList"})
    public JsonResult selectEmpSaleDetailList(HttpServletRequest request, @RequestBody EmpSaleDetailInData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        return JsonResult.success(tichengPlanZService.selectEmpSaleDetailListOptimize(inData, userInfo), "提示：获取数据成功！");
    }

    @Log("导出提成方案统计查询——员工提成明细v5")
    @ApiOperation(value = "导出提成方案统计查询——员工提成明细v5")
    @PostMapping({"exportEmpSaleDetailListV5"})
    public Result exportEmpSaleDetailListV5(HttpServletRequest request, @RequestBody EmpSaleDetailInData inData, HttpServletResponse response) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        return tichengPlanZService.exportEmpSaleDetailListV5(inData, userInfo, response);
    }

    @ApiOperation(value = "多条件查询营业员列表")
    @PostMapping({"selectAssistantByCondition"})
    public JsonResult selectAssistantByCondition(HttpServletRequest request, @RequestBody SelectAssistantDTO selectAssistantDTO) {
        if (selectAssistantDTO.isCurUserClient()) {
            GetLoginOutData userInfo = super.getLoginUser(request);
            selectAssistantDTO.setClient(userInfo.getClient());
        }
        return JsonResult.success(tichengPlanZService.selectAssistantByCondition(selectAssistantDTO), "提示：获取数据成功！");
    }

}
