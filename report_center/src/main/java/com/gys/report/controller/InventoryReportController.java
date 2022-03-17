package com.gys.report.controller;

import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.report.entity.DifferenceResultDetailedQueryOutData;
import com.gys.report.entity.DifferenceResultQueryInVo;
import com.gys.report.entity.DifferenceResultQueryOutData;
import com.gys.report.entity.InventoryDocumentsOutData;
import com.gys.report.service.InventoryReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
/**
 * 盘点报表控制器
 *
 * @author xiaoyuan on 2020/9/8
 */
@RestController
@Api(tags = "盘点报表")
@RequestMapping({"/inventoryReport"})
public class InventoryReportController extends BaseController {

    @Autowired
    private InventoryReportService reportService;


    @ApiOperation(value = "盘点差异结果查询", response = DifferenceResultQueryOutData.class)
    @PostMapping("/differenceResultQuery")
    public JsonResult differenceResultQuery(HttpServletRequest request, @Valid @RequestBody DifferenceResultQueryInVo inData) {
        GetLoginOutData userInfo = super.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        inData.setBrId(userInfo.getDepId());
        return JsonResult.success(this.reportService.getDifferenceResultQuery(inData), "success");
    }

    @ApiOperation(value = "盘点差异结果明细查询", response = DifferenceResultDetailedQueryOutData.class)
    @PostMapping("/differenceResultDetailedQuery")
    public JsonResult differenceResultDetailedQuery(HttpServletRequest request, @Valid @RequestBody DifferenceResultQueryInVo inData) {
        GetLoginOutData userInfo = super.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        return JsonResult.success(this.reportService.getDifferenceResultDetailedQuery(inData), "success");
    }

    @ApiOperation(value = "门店盘点单据查询", response = InventoryDocumentsOutData.class)
    @PostMapping("/inventoryDocumentQuery")
    public JsonResult inventoryDocumentQuery(HttpServletRequest request, @Valid @RequestBody DifferenceResultQueryInVo inData) {
        GetLoginOutData userInfo = super.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        return JsonResult.success(this.reportService.inventoryDocumentQuery(inData), "success");
    }

    //盘点数据差异
    @PostMapping("/getInventoryDataDetail")
    public JsonResult getInventoryDataDetail(HttpServletRequest request, @Valid @RequestBody DifferenceResultQueryInVo inData) {
        GetLoginOutData userInfo = super.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        inData.setBrSite(userInfo.getDepId());
        return JsonResult.success(this.reportService.getInventoryDataDetail(inData), "提示：查询成功！");
    }
}
