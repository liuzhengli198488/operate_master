package com.gys.controller;

import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.data.SdElectronChangeInData;
import com.gys.common.response.Result;
import com.gys.service.GaiaSdElectronChangeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @desc:
 * @author: ZhangChi
 * @createTime: 2022/1/7 16:01
 */
@Slf4j
@RestController
@RequestMapping({"/electronChange/"})
public class GaiaSdElectronChangeController extends BaseController {
    @Resource
    private GaiaSdElectronChangeService electronChangeService;

    @PostMapping({"/getElectronChange"})
    public JsonResult getElectronChange(HttpServletRequest request, @RequestBody SdElectronChangeInData inData){
        log.info("***电子券异动报表:进入查询");
        GetLoginOutData loginUser = this.getLoginUser(request);
        inData.setClient(loginUser.getClient());

        return JsonResult.success(electronChangeService.getElectronChange(inData),"提示：操作成功！");
    }

    @PostMapping({"exportCSV"})
    public Result exportCSV(HttpServletRequest request, @RequestBody SdElectronChangeInData inData){
        log.info("***电子券异动报表:导出");
        GetLoginOutData loginUser = this.getLoginUser(request);
        inData.setClient(loginUser.getClient());
        return electronChangeService.exportCSV(inData);
    }
}
