package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.entity.GaiaSdPayDayreportH;
import com.gys.entity.data.payDayReport.PayDayReportInData;

import java.util.List;

public interface GaiaSdPayDayreportHMapper extends BaseMapper<GaiaSdPayDayreportH> {
    List<GaiaSdPayDayreportH> selectPayDayReport(PayDayReportInData inData);
}