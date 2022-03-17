package com.gys.controller;

import cn.hutool.json.JSONUtil;
import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.entity.data.marketing.MarketingInDate;
import com.gys.entity.data.marketing.StartAndEndDayInData;
import com.gys.service.MarketingReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/marketing")
@Slf4j
public class MarketingReportController extends BaseController {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private MarketingReportService marketingReportService;



    @PostMapping("/getMonthReport")
    public JsonResult getMonthReport(@RequestBody StartAndEndDayInData inData) {
        GetLoginOutData userInfo = super.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        
        /*inData.setClientId("10000001");
        inData.setBrId("10001");*/
        return marketingReportService.getMonthReport(inData);
    }

    @PostMapping("/getMarketingInfoByPlanCode")
    public JsonResult getMarketingInfoByPlanCode(@RequestBody MarketingInDate inData) {
        GetLoginOutData userInfo = super.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        log.info(String.format("<APP营销报告><营销报告信息><请求参数：%s>", JSONUtil.toJsonStr(inData)));
        return JsonResult.success(marketingReportService.getMarketingInfoByPlanCode(inData), "提示：获取数据成功！");
    }

    @PostMapping("/listMarketingStores")
    public JsonResult listMarketingStores(@RequestBody MarketingInDate inData) {
        GetLoginOutData userInfo = super.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        inData.setUserId(userInfo.getUserId());
        log.info(String.format("<APP营销报告><查询参与活动的门店><请求参数：%s>", JSONUtil.toJsonStr(inData)));
        return JsonResult.success(marketingReportService.listMarketingStores(inData), "提示：获取数据成功！");
    }

    @PostMapping("/listVIPInfoByPlanCode")
    public JsonResult listVIPInfoByPlanCode(@RequestBody MarketingInDate inData) {
        GetLoginOutData userInfo = super.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        inData.setUserId(userInfo.getUserId());
        log.info(String.format("<APP营销报告><营销报告信息-VIP><请求参数：%s>", JSONUtil.toJsonStr(inData)));
        return JsonResult.success(marketingReportService.listVIPInfoByPlanCode(inData), "提示：获取数据成功！");
    }

    @PostMapping("/listMidTop")
    public JsonResult listMidTOP(@RequestBody MarketingInDate inData) {
        GetLoginOutData userInfo = super.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        inData.setUserId(userInfo.getUserId());
        log.info(String.format("<APP营销报告><中类TOP><请求参数：%s>", JSONUtil.toJsonStr(inData)));
        return JsonResult.success(marketingReportService.listMidTOP(inData), "提示：获取数据成功！");
    }

    @PostMapping("/listSalesTop")
    public JsonResult listSalesTop(@RequestBody MarketingInDate inData) {
        GetLoginOutData userInfo = super.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        inData.setUserId(userInfo.getUserId());
        log.info(String.format("<APP营销报告><销售额TOP><请求参数：%s>", JSONUtil.toJsonStr(inData)));
        return JsonResult.success(marketingReportService.listSalesTop(inData), "提示：获取数据成功！");
    }

    @PostMapping("/listSalesQtyTop")
    public JsonResult listSalesQtyTop(@RequestBody MarketingInDate inData) {
        GetLoginOutData userInfo = super.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        inData.setUserId(userInfo.getUserId());
        log.info(String.format("<APP营销报告><销售量TOP><请求参数：%s>", JSONUtil.toJsonStr(inData)));
        return JsonResult.success(marketingReportService.listSalesQtyTop(inData), "提示：获取数据成功！");
    }

    @PostMapping("/listGoodsInfoByPlanCode")
    public JsonResult listGoodsInfoByPlanCode(@RequestBody MarketingInDate inData) {
        GetLoginOutData userInfo = super.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        inData.setUserId(userInfo.getUserId());
        log.info(String.format("<APP营销报告><商品><请求参数：%s>", JSONUtil.toJsonStr(inData)));
        return JsonResult.success(marketingReportService.listGoodsInfoByPlanCode(inData), "提示：获取数据成功！");
    }
}
