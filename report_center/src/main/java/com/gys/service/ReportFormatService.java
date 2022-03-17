package com.gys.service;

import com.gys.common.data.*;
import com.gys.common.response.Result;
import com.gys.entity.GaiaSdPayDayreportH;
import com.gys.entity.data.payDayReport.PayDayReportDetailInData;
import com.gys.entity.data.payDayReport.PayDayReportDetailOutData;
import com.gys.entity.data.payDayReport.PayDayReportInData;
import com.gys.entity.data.reportFormat.ReportFormatData;

import java.util.List;
import java.util.Map;

public interface ReportFormatService {

    Integer saveReportFormat(Map inData);

    Integer deleteReportFormat(Map inData);

    ReportFormatData selectReportFormat(Map inData);
}
