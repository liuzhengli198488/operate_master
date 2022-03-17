package com.gys.controller;


import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.data.MaterialDocumentInData;
import com.gys.common.data.NoSaleOutData;
import com.gys.entity.data.businessReport.BusinessReportRequest;
import com.gys.service.BusinessReportService;
import com.gys.service.MaterialDocumentService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/businessReport")
public class BusinessReportController extends BaseController {
    @Autowired
    private BusinessReportService businessReportService;

    @ApiOperation(value = "经营月报—销售", response = NoSaleOutData.class)
    @PostMapping({"businessSalesReport"})
    public JsonResult noLogList(HttpServletRequest request, @RequestBody BusinessReportRequest inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClient(userInfo.getClient());
        return JsonResult.success(this.businessReportService.businessSalesReport(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "整体销售—每月", response = NoSaleOutData.class)
    @PostMapping({"listMonthSalesInfo"})
    public JsonResult listMonthSalesInfo(HttpServletRequest request, @RequestBody BusinessReportRequest inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClient(userInfo.getClient());
        return JsonResult.success(this.businessReportService.listMonthSalesInfo(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "整体销售KL—每月", response = NoSaleOutData.class)
    @PostMapping({"listMonthSalesInfoByKL"})
    public JsonResult listMonthSalesInfoByKL(HttpServletRequest request, @RequestBody BusinessReportRequest inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClient(userInfo.getClient());
        return JsonResult.success(this.businessReportService.listMonthSalesInfoByKL(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "单店日均—每月", response = NoSaleOutData.class)
    @PostMapping({"listSingleSalesInfo"})
    public JsonResult listSingleSalesInfo(HttpServletRequest request, @RequestBody BusinessReportRequest inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClient(userInfo.getClient());
        return JsonResult.success(this.businessReportService.listSingleSalesInfo(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "单店日均KL—每月", response = NoSaleOutData.class)
    @PostMapping({"listSingleSalesInfoByKL"})
    public JsonResult listSingleSalesInfoByKL(HttpServletRequest request, @RequestBody BusinessReportRequest inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClient(userInfo.getClient());
        return JsonResult.success(this.businessReportService.listSingleSalesInfoByKL(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "会员销售—每月", response = NoSaleOutData.class)
    @PostMapping({"listVipSalesInfo"})
    public JsonResult listVipSalesInfo(HttpServletRequest request, @RequestBody BusinessReportRequest inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClient(userInfo.getClient());
        return JsonResult.success(this.businessReportService.listVipSalesInfo(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "医保销售—每月", response = NoSaleOutData.class)
    @PostMapping({"listMedicalSalesInfo"})
    public JsonResult listMedicalSalesInfo(HttpServletRequest request, @RequestBody BusinessReportRequest inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClient(userInfo.getClient());
        return JsonResult.success(this.businessReportService.listMedicalSalesInfo(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "公司销售报表周报", response = NoSaleOutData.class)
    @PostMapping({"listCompanyWeeklySalesReport"})
    public JsonResult listCompanyWeeklySalesReport(HttpServletRequest request, @RequestBody BusinessReportRequest inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClient(userInfo.getClient());
        return JsonResult.success(this.businessReportService.listCompanyWeeklySalesReport(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "查询商品分类列表", response = NoSaleOutData.class)
    @PostMapping({"listBigCode"})
    public JsonResult listBigCode(HttpServletRequest request, @RequestBody BusinessReportRequest inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClient(userInfo.getClient());
        return JsonResult.success(this.businessReportService.listBigCode(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "查询加盟商和客户列表", response = NoSaleOutData.class)
    @PostMapping({"listClientOrStores"})
    public JsonResult listClientOrStores(HttpServletRequest request, @RequestBody BusinessReportRequest inData) {
        return JsonResult.success(this.businessReportService.listClientOrStores(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "查询商品成分编码-四级联动", response = NoSaleOutData.class)
    @PostMapping({"listClassCode"})
    public JsonResult listClassCode(HttpServletRequest request, @RequestBody BusinessReportRequest inData) {
        return JsonResult.success(this.businessReportService.listClassCode(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "查询店型字典", response = NoSaleOutData.class)
    @PostMapping({"listStoreType"})
    public JsonResult listStoreType(HttpServletRequest request, @RequestBody BusinessReportRequest inData) {
        return JsonResult.success(this.businessReportService.listStoreType(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "公司销售报表周报", response = NoSaleOutData.class)
    @PostMapping({"exportWeeklySalesReport"})
    public JsonResult exportWeeklySalesReport(HttpServletRequest request, @RequestBody BusinessReportRequest inData)throws IOException {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClient(userInfo.getClient());
        return JsonResult.success(this.businessReportService.exportWeeklySalesReport(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "公司销售报表查询某年的所有周", response = NoSaleOutData.class)
    @PostMapping({"listWeek"})
    public JsonResult listWeek(HttpServletRequest request, @RequestBody BusinessReportRequest inData)throws IOException {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClient(userInfo.getClient());
        return JsonResult.success(this.businessReportService.listWeek(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "查询当前周数", response = NoSaleOutData.class)
    @PostMapping({"getNowWeekNum"})
    public JsonResult getNowWeekNum(HttpServletRequest request, @RequestBody BusinessReportRequest inData)throws IOException {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClient(userInfo.getClient());
        return JsonResult.success(this.businessReportService.getNowWeekNum(inData), "提示：获取数据成功！");
    }

}
