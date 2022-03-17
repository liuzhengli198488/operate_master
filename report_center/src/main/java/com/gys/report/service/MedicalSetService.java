package com.gys.report.service;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.PageInfo;
import com.gys.report.entity.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface MedicalSetService {
    /**
     *  查询医保结算信息
     * @param inData
     * @return
     */
    public PageInfo<MedicalSetOutData> selectMedicalSetList(MedicalSetInData inData);

    /**
     * 查询医保销售信息
     * @param inData
     * @return
     */
    public List<MedicalSalesOutData> selectMedicalSalesList(MedicalSalesInData inData);
    /**
     * 导出医保结算信息
     * @param inData
     */
    public void exportMedicalSetList(MedicalSetInData inData, HttpServletResponse response);

    /**
     * 医保销售对账汇总(对总账)
     * @param inData 入参
     * @return
     */
    List<SelectMedicalSummaryDTO> selectMedicalSummaryList(SelectMedicalSummaryData inData);

    /**
     * 医保销售对账汇总(按照机构(名称))
     * @param inData
     * @return
     */
    List<SelectMedicalSummaryDTO> selectMedicalStoreSummaryList(SelectMedicalSummaryData inData);

    /**
     * 获取险种列表
     * @param userInfo
     * @return
     */
    List<Map<String,String>> selectInSuTypesList(GetLoginOutData userInfo);
}
