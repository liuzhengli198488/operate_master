package com.gys.mapper;
import com.gys.entity.EmployeeHealthRecordDto;
import com.gys.entity.EmployeeHealthRecordReportVo;
import com.gys.entity.EmployeeHealthRecordVo;
import com.gys.entity.GaiaEmployeeHealthRecord;

import java.util.List;

public interface GaiaEmployeeHealthRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GaiaEmployeeHealthRecord record);

    int insertSelective(GaiaEmployeeHealthRecord record);

    GaiaEmployeeHealthRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GaiaEmployeeHealthRecord record);

    int updateByPrimaryKey(GaiaEmployeeHealthRecord record);

    List<EmployeeHealthRecordVo> getEmployeeHealthRecords(EmployeeHealthRecordDto dto);

    List<EmployeeHealthRecordReportVo> reportEmployeeHealthRecords(EmployeeHealthRecordDto dto);
}