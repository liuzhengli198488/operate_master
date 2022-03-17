package com.gys.controller;


import com.gys.common.base.BaseController;
import com.gys.common.data.*;
import com.gys.common.log.Log;
import com.gys.common.response.Result;
import com.gys.entity.data.payDayReport.PayDayReportDetailInData;
import com.gys.entity.data.payDayReport.PayDayReportInData;
import com.gys.service.ReportFormatService;
import com.gys.service.SaleReportService;
import com.gys.service.StoreDataService;
import com.gys.util.CommonUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/format/")
public class ReportFormatController extends BaseController {

    @Resource
    private ReportFormatService reportFormatService;


    @PostMapping({"saveReportFormat"})
    public JsonResult saveReportFormat(HttpServletRequest request, @RequestBody Map inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.put("client",userInfo.getClient());
        inData.put("userId",userInfo.getUserId());
        return JsonResult.success( this.reportFormatService.saveReportFormat(inData), "提示：保存数据成功！");
    }

    @PostMapping({"deleteReportFormat"})
    public JsonResult deleteReportFormat(HttpServletRequest request, @RequestBody Map inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.put("client",userInfo.getClient());
        inData.put("userId",userInfo.getUserId());
        return JsonResult.success( this.reportFormatService.deleteReportFormat(inData), "提示：重置数据成功！");
    }

    @PostMapping({"selectReportFormat"})
    public JsonResult selectReportFormat(HttpServletRequest request, @RequestBody Map inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.put("client",userInfo.getClient());
        inData.put("userId",userInfo.getUserId());
        return JsonResult.success( this.reportFormatService.selectReportFormat(inData), "提示：获取数据成功！");
    }
}
