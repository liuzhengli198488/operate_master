package com.gys.service;

import com.gys.common.data.PageInfo;
import com.gys.entity.data.consignment.dto.RecommendedDocumentsDto;
import com.gys.entity.data.consignment.dto.StoreDto;
import com.gys.entity.data.consignment.dto.StoreRecommendedSaleDto;
import com.gys.entity.data.consignment.vo.StoreReport;
import com.gys.entity.data.consignment.vo.StoreVoList;

import javax.servlet.http.HttpServletResponse;

/**
 * @Auther: tzh
 * @Date: 2021/12/7 10:36
 * @Description: ConsignmentService
 * @Version 1.0.0
 */
public interface ConsignmentService {
    PageInfo getStoreRecommendReport(StoreRecommendedSaleDto dto);

    PageInfo getStoreUnRecommendReport(StoreRecommendedSaleDto dto);

    void exportStoreReport(StoreRecommendedSaleDto dto, HttpServletResponse response);

    StoreVoList getAllStores(StoreDto dto);

    PageInfo getRecommendedDocuments(RecommendedDocumentsDto data);
}
