package com.gys.report.service;

import com.gys.common.response.Result;
import com.gys.report.entity.RepertoryReportInData;
import com.gys.report.entity.RepertoryReportOutData;


public interface RepertoryReportService {
    RepertoryReportOutData selectRepertoryData(RepertoryReportInData inData);


    Result exportRepertoryData(RepertoryReportInData inData);
}
