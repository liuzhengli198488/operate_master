package com.gys.report.service;

import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.report.entity.dto.MedicalInsturanceSummaryDTO;

public interface MedicalInsuranceService {

    //居民医疗费用汇总
    JsonResult queryMedicalInsuranceSummary(MedicalInsturanceSummaryDTO summaryDTO, GetLoginOutData login);

}
