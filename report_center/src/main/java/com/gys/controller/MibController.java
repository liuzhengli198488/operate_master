package com.gys.controller;

import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.entity.InData;
import com.gys.service.MibService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@RequestMapping({"/mib/"})
@Controller
public class MibController extends BaseController {
    @Autowired
    private MibService mibService;

   /**
     * setlInfo查询
     * 查询医保销售
     * @param request
     * @return
     */
    @PostMapping({"getSeltInfo"})
    @ResponseBody
    public JsonResult getSeltInfo(HttpServletRequest request, @RequestBody InData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        return JsonResult.success( this.mibService.getSeltInfo(userInfo, inData), "success");
    }


    /**
     * setlInfo查询（对账明细用）
     * 查询医保销售
     * @param request
     * @return
     */
    @PostMapping({"getSeltInfo3202"})
    @ResponseBody
    public JsonResult getSeltInfo3202(HttpServletRequest request, @RequestBody InData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        return JsonResult.success( this.mibService.getSeltInfo3202(userInfo, inData), "success");
    }

    /**
     * setlInfo查询
     * 医保对账
     * @param request
     * @return
     */
    @PostMapping({"getSeltInfoSum"})
    @ResponseBody
    public JsonResult getSeltInfoSum(HttpServletRequest request, @RequestBody InData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        return JsonResult.success( this.mibService.getSeltInfoSum(userInfo, inData), "success");
    }

    /**
     * setlInfo查询
     * 医保对账
     * @param request
     * @return
     */
    @PostMapping({"getSeltInfoSumByHlj2"})
    @ResponseBody
    public JsonResult getSeltInfoSumByHlj2(HttpServletRequest request, @RequestBody InData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        return JsonResult.success( this.mibService.getSeltInfoSumByHlj2(userInfo, inData), "success");
    }


    /**
     * setlInfo查询
     * 医保对账
     * @param request
     * @return
     */
    @PostMapping({"getSeltInfoSum2"})
    @ResponseBody
    public JsonResult getSeltInfoSum2(HttpServletRequest request, @RequestBody InData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        return JsonResult.success( this.mibService.getSeltInfoSum2(userInfo, inData), "success");
    }

}
