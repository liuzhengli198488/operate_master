package com.gys.controller;

import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.entity.priceProposal.condition.*;
import com.gys.service.ProductPriceProposalService;
import groovy.util.logging.Slf4j;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;


/**
 * @Author Jinwencheng
 * @Version 1.0
 * @Description 商品价格建议控制器
 * @CreateTime 2022-01-11 14:39:00
 */
@Controller
@Api(tags = "商品价格建议")
@RequestMapping({"/productPriceProposal/"})
@Slf4j
public class ProductPriceProposalController extends BaseController {

    @Autowired
    private ProductPriceProposalService priceProposalService;

    @ApiOperation(value = "生成价格建议数据")
    @PostMapping({"makeData"})
    @ResponseBody
    public JsonResult makeData() {
        priceProposalService.makeData();
        return JsonResult.success(null, "生成成功");
    }

    @ApiOperation(value = "清空所有数据")
    @PostMapping({"clearData"})
    @ResponseBody
    public JsonResult clearData() {
        priceProposalService.clearData();
        return JsonResult.success(null, "清空成功");
    }

    @ApiOperation(value = "查询价格建议列表")
    @PostMapping({"selectPriceProposalList"})
    @ResponseBody
    public JsonResult selectPriceProposalList(HttpServletRequest request, @RequestBody SelectPriceProposalListCondition condition) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        condition.setClientId(userInfo.getClient());
        return JsonResult.success(priceProposalService.selectPriceProposalList(condition), "查询成功");
    }

    @ApiOperation(value = "查询价格建议详情列表")
    @PostMapping({"selectPriceProposalDetailList"})
    @ResponseBody
    public JsonResult selectPriceProposalDetailList(HttpServletRequest request, @Valid @RequestBody SelectPriceProposalDetailListCondition condition) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        condition.setClientId(userInfo.getClient());
        return JsonResult.success(priceProposalService.selectPriceProposalDetailList(condition), "查询成功");
    }

    @ApiOperation(value = "导出")
    @PostMapping({"export"})
    public void export(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody SelectPriceProposalDetailListCondition condition) throws IOException {
        GetLoginOutData userInfo = this.getLoginUser(request);
        condition.setClientId(userInfo.getClient());
        priceProposalService.export(response, condition);
    }

    @ApiOperation(value = "保存价格建议信息")
    @PostMapping({"savePriceProposaInfo"})
    @ResponseBody
    public JsonResult savePriceProposaInfo(HttpServletRequest request, @Valid @RequestBody List<SavePriceProposalCondition> conditions) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        priceProposalService.savePriceProposaInfo(conditions, userInfo.getClient());
        return JsonResult.success(null, "保存成功");
    }

    @ApiOperation(value = "查询所有门店")
    @PostMapping({"selectAllStos"})
    @ResponseBody
    public JsonResult selectAllStos(HttpServletRequest request) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        return JsonResult.success(priceProposalService.selectAllStosByClientId(userInfo.getClient()), "查询成功");
    }

    @ApiOperation(value = "门店快捷选择查询")
    @PostMapping({"stoQuickSearch"})
    @ResponseBody
    public JsonResult stoQuickSearch(HttpServletRequest request, @RequestBody StoQuickSearchCondition condition) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        condition.setClientId(userInfo.getClient());
        return JsonResult.success(priceProposalService.stoQuickSearch(condition), "查询成功");
    }

    @ApiOperation(value = "提交调价")
    @PostMapping({"saveRetailPriceInfo"})
    @ResponseBody
    public JsonResult saveRetailPriceInfo(HttpServletRequest request, @Valid @RequestBody SaveRetailPriceInfoCondition conditions) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        return JsonResult.success(priceProposalService.saveRetailPriceInfo(conditions, userInfo.getClient()), "查询成功");
    }

}
