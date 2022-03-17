package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.entity.GaiaSdPayDayreportD;
import com.gys.entity.data.payDayReport.PayDayReportDetailInData;
import com.gys.entity.data.payDayReport.PayDayReportDetailOutData;

import java.util.List;

public interface GaiaSdPayDayreportDMapper extends BaseMapper<GaiaSdPayDayreportD> {
    List<PayDayReportDetailOutData> selectPayDayReportDetail(PayDayReportDetailInData inData);
}