package com.gys.controller;

import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.data.SalesStatisticsReportInData;
import com.gys.common.data.SalesStatisticsReportOutData;
import com.gys.service.SalesStatisticsReportService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/salesStatisticsReport/")
public class SalesStatisticsReportController extends BaseController {

    @Autowired
    private SalesStatisticsReportService salesStatisticsReportService;

    @ApiOperation(value = "查询",response = SalesStatisticsReportOutData.class)
    @PostMapping({"query"})
    public JsonResult query(HttpServletRequest request, @RequestBody SalesStatisticsReportInData inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClientId(userInfo.getClient());
//        inData.setClientId("10000005");
        return JsonResult.success(this.salesStatisticsReportService.query(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "导出")
    @PostMapping({"export"})
    public JsonResult export(HttpServletRequest request, @RequestBody SalesStatisticsReportInData inData, HttpServletResponse response) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
//        inData.setClientId("10000005");

        return this.salesStatisticsReportService.export(inData,response);
    }

    @ApiOperation(value = "查询默认时间")
    @PostMapping({"queryDefaultDate"})
    public JsonResult queryDefaultDate(HttpServletRequest request) {
        return this.salesStatisticsReportService.queryDefaultDate();
    }
}
