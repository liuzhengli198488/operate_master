package com.gys.report.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.exception.BusinessException;
import com.gys.common.response.Result;
import com.gys.report.entity.*;
import com.gys.report.service.ReportFormsService;
import com.gys.util.CosUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Api(tags = "报表汇总", description = "/reportForms")
@RestController
@RequestMapping({"/reportForms/"})
@Slf4j
public class ReportFormsController extends BaseController {

    @Autowired
    private ReportFormsService reportFormsService;
    @Resource
    public CosUtils cosUtils;

    @ApiOperation(value = "损益单据明细查询", response = BusinessDocumentOutData.class)
    @PostMapping({"selectBusinessDocumentDetailPage"})
    public JsonResult selectBusinessDocumentDetailPage(HttpServletRequest request,@RequestBody BusinessDocumentInData data) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        return JsonResult.success(reportFormsService.selectBusinessDocumentDetailPage(data), "提示：获取数据成功！");
    }

    @ApiOperation(value = "损益单据查询", response = BusinessDocumentOutData.class)
    @PostMapping({"selectBusinessDocumentPage"})
    public JsonResult selectBusinessDocumentPage(HttpServletRequest request,@RequestBody BusinessDocumentInData data) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        return JsonResult.success(reportFormsService.selectBusinessDocumentPage(data), "提示：获取数据成功！");
    }

    @ApiOperation(value = "加盟商下进销存变动查询",response = InventoryChangeSummaryOutData.class)
    @PostMapping({"selectInventoryChangeSummary"})
    public JsonResult selectInventoryChangeSummary(HttpServletRequest request, @RequestBody InventoryChangeSummaryInData data) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        return JsonResult.success(reportFormsService.selectInventoryChangeSummary(data), "提示：获取数据成功！");
    }

    @ApiOperation(value = "门店商品进销存汇总",response = InventoryChangeSummaryOutData.class)
    @PostMapping({"selectInventoryChangeSummaryWithStore"})
    public JsonResult selectInventoryChangeSummaryWithStore(HttpServletRequest request, @RequestBody InventoryChangeSummaryInData data) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        return JsonResult.success(reportFormsService.selectInventoryChangeSummaryWithStore(data), "提示：获取数据成功！");
    }

    @ApiOperation(value = "商品销售汇总查询" ,response = StoreProductSaleClientOutData.class)
    @PostMapping({"selectProductSaleByClient"})
    public JsonResult selectProductSaleByClient(HttpServletRequest request, @RequestBody StoreProductSaleClientInData data) {
         GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        //data.setClient("10000005");
        return JsonResult.success(reportFormsService.selectProductSaleByClient(data), "提示：获取数据成功！");
    }
    /**
    * @date 2022/2/22 17:12
    * @author liuzhengli
    * @Description TODO
    * @param  * @param null
    * @return {@link null}
    * @Version1.0
    **/
    @ApiOperation(value = "门店商品销售汇总查询",response =StoreProductSaleStoreOutData.class)
    @PostMapping({"selectProductSaleByStore"})
    public JsonResult selectProductSaleByStore(HttpServletRequest request,@RequestBody StoreProductSaleStoreInData data) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        //data.setClient("10000005");
        return JsonResult.success(reportFormsService.selectProductSaleByStore(data), "提示：获取数据成功！");
    }
    @ApiOperation(value = "供应商商品销售汇总(供应商用)",response =ProductSalesBySupplierOutData.class)
    @PostMapping({"selectProductSalesBySupplier"} )
    public JsonResult selectProductSalesBySupplier(HttpServletRequest request, @RequestBody ProductSalesBySupplierInData data) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        //data.setClient("10000005");
        return JsonResult.success(reportFormsService.selectProductSalesBySupplier(data), "提示：获取数据成功！");
    }
    @ApiOperation(value = "供应商商品销售明细导出")
    @PostMapping({"exportProductSalesBySupplier"} )
    public Result exportProductSalesBySupplier(HttpServletRequest request, @RequestBody ProductSalesBySupplierInData data) throws IOException {
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        //data.setClient("10000005");
        return reportFormsService.exportProductSalesBySupplier(data);
    }


    @ApiOperation(value = "供应商商品销售汇总(门店用)",response =ProductSalesBySupplierOutData.class)
    @PostMapping({"selectProductSalesBySupplierByStore"} )
    public JsonResult selectProductSalesBySupplierByStore(HttpServletRequest request, @RequestBody ProductSalesBySupplierInData data) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        return JsonResult.success(reportFormsService.selectProductSalesBySupplierByStore(data), "提示：获取数据成功！");
    }

    @ApiOperation(value = "门店分税率销售明细查询", response = StoreRateSellOutData.class)
    @GetMapping({"selectStoreRateSellDetailPage"})
    public JsonResult selectStoreRateSellDetailPage(HttpServletRequest request, StoreRateSellInData data) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        return JsonResult.success(reportFormsService.selectStoreRateSellDetailPage(data), "提示：获取数据成功！");
    }

    @ApiOperation(value = "门店分税率销售明细汇总查询", response = StoreRateSellOutData.class)
    @GetMapping({"selectStoreRateSellPage"})
    public JsonResult selectStoreRateSellPage(HttpServletRequest request, StoreRateSellInData data) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        return JsonResult.success(reportFormsService.selectStoreRateSellPage(data), "提示：获取数据成功！");
    }

    @ApiOperation(value = "门店日期销售明细查询(门店用)")
    @PostMapping({"selectStoreSaleByDay"})
    public JsonResult selectStoreSaleByDay(HttpServletRequest request,@RequestBody StoreSaleDayInData data) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        return JsonResult.success(reportFormsService.selectStoreSaleByDay(data), "提示：获取数据成功！");
    }
    /**
    * @date 2022/2/22 17:35
    * @author liuzhengli
    * @Description TODO
    * @param  * @param null
    * @return {@link null}
    * @Version1.0
    **/
    @ApiOperation(value = "门店日期销售明细查询(加盟商用)")
    @PostMapping({"selectStoreSaleByDayAndClient"})
    public JsonResult selectStoreSaleByDayAndClient(HttpServletRequest request,@RequestBody StoreSaleDayInData data) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        //data.setClient("10000005");
        return JsonResult.success(reportFormsService.selectStoreSaleByDayAndClient(data), "提示：获取数据成功！");
    }

    @ApiOperation(value = "供应商单据明细查询" , response = SupplierDealOutData.class)
    @PostMapping({"selectSupplierDealDetailPage"})
    public JsonResult selectSupplierDealDetailPage(HttpServletRequest request,@RequestBody SupplierDealInData data){
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        return JsonResult.success(reportFormsService.selectSupplierDealDetailPage(data), "提示：获取数据成功！");
    }

    @ApiOperation(value = "供应商往来单据列表" , response = SupplierDealOutData.class)
    @GetMapping({"selectSupplierDealPage"})
    public JsonResult selectSupplierDealPage(HttpServletRequest request,SupplierDealInData data){
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        return JsonResult.success(reportFormsService.selectSupplierDealPage(data), "提示：获取数据成功！");
    }

    @ApiOperation(value = "供应商汇总单据查询",response = SupplierDealOutData.class)
    @PostMapping({"selectSupplierSummaryDealPage"})
    public JsonResult selectSupplierSummaryDealPage(HttpServletRequest request,@RequestBody SupplierDealInData data){
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        return JsonResult.success(reportFormsService.selectSupplierSummaryDealPage(data),"提示: 获取数据成功");
    }

    @ApiOperation(value = "批发销售明细查询", response = WholesaleSaleOutData.class)
    @PostMapping({"selectWholesaleSaleDetailPage"})
    public JsonResult selectWholesaleSaleDetailPage(HttpServletRequest request,@RequestBody WholesaleSaleInData data) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        return JsonResult.success(reportFormsService.selectWholesaleSaleDetailPage(data), "提示：获取数据成功！");
    }
    @ApiOperation(value = "批发销售明细查询导出", response = WholesaleSaleOutData.class)
    @PostMapping({"selectWholesaleSaleDetailPage/export"})
    public void selectWholesaleSaleDetailPageExport(HttpServletRequest request,HttpServletResponse response,@RequestBody WholesaleSaleInData data) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        log.info("<用户：{}调用了批发销售明细查询导出接口>",userInfo.getUserId()+"-"+userInfo.getLoginName());
        reportFormsService.selectWholesaleSaleDetailPageExport(response,data);
    }

    @ApiOperation(value = "批发销售查询", response = WholesaleSaleOutData.class)
    @PostMapping({"selectWholesaleSalePage"})
    public JsonResult selectWholesaleSalePage(HttpServletRequest request,@RequestBody WholesaleSaleInData data) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        return JsonResult.success(reportFormsService.selectWholesaleSalePage(data), "提示：获取数据成功！");
    }
    @ApiOperation(value = "批发销售查询导出", response = WholesaleSaleOutData.class)
    @PostMapping({"selectWholesaleSalePage/export"})
    public void selectWholesaleSalePageExport(HttpServletRequest request,HttpServletResponse response,@RequestBody WholesaleSaleInData data) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        log.info("<用户：{}调用了批发销售查询导出接口>",userInfo.getUserId()+"-"+userInfo.getLoginName());
        reportFormsService.selectWholesaleSalePageExport(response,data);
    }

    @ApiOperation(value = "标记开票")
    @PostMapping({"updateStoreSaleInvoiced"})
    public JsonResult updateStoreSaleInvoiced(HttpServletRequest request, @RequestBody List<UpdateInvoicedInData> data) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.forEach(i->i.setClient(userInfo.getClient()));
        reportFormsService.updateStoreSaleInvoiced(data);
        return JsonResult.success(true,"提示：标记开票成功！");
    }
    @ApiOperation(value = "配送数据汇总查询")
    @PostMapping("deviveryDataCollectSelect")
    public JsonResult deviveryDataCollectSelect(HttpServletRequest request,@RequestBody WholesaleSaleInData data){
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        return JsonResult.success(reportFormsService.selectdeviveryDatasPage(data), "提示：获取数据成功！");
    }
    @ApiOperation(value = "配送数据汇总-导出")
    @PostMapping(value = "deviveryDataCollectSelectExport")
    public void deviveryDataCollectSelectExport(HttpServletRequest request, HttpServletResponse response, @RequestBody WholesaleSaleInData data) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        reportFormsService.deviveryDataCollectSelectExport(response,data);
    }
    @ApiOperation(value = "门店销售分类汇总查询")
    @PostMapping({"selectStoreSaleByDate"})
    public JsonResult selectStoreSaleByDate(HttpServletRequest request,@RequestBody StoreSaleDateInData data) {
       GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        data.setUserRestrictInfo(userInfo.getUserRestrictInfo());
        if("2".equals(userInfo.getUserRestrictInfo().getRestrictType()) && CollectionUtil.isEmpty(userInfo.getUserRestrictInfo().getRestrictStoCodes())){
            throw new BusinessException("此用户在区域下没有门店查看数据权限！");
        }
        //data.setClient("10000005");
        return JsonResult.success(reportFormsService.selectStoreSaleByDate(data), "提示：获取数据成功！");
    }
    @ApiOperation(value = "加盟商下进销存变动明细查询",response = InventoryChangeSummaryDetailOutData.class)
    @PostMapping({"selectInventoryChangeSummaryDetail"})
    public JsonResult selectInventoryChangeSummaryDetail(HttpServletRequest request,@RequestBody InventoryChangeSummaryInData data) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        return JsonResult.success(reportFormsService.selectInventoryChangeSummaryDetail(data), "提示：获取数据成功！");
    }
    @ApiOperation(value = "进销存仓库库存检查")
    @PostMapping({"/selectInventoryStockCheckListByDc"})
    public JsonResult selectInventoryStockCheckListByDc(HttpServletRequest request,@RequestBody InventoryChangeCheckInData data) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        return JsonResult.success(reportFormsService.selectInventoryStockCheckListByDc(data),"提示：获取数据成功");
    }
    @ApiOperation(value = "进销存仓库库存检查导出")
    @PostMapping({"/selectInventoryStockCheckListByDc/export"})
    public void checkListDcExport(HttpServletRequest request, HttpServletResponse response, @RequestBody InventoryChangeCheckInData data) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        reportFormsService.checkListDcExport(data,response);
    }

    @ApiOperation(value = "进销存门店库存检查")
    @PostMapping({"/selectInventoryStockCheckListBySto"})
    public JsonResult selectInventoryStockCheckListBySto(HttpServletRequest request,@RequestBody InventoryChangeCheckInData data) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        return JsonResult.success(reportFormsService.selectInventoryStockCheckListBySto(data),"提示：获取数据成功");
    }

    @ApiOperation(value = "进销门店存库存检查导出")
    @PostMapping({"/selectInventoryStockCheckListBySto/export"})
    public void checkListStoExport(HttpServletRequest request, HttpServletResponse response, @RequestBody InventoryChangeCheckInData data) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        reportFormsService.checkListStoExport(data,response);
    }
}