package com.gys.report.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.response.Result;
import com.gys.entity.SysDic;
import com.gys.report.entity.GetMemberInfoInData;
import com.gys.report.entity.GetSalesSummaryOfSalesmenReportInData;
import com.gys.report.entity.SalespersonsSalesDetailsOutData;
import com.gys.report.entity.SupplierInfoDTO;
import com.gys.report.service.SalesSummaryOfSalesmenReportService;
import com.gys.service.ISysDicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wavesen.shen
 */
@Api(tags = "营业员销售查询")
@RestController
@RequestMapping({"/SalesSummaryOfSalesmenReport/"})
public class SalesSummaryOfSalesmenReportController extends BaseController {
    @Resource
    private SalesSummaryOfSalesmenReportService salesSummaryOfSalesmenReportService;
    @Resource
    private ISysDicService sysDicService;

    @ApiOperation(value = "列表条件查询")
    @PostMapping("queryReport")
    public JsonResult queryReport(HttpServletRequest request, @Valid @RequestBody GetSalesSummaryOfSalesmenReportInData inData){
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        inData.setBrId(userInfo.getDepId());
        return JsonResult.success(this.salesSummaryOfSalesmenReportService.queryReport(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "联合用药查询")
    @PostMapping("/union/queryReport")
    public JsonResult unionQueryReport(HttpServletRequest request, @Valid @RequestBody GetSalesSummaryOfSalesmenReportInData inData){
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        inData.setBrId(userInfo.getDepId());
        return JsonResult.success(this.salesSummaryOfSalesmenReportService.unionQueryReport(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "联合用药查询")
    @PostMapping("/union/queryExport")
    public Result unionQueryExport(HttpServletRequest request, @Valid @RequestBody GetSalesSummaryOfSalesmenReportInData inData){
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        inData.setBrId(userInfo.getDepId());
        return this.salesSummaryOfSalesmenReportService.unionQueryExport(inData);
    }

    @ApiOperation(value = "商品销售明细报表初始化销售状态下拉",response = SalespersonsSalesDetailsOutData.class)
    @GetMapping("getProStatusList")
    public JsonResult getProStatusList(HttpServletRequest request){
        GetLoginOutData userInfo = this.getLoginUser(request);
        List<String> resList = new ArrayList<>();
        List<SysDic> proStatusList = this.sysDicService.getSysDicListByTypeCode(null, null, "PRO_STATUS");
        if(CollectionUtil.isNotEmpty(proStatusList)){
            for(SysDic sysDic : proStatusList){
                resList.add(sysDic.getKeyCode());
            }
        }
//        return JsonResult.success(this.salesSummaryOfSalesmenReportService.getProStatusList(userInfo), "提示：获取数据成功！");
        return JsonResult.success(resList, "提示：获取数据成功！");
    }

    @ApiOperation(value = "营业员销售明细条件查询(门店用)",response = SalespersonsSalesDetailsOutData.class)
    @PostMapping("getSalespersonsSalesDetails")
    public JsonResult getSalespersonsSalesDetails(HttpServletRequest request, @Valid @RequestBody GetSalesSummaryOfSalesmenReportInData inData){
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        return JsonResult.success(this.salesSummaryOfSalesmenReportService.getSalespersonsSalesDetails(inData), "提示：获取数据成功！");
    }

    /**
     * 商品销售明细表导出
     */
    @PostMapping("exportSalespersonsSalesDetails")
    public void exportSalespersonsSalesDetails(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody GetSalesSummaryOfSalesmenReportInData inData){
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        this.salesSummaryOfSalesmenReportService.exportSalespersonsSalesDetails(inData,response);
    }

    @ApiOperation(value = "营业员销售明细条件查询(加盟商用)",response = SalespersonsSalesDetailsOutData.class)
    @PostMapping("getSalespersonsSalesDetailsByClient")
    public JsonResult getSalespersonsSalesDetailsByClient(HttpServletRequest request, @Valid @RequestBody GetSalesSummaryOfSalesmenReportInData inData){
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        //inData.setClientId("10000005");
        return JsonResult.success(this.salesSummaryOfSalesmenReportService.getSalespersonsSalesDetailsByClient(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "营业员销售明细按分类导出按商品",response = SalespersonsSalesDetailsOutData.class)
    @PostMapping("getSalespersonsSalesByPro")
    public JsonResult getSalespersonsSalesByClass(HttpServletRequest request, @Valid @RequestBody GetSalesSummaryOfSalesmenReportInData inData){
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        return JsonResult.success(this.salesSummaryOfSalesmenReportService.getSalespersonsSalesByPro(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "营业员销售明细按分类导出按人员",response = SalespersonsSalesDetailsOutData.class)
    @PostMapping("getSalespersonsSalesByUser")
    public JsonResult getSalespersonsSalesByUser(HttpServletRequest request, @Valid @RequestBody GetSalesSummaryOfSalesmenReportInData inData){
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        return JsonResult.success(this.salesSummaryOfSalesmenReportService.getSalespersonsSalesByUser(inData), "提示：获取数据成功！");
    }
    @ApiOperation(value = "营业员销售明细按分类导出按医生",response = SalespersonsSalesDetailsOutData.class)
    @PostMapping("getSalespersonsSalesByDoctor")
    public JsonResult getSalespersonsSalesByDoctor(HttpServletRequest request, @Valid @RequestBody GetSalesSummaryOfSalesmenReportInData inData){
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        return JsonResult.success(this.salesSummaryOfSalesmenReportService.getSalespersonsSalesByDoctor(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "供应商列表查询" ,response = SupplierInfoDTO.class)
    @GetMapping({"getSupByClient"})
    public JsonResult getSupByClient(HttpServletRequest request) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        return JsonResult.success(this.salesSummaryOfSalesmenReportService.getSupByClient(userInfo.getClient()), "提示：获取数据成功！");
    }

    @PostMapping("queryMemberInfo")
    public JsonResult queryMemberInfo(HttpServletRequest request, @RequestBody GetMemberInfoInData inData){
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        inData.setBrId(userInfo.getDepId());
        return JsonResult.success(this.salesSummaryOfSalesmenReportService.queryMemberInfo(inData), "提示：获取数据成功！");
    }

    @PostMapping({"/getProTssx"})
    public JsonResult getProTssx(HttpServletRequest request){
        GetLoginOutData userInfo = this.getLoginUser(request);
        String client = userInfo.getClient();
        return JsonResult.success(salesSummaryOfSalesmenReportService.getProTssx(client),"提示：获取数据成功！");
    }
    @ApiOperation(value = "加盟商下所以营业员")
    @PostMapping({"selectClientUser"})
    public JsonResult selectClientUser(HttpServletRequest request) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        return JsonResult.success(this.salesSummaryOfSalesmenReportService.selectClientUser(userInfo.getClient()), "提示：获取数据成功！");
    }
}
