package com.gys.service;

import com.gys.common.data.*;
import com.gys.common.response.Result;
import com.gys.entity.GaiaSdPayDayreportH;
import com.gys.entity.data.payDayReport.PayDayReportDetailInData;
import com.gys.entity.data.payDayReport.PayDayReportDetailOutData;
import com.gys.entity.data.payDayReport.PayDayReportInData;

import java.util.List;
import java.util.Map;

public interface SaleReportService {
    NoSaleOutData selectNosaleReportList(SaleReportInData inData);

    List<MedicalInsuranceOutData>selectMedicalInsuranceList(MedicalInsuranceInData inData);

    Result exportMedicalInsurance(MedicalInsuranceInData inData, String fileName);


    PageInfo selectNosaleReportListNew(SaleReportInData inData);

    List<Map<String, Object>> selectNosaleReportListNewExcel(SaleReportInData inData);

    List<GaiaSdPayDayreportH> selectPayDayReport(PayDayReportInData inData);

    List<PayDayReportDetailOutData> selectPayDayReportDetail(PayDayReportDetailInData inData);
}
