package com.gys.mapper;

import com.gys.entity.data.reportFormat.ReportFormatData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReportFormatMapper {

    Integer insertReportFormat(ReportFormatData inData);

    Integer updateReportFormat(ReportFormatData inData);

    Integer deleteReportFormat(ReportFormatData inData);

    List<ReportFormatData> selectReportFormat(ReportFormatData inData);
}
