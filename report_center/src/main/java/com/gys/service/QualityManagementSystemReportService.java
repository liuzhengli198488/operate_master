package com.gys.service;

import com.github.pagehelper.PageInfo;
import com.gys.entity.wk.dto.QualityManagementSystemReportDelDto;
import com.gys.entity.wk.dto.QualityManagementSystemReportDto;
import com.gys.entity.wk.dto.QualityManagementSystemReportQueryDto;
import com.gys.entity.wk.dto.QualityManagementSystemReportUpdateDto;
import com.gys.entity.wk.entity.GaiaMedicinalQualityRecord;
import com.gys.entity.wk.entity.GaiaQualityManagementSystemReport;

import java.util.List;

/**
 * @Auther: tzh
 * @Date: 2022/1/4 13:29
 * @Description: QualityManagementSystemReportService
 * @Version 1.0.0
 */
public interface QualityManagementSystemReportService {
    void addRecords(List<QualityManagementSystemReportDto> dtos);

    PageInfo<GaiaQualityManagementSystemReport> searchRecords(QualityManagementSystemReportQueryDto dto);

    List<GaiaQualityManagementSystemReport> selectList(List<Long> dto);

    void delList(QualityManagementSystemReportDelDto dto);


    GaiaQualityManagementSystemReport selectOne(Long id);



    void updateList(QualityManagementSystemReportUpdateDto dto);
}
