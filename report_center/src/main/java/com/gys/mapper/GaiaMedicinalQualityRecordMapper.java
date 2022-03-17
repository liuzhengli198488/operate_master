package com.gys.mapper;
import com.gys.common.base.BaseMapper;
import com.gys.entity.wk.dto.MedicinalQualityRecordDelDto;
import com.gys.entity.wk.dto.MedicinalQualityRecordDto;
import com.gys.entity.wk.dto.MedicinalQualityRecordQueryDto;
import com.gys.entity.wk.dto.MedicinalQualityRecordUpdateDto;
import com.gys.entity.wk.entity.GaiaMedicinalQualityRecord;
import com.gys.entity.wk.vo.GaiaMedicinalQualityRecordVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GaiaMedicinalQualityRecordMapper extends BaseMapper<GaiaMedicinalQualityRecord> {
    void del(MedicinalQualityRecordDelDto dto);

    void updateList(List<MedicinalQualityRecordUpdateDto> dtos);

    void addList(List<MedicinalQualityRecordDto> dtos);

    List<GaiaMedicinalQualityRecordVo> searchRecords(MedicinalQualityRecordQueryDto dto);

    List<GaiaMedicinalQualityRecord> selectList(@Param("ids") List<Long> ids);
}