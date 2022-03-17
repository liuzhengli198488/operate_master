package com.gys.controller;

import com.gys.common.base.BaseController;
import com.gys.common.data.JsonResult;
import com.gys.report.entity.SaveUserCommissionSummaryDetailInData;
import com.gys.service.IUserCommissionSummaryDetailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户提成明细 前端控制器
 * </p>
 *
 * @author yifan.wang
 * @since 2022-02-11
 */
@RestController
@Api(tags = "用户提成明细接口")
@RequestMapping("/api/userCommissionSummaryDetail")
public class UserCommissionSummaryDetailController extends BaseController {

    @Autowired
    private IUserCommissionSummaryDetailService userCommissionSummaryDetailService;

    @ApiOperation(value = "统计每天员工销售提成")
    @PostMapping("saveUserCommissionSummaryDetail")
    private JsonResult saveUserCommissionSummaryDetail(@RequestBody SaveUserCommissionSummaryDetailInData inData) {
        this.userCommissionSummaryDetailService.saveUserCommissionSummaryDetail(inData);
        return JsonResult.success();
    }
}

