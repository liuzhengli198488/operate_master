package com.gys.controller;

import cn.hutool.json.JSONUtil;
import com.gys.common.response.Result;
import com.gys.common.response.ResultUtil;
import com.gys.entity.data.commodityInventoryReport.CommoditySummaryInData;
import com.gys.entity.data.commodityInventoryReport.CommoditySummaryOutData;
import com.gys.service.CommodityInventoryReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @desc:
 * @author: RULAISZ
 * @createTime: 2021/11/26 15:36
 */
@Slf4j
@RestController
@RequestMapping("/commodityInventoryReport")
public class CommodityInventoryReportController {

    @Autowired
    private CommodityInventoryReportService commodityInventoryReportService;

    /**
     * 系统级-汇总表
     */
    @PostMapping("summaryList")
    public Result summaryList(@RequestBody @Valid CommoditySummaryInData inData){
        log.info(String.format("<商品调库><系统级-汇总表><请求参数：%s>", JSONUtil.toJsonStr(inData)));
        List<CommoditySummaryOutData> list = commodityInventoryReportService.summaryList(inData);
        return ResultUtil.success(list);
    }

    /**
     * 系统级-汇总表
     */
    @PostMapping("summaryExport")
    public Result summaryExport(@RequestBody @Valid CommoditySummaryInData inData){
        log.info(String.format("<商品调库><系统级-汇总表-导出><请求参数：%s>", JSONUtil.toJsonStr(inData)));
        return commodityInventoryReportService.summaryExport(inData);
    }
}
