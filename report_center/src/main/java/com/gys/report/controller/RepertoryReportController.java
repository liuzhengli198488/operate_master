package com.gys.report.controller;


import com.gys.common.base.BaseController;
import com.gys.common.data.*;
import com.gys.common.response.Result;
import com.gys.report.entity.RepertoryReportInData;
import com.gys.report.service.RepertoryReportService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/repertory/")
public class RepertoryReportController extends BaseController {

    @Resource
    private RepertoryReportService repertoryReportService;

	@PostMapping({"selectRepertoryData"})
    public JsonResult selectRepertoryData(HttpServletRequest request, @RequestBody RepertoryReportInData inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClient(userInfo.getClient());
        inData.setUserId(userInfo.getUserId());
        return JsonResult.success( this.repertoryReportService.selectRepertoryData(inData), "提示：获取数据成功！");
    }
    @PostMapping({"exportRepertoryData"})
    public Result exportRepertoryData(HttpServletRequest request, @RequestBody RepertoryReportInData inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClient(userInfo.getClient());
        inData.setUserId(userInfo.getUserId());
        return  this.repertoryReportService.exportRepertoryData(inData);
    }
}
