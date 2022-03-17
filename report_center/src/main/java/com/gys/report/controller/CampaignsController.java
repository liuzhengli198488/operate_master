package com.gys.report.controller;

import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.entity.data.salesSummary.SalesSummaryData;
import com.gys.report.entity.ProCampaignsOutData;
import com.gys.report.service.ProCampaignsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Api(tags = "活动商品报表相关接口")
@RestController
@RequestMapping({"/campaigns/"})
@Slf4j
public class CampaignsController extends BaseController {

    @Resource
    private ProCampaignsService campaignsService;

    @ApiOperation(value = "门店活动商品明细查询",response = ProCampaignsOutData.class)
    @PostMapping("/selectCampainsProDetails")
    public JsonResult selectCampainsProDetails(HttpServletRequest request, @Valid @RequestBody SalesSummaryData summaryData){
        GetLoginOutData userInfo = this.getLoginUser(request);
        summaryData.setClient(userInfo.getClient());
        return JsonResult.success(campaignsService.selectCampainsProDetails(summaryData),"返回成功");
    }

    @ApiOperation(value = "门店活动商品汇总查询",response = ProCampaignsOutData.class)
    @PostMapping("/selectCampainsProTotal")
    public JsonResult selectCampainsProTotal(HttpServletRequest request, @Valid @RequestBody SalesSummaryData summaryData){
        GetLoginOutData userInfo = this.getLoginUser(request);
        summaryData.setClient(userInfo.getClient());
        return JsonResult.success(campaignsService.selectCampainsProTotal(summaryData),"返回成功");
    }

   }
