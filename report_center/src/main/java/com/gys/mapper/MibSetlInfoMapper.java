package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.entity.InData;
import com.gys.entity.MibInfoOutData;
import com.gys.entity.MibSetlInfo;
import com.gys.report.entity.dto.MedicalInsturanceSummaryDTO;
import com.gys.report.entity.vo.MedicalInsturanceSummaryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MibSetlInfoMapper extends BaseMapper<MibSetlInfo> {

	List<MibInfoOutData> getSeltInfo(InData inData);



    List<MibInfoOutData> getSeltInfo3202(InData inData);


    List<MibInfoOutData> getSeltInfoSum(InData inData);
    List<MibInfoOutData> getSeltInfoSumByHlj(InData inData);
    String selectStoPriceComparison(@Param("clientId") String client, @Param("stoCode") String stoCode, @Param("paramStr") String paramStr);
    List<MibInfoOutData> getSeltInfoSumByHlj2(InData inData);

    List<MedicalInsturanceSummaryVO> queryMedicalInsuranceSummary(MedicalInsturanceSummaryDTO summaryDTO);

    List<MibInfoOutData> getSeltInfoSum2(InData inData);
}