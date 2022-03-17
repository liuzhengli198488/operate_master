package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.entity.GaiaProductBusiness;
import com.gys.entity.data.approval.dto.ApprovalDto;
import com.gys.entity.data.approval.vo.ApprovalVo;
import com.gys.entity.wk.dto.GetProductThirdlyInData;
import com.gys.entity.wk.vo.GetProductThirdlyOutData;
import com.gys.report.entity.ProductTssxOutData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GaiaProductBusinessMapper extends BaseMapper<GaiaProductBusiness> {
    void batchInsertProduct(List<GaiaProductBusiness> list);
    List<ApprovalVo> getApprovalVoList(ApprovalDto dto);

    List<ApprovalVo> getApprovalVoListByProCodes(ApprovalDto list);

    List<GetProductThirdlyOutData> queryProFourthlyByStore(GetProductThirdlyInData inData);

    List<GetProductThirdlyOutData> queryProFourthlyByClinet(GetProductThirdlyInData inData);

    List<ProductTssxOutData> getProTssxByClient(@Param("client") String client);

    List<GaiaProductBusiness> getTestList();
}