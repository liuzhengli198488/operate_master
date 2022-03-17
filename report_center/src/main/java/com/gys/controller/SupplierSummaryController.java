package com.gys.controller;


import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.report.entity.ProductSalesBySupplierWithSaleManOutData;
import com.gys.report.entity.SupplierSummaryInData;
import com.gys.service.SupplierSummaryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Api(tags = "供应商按业务员汇总", description = "supplier-summary")
@RestController
@RequestMapping({"supplierSummary"})
public class SupplierSummaryController extends BaseController {

    @Autowired
    SupplierSummaryService supplierSummaryService;

    @ApiOperation(value = "供应商汇总" ,response = ProductSalesBySupplierWithSaleManOutData.class)
    @PostMapping("getSupplierSummaryList")
    public JsonResult selectSalesSummary(HttpServletRequest request, @RequestBody SupplierSummaryInData summaryData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        summaryData.setWithSaleMan(ObjectUtils.isEmpty(summaryData.getWithSaleMan()) ? "0" : summaryData.getWithSaleMan());
        summaryData.setWithSto(ObjectUtils.isEmpty(summaryData.getWithSto()) ? "0" : summaryData.getWithSto());
        summaryData.setWithPro(ObjectUtils.isEmpty(summaryData.getWithPro()) ? "0" : summaryData.getWithPro());
        summaryData.setClient(userInfo.getClient());
        return JsonResult.success(supplierSummaryService.getSupplierSummaryList(summaryData), "返回成功");
    }


    @ApiOperation(value = "获取业务员")
    @PostMapping("getSaleManList")
    public JsonResult getSaleManList(HttpServletRequest request, @RequestBody SupplierSummaryInData summaryData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        return JsonResult.success(supplierSummaryService.getSaleManList(userInfo.getClient(),summaryData.getSupplierCode()), "返回成功");
    }
}

