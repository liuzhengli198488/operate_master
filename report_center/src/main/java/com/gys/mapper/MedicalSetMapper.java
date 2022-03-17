package com.gys.mapper;

import com.gys.report.entity.*;
import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;

import java.util.List;
@Mapper
public interface MedicalSetMapper {

    List<MedicalSetOutData> selectMedicalSetList(MedicalSetInData inData);

    List<MedicalSalesOutData> selectMedicalSalesList(MedicalSalesInData inData);

    /**
     * 医保销售对账汇总(对总账)
     * @param inData 入参
     * @return
     */
    List<SelectMedicalSummaryDTO> selectMedicalSummaryList(@Param("inData") SelectMedicalSummaryData inData);

    /**
     * 医保销售对账汇总(按照机构(名称))
     * @param inData
     * @return
     */
    List<SelectMedicalSummaryDTO> selectMedicalStoreSummaryList(@Param("inData")SelectMedicalSummaryData inData);
}
