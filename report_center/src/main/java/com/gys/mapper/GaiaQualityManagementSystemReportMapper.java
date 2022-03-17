package com.gys.mapper;


import com.gys.entity.wk.dto.QualityManagementSystemReportDelDto;
import com.gys.entity.wk.dto.QualityManagementSystemReportDto;
import com.gys.entity.wk.dto.QualityManagementSystemReportQueryDto;
import com.gys.entity.wk.dto.QualityManagementSystemReportUpdateDto;
import com.gys.entity.wk.entity.GaiaQualityManagementSystemReport;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GaiaQualityManagementSystemReportMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GaiaQualityManagementSystemReport record);

    int insertSelective(GaiaQualityManagementSystemReport record);

    GaiaQualityManagementSystemReport selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GaiaQualityManagementSystemReport record);

    int updateByPrimaryKey(GaiaQualityManagementSystemReport record);

    void addList(@Param("dtos") List<QualityManagementSystemReportDto> dtos);

    List searchRecords(QualityManagementSystemReportQueryDto dto);

    List<GaiaQualityManagementSystemReport> selectList(@Param("dto") List<Long> dto);


    void delList( QualityManagementSystemReportDelDto dto);

    void updateList(QualityManagementSystemReportUpdateDto dto);
}