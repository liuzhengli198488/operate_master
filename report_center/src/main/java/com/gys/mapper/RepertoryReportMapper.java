package com.gys.mapper;

import com.gys.report.entity.RepertoryReportData;
import com.gys.report.entity.RepertoryReportInData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RepertoryReportMapper {

    List<RepertoryReportData> selectRepertoryData(RepertoryReportInData inData);

    Integer selectCount(RepertoryReportInData inData);
}
