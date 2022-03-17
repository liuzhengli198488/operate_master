package com.gys.report.controller;

import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.response.Result;
import com.gys.report.entity.*;
import com.gys.report.service.MedicalSetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(tags = "医保信息")
@RestController
@RequestMapping("/medicalSet/")
public class MedicalSetController extends BaseController {

    @Resource
    private MedicalSetService medicalSetService;

    @ApiOperation(value = "医保结算查询",response = MedicalSetOutData.class)
    @PostMapping("selectMedicalSetList")
    public JsonResult selectMedicalSetList(HttpServletRequest request, @RequestBody MedicalSetInData inData) {
       GetLoginOutData userInfo = getLoginUser(request);
       inData.setClientId(userInfo.getClient());
       return JsonResult.success(medicalSetService.selectMedicalSetList(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "医保销售详情查询",response = MedicalSalesOutData.class)
    @PostMapping("selectMedicalSalesList")
    public JsonResult selectMedicalSalesList(HttpServletRequest request, @RequestBody MedicalSalesInData inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        return JsonResult.success(medicalSetService.selectMedicalSalesList(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "医保结算导出",response = MedicalSetOutData.class)
    @PostMapping("reportMedicalSet")
    public void reportMedicalSetList(HttpServletRequest request, HttpServletResponse response, @RequestBody MedicalSetInData inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        medicalSetService.exportMedicalSetList(inData,response);
    }

    @ApiOperation(value = "医保销售对账汇总(对总账)")
    @PostMapping("selectMedicalSummaryList")
    public JsonResult selectMedicalSummaryList(HttpServletRequest request,@RequestBody SelectMedicalSummaryData inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClient(userInfo.getClient());
        return JsonResult.success(medicalSetService.selectMedicalSummaryList(inData),"获取成功");
     }

    @ApiOperation(value = "医保销售对账汇总(对总账)")
    @PostMapping("selectMedicalStoreSummaryList")
    public JsonResult selectMedicalStoreSummaryList(HttpServletRequest request,@RequestBody SelectMedicalSummaryData inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClient(userInfo.getClient());
        return JsonResult.success(medicalSetService.selectMedicalStoreSummaryList(inData),"获取成功");
    }
    @ApiOperation(value = "获取险种列表")
    @PostMapping("selectInSuTypesList")
    public JsonResult selectInSuTypesList(HttpServletRequest request) {
        GetLoginOutData userInfo = getLoginUser(request);
        return JsonResult.success(medicalSetService.selectInSuTypesList(userInfo),"获取成功");
    }
}
