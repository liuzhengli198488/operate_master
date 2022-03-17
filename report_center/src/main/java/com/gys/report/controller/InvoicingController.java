package com.gys.report.controller;

import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.report.entity.InvoicingInData;
import com.gys.report.entity.InvoicingOutData;
import com.gys.report.service.InvoicingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping({"/invoicing/"})
@Slf4j
@Api(tags = "库存管理")
public class InvoicingController extends BaseController {
    @Resource
    private InvoicingService invoicingService;

    @ApiOperation(value = "进销存明细查询", response = InvoicingOutData.class)
    @PostMapping({"/invoicingList"})
    public JsonResult invoicingList(HttpServletRequest request, @RequestBody InvoicingInData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        inData.setGssbBrId(userInfo.getDepId());
        return JsonResult.success(invoicingService.getInvoicingList(inData), "提示：获取数据成功！");
    }

    /**
     * 进销存明细导出
     * @param request
     * @param inData
     * @return
     */
    @PostMapping("invoicingExport")
    public void invoicingExport(HttpServletRequest request, HttpServletResponse response, @RequestBody InvoicingInData inData){
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        inData.setGssbBrId(userInfo.getDepId());
        invoicingService.invoicingExport(inData,response);
    }
}
