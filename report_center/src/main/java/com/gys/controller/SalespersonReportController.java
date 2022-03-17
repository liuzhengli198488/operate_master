package com.gys.controller;

import cn.hutool.json.JSONUtil;
import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.data.PersonalSaleInData;
import com.gys.entity.data.salesSummary.PersonSalesInData;
import com.gys.service.SalespersonReportService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.ParseException;

@RestController
@RequestMapping("/app/salespersonReport/")
@Slf4j
public class SalespersonReportController extends BaseController {

    @Autowired
    private SalespersonReportService salespersonReportService;

    /**
     * 员工个人销售报表
     * @return
     */
//    @PostMapping("personalSales")
//    public JsonResult personalSales(HttpServletRequest request, @RequestBody @Valid PersonalSaleInData inData) throws ParseException {
//        GetLoginOutData userInfo = this.getLoginUser(request);
//        if (StringUtils.isBlank(inData.getClient())){
//            inData.setClient(userInfo.getClient());
//        }
//        if (StringUtils.isBlank(inData.getUserId())){
//            inData.setUserId(userInfo.getUserId());
//        }
//        return JsonResult.success(this.salespersonReportService.personalSales(inData), "提示：获取数据成功！");
//    }

    /**
     * 员工个人销售报表 整合提成方案
     *
     * @return
     */
    @PostMapping("personalSales")
    public JsonResult personalSaleReport(HttpServletRequest request, @RequestBody @Valid PersonalSaleInData inData) throws ParseException {
        GetLoginOutData userInfo = this.getLoginUser(request);
        if (StringUtils.isBlank(inData.getClient())) {
            inData.setClient(userInfo.getClient());
        }
        if (StringUtils.isBlank(inData.getUserId())) {
            inData.setUserId(userInfo.getUserId());
        }
        log.info(String.format("<personalSaleReport><请求参数：%s>", JSONUtil.toJsonStr(inData)));
        return JsonResult.success(this.salespersonReportService.personalSaleReportV2(inData), "提示：获取数据成功！");
    }

    /*@PostMapping({"judgment"})
    public JsonResult judgment(HttpServletRequest request, @RequestBody @Valid PersonalSaleInData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        if (StringUtils.isBlank(inData.getClient())){
            inData.setClient(userInfo.getClient());
        }
        if (StringUtils.isBlank(inData.getUserId())){
            inData.setUserId(userInfo.getUserId());
        }
        return JsonResult.success(this.salespersonReportService.judgment(inData), "提示：获取数据成功！");
    }*/

    /**
     * 查询列表
     *
     * @param request
     * @param inData
     * @return
     */
    @ApiOperation(value = "员工销售排名")
    @PostMapping("/listEmployeeSalesRank")
    public JsonResult listEmployeeSalesRank(HttpServletRequest request, @RequestBody PersonSalesInData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        log.info(String.format("<APP员工销售排名><员工销售排名><请求参数：%s>", JSONUtil.toJsonStr(inData)));
        return JsonResult.success(salespersonReportService.listEmployeeSalesRank(inData), "返回成功");
    }


    /**
     * 首页看板_员工
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "首页看板_员工")
    @GetMapping(value = "getPersonnelData/{dayType}")
    public JsonResult getPersonnelData(HttpServletRequest request, @PathVariable(value = "dayType") Integer dayType) throws ParseException {
        GetLoginOutData userInfo = this.getLoginUser(request);
       /* userInfo.setUserId("1004");
        userInfo.setClient("10000003");*/
        log.info(String.format("<首页看板_员工><请求参数 dayType：%s>", dayType));
        return JsonResult.success(salespersonReportService.getPersonnelData(userInfo, dayType), "返回成功");
    }

}
