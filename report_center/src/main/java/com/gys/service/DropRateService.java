package com.gys.service;

import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.PageInfo;
import com.gys.entity.data.dropdata.*;
import com.gys.entity.data.xhl.dto.*;
import com.gys.entity.data.xhl.vo.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface DropRateService {

    Map<String,Object> init();

    PageInfo reportData(GetLoginOutData loginUser, DropRateInData inData);

    Map<String, BigDecimal> getIndustryAverage(String clientId);

    /**
     * 获取已过账的下货率详情列表
     */
    PageInfo getHistoryPostedUnloadRateDetailList(GetLoginOutData loginUser, GetHistoryPostedUnloadRateDetailResponse getHistoryPostedUnloadRateDetailListForm);

    /**
     * 获取所有客户(加盟商)
     * @param loginUser 登陆人信息
     * @param clientId 加盟商编号
     * @param clientName 加盟商姓名
     * @return
     */
    List<GetClientListDTO> getClientList(GetLoginOutData loginUser,  String clientName);

    /**
     * 获取捕获方式
     * @param loginUser
     * @return
     */
    List<Map<String, String>> getReplenishStyle(GetLoginOutData loginUser);

    /**
     * 下货率 -- 商品大类下拉
     */
    List<PullingBean>  getCommodityCategoryList(GetLoginOutData loginUser);

    boolean  generateHistoryPostedUnloadRate(GetLoginOutData loginUser,GenerateHistoryPostedUnloadRateForm generateHistoryPostedUnloadRateForm);

    List<ViewGetProductPositioningListVo> getProductPositioningList(ViewGetProductPositioningListForm viewGetProductPositioningListForm);

    PageInfo getListDropList(ReportInfoDto dto);

    PageInfo getReportSummary(ReportSummaryDto dto);

    ClientStoreVo getClientsOrStores(ClientStoreDto dto);

    com.github.pagehelper.PageInfo<ClientBaseInfoVo> getClientBaseInfo(QueryDto dto);

    void updateInfo(ClientBaseInfoDto dto);

    List<StoreData> getStores(List<String> dto);

    List<ClientBaseInfoVo> getClientsInfo(QueryDto dto);

    ReportInfoExportVo getTotalReportInfo(List<ReportInfoVo> list);


    ReportInfoVo getListTotal(List<ReportInfoVo> list);
}
