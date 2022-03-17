package com.gys.controller;

import com.gys.common.base.BaseController;
import com.gys.common.data.*;
import com.gys.service.AppReportService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/app/report/")
public class AppReportController extends BaseController {
    @Autowired
    private AppReportService appReportService;
    @Autowired
    private HttpServletRequest request;

    /**
     * 获取年份列表
     * @return
     */
    @ApiOperation(value = "查询年份")
    @PostMapping({"yearlist"})
    public JsonResult list(HttpServletRequest request) {
        GetLoginOutData userInfo = getLoginUser(request);
        return JsonResult.success(this.appReportService.selectYearlist(), "提示：获取数据成功！");
    }

    @ApiOperation(value = "查询周/月 type = 1 月份 type = 2 周")
    @PostMapping({"weekOrMonthList"})
    public JsonResult weekOrMonthList(HttpServletRequest request,@RequestBody Map<String,String> inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        return JsonResult.success(this.appReportService.selectWeekOrMonthListByYear(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "品类（大类，中类）销售与毛利结构报表（周/月）",response = ReportOutData.class)
    @PostMapping({"categoryClassList"})
    public JsonResult categoryClassList(HttpServletRequest request, @RequestBody ReportInData inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        return JsonResult.success(this.appReportService.categoryClassList(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "成分品类（大类，中类）销售与毛利结构报表（周/月）",response = ReportOutData.class)
    @PostMapping({"ingredientClassList"})
    public JsonResult ingredientClassList(HttpServletRequest request, @RequestBody ReportInData inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        return JsonResult.success(this.appReportService.ingredientClassList(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "TOP10/50销售成分（周/月）",response = CompClassOutData.class)
    @PostMapping({"compClassList"})
    public JsonResult compClassList(HttpServletRequest request, @RequestBody ReportInData inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        return JsonResult.success(this.appReportService.compClassList(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "TOP10/50销售品种列表（周/月）",response = SaleBreedOutData.class)
    @PostMapping({"saleBreedList"})
    public JsonResult saleBreedList(HttpServletRequest request, @RequestBody ReportInData inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        return JsonResult.success(this.appReportService.saleBreedList(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "TOP10积压天数/成本金额的商品列表",response = OverStockOutData.class)
    @PostMapping({"overStockList"})
    public JsonResult overStockList(HttpServletRequest request, @RequestBody ReportInData inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        //inData.setClientId("10000005");
        return JsonResult.success(this.appReportService.overStockList(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "上周（月）关联率情况汇总",response = RelevancyOutData.class)
    @PostMapping({"selectRelevancyTotal"})
    public JsonResult selectRelevancyTotal(HttpServletRequest request, @RequestBody ReportInData inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        //inData.setClientId("10000003");
        return JsonResult.success(this.appReportService.selectRelevancyTotal(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "上周（月）销售额TOP10所对应的关联成分（前3个）列表清单",response = CompClassRelevancyTotal.class)
    @PostMapping({"selectCompClassRelevancyList"})
    public JsonResult selectCompClassRelevancyList(HttpServletRequest request, @RequestBody ReportInData inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClientId(userInfo.getClient());
//        inData.setClientId("10000005");
        return JsonResult.success(this.appReportService.selectCompClassRelevancyList(inData), "提示：获取数据成功！");
    }

}
