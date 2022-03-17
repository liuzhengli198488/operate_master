//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gys.report.controller;

import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.response.Result;
import com.gys.report.common.annotation.FrequencyValid;
import com.gys.report.entity.GetReplenishInData;
import com.gys.report.service.ReplenishWebService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(
        tags = {"补货web相关接口"}
)
@RestController
@RequestMapping({"/replenish/web/"})
public class ReplenishWebController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(ReplenishWebController.class);
    @Resource
    private ReplenishWebService replenishWebService;

    public ReplenishWebController() {
    }

    @ApiOperation(
            value = "门店补货对比差异表",
            notes = "门店补货对比差异表"
    )
    @PostMapping({"listDifferentReplenish"})
    public JsonResult listDifferentReplenish(HttpServletRequest request, @RequestBody GetReplenishInData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        return JsonResult.success(this.replenishWebService.listDifferentReplenish(inData, userInfo), "提示：获取数据成功！");
    }

    @ApiOperation(
            value = "门店补货对比差异明细表",
            notes = "门店补货对比差异明细表"
    )
    @PostMapping({"listDifferentReplenishDetail"})
    public JsonResult listDifferentReplenishDetail(HttpServletRequest request, @RequestBody GetReplenishInData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        return JsonResult.success(this.replenishWebService.listDifferentReplenishDetail(inData, userInfo), "提示：获取数据成功！");
    }

    @FrequencyValid
    @ApiOperation("导出门店补货对比差异表")
    @PostMapping({"/export"})
    public Result export(HttpServletRequest request, @RequestBody GetReplenishInData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        return this.replenishWebService.exportData(inData, userInfo);
    }
}
