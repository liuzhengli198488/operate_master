package com.gys.service;
import com.github.pagehelper.PageInfo;
import com.gys.entity.GaiaProductBusiness;
import com.gys.entity.wk.dto.*;
import com.gys.entity.wk.entity.GaiaMedicinalQualityRecord;
import com.gys.entity.wk.vo.GaiaMedicinalQualityRecordVo;
import com.gys.entity.wk.vo.GetProductThirdlyOutData;

import java.util.List;

/**
 * @Auther: tzh
 * @Date: 2021/12/30 15:43
 * @Description: MedicinalQualityRecordService
 * @Version 1.0.0
 */
public interface MedicinalQualityRecordService {
    void add(GaiaMedicinalQualityRecord record);

    PageInfo<GaiaMedicinalQualityRecordVo> searchRecords(MedicinalQualityRecordQueryDto dto);

    void del(MedicinalQualityRecordDelDto dto);

    PageInfo<GetProductThirdlyOutData> queryProFourthly(GetProductThirdlyInData inData);

    GaiaMedicinalQualityRecord getOneRecord(MedicinalQualityRecordDetailDto dto);

    void update(List<MedicinalQualityRecordUpdateDto> dtos);

    void addRecords(List<MedicinalQualityRecordDto> dtos);

    GaiaMedicinalQualityRecord selectOne(Long id);

    void updateById(MedicinalQualityRecordUpdateDto dto);

    List<GaiaMedicinalQualityRecord> selectList(List<Long> ids);

    String test();
}
