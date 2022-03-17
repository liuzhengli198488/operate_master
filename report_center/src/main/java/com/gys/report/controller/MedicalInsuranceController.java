package com.gys.report.controller;


import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.report.entity.dto.MedicalInsturanceSummaryDTO;
import com.gys.report.service.MedicalInsuranceService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("medicalInsurance")
@Api(tags = "居民医保费用")
@Slf4j
public class MedicalInsuranceController extends BaseController {

    @Autowired
    private MedicalInsuranceService medicalInsuranceService;
    //居民医疗费用汇总查询
    @PostMapping("queryMedicalInsuranceSummary")
    public JsonResult queryMedicalInsuranceSummary(HttpServletRequest request, @RequestBody  MedicalInsturanceSummaryDTO summaryDTO){
        GetLoginOutData loginUser = getLoginUser(request);
        summaryDTO.setClientId(loginUser.getClient());
        log.info("加盟商id:{}",summaryDTO.getClientId());
        return this.medicalInsuranceService.queryMedicalInsuranceSummary(summaryDTO,loginUser);
    }
}
