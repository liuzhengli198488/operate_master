package com.gys.report.service;

import com.gys.report.entity.DifferenceResultDetailedQueryOutData;
import com.gys.report.entity.DifferenceResultQueryInVo;
import com.gys.report.entity.DifferenceResultQueryOutData;
import com.gys.report.entity.InventoryDocumentsOutData;

import java.util.List;

/**
 * @author xiaoyuan
 */
public interface InventoryReportService {


    /**
     * 盘点差异结果查询
     * @param inData
     * @return
     */
    List<DifferenceResultQueryOutData> getDifferenceResultQuery(DifferenceResultQueryInVo inData);

    /**
     * 盘点差异结果明细查询
     * @param inData
     * @return
     */
    List<DifferenceResultDetailedQueryOutData> getDifferenceResultDetailedQuery(DifferenceResultQueryInVo inData);

    /**
     * 门店盘点单据查询
     * @param inData
     * @return
     */
    List<InventoryDocumentsOutData> inventoryDocumentQuery(DifferenceResultQueryInVo inData);

    /**
     * 盘点数据差异（门店报表）
     * @param inData
     * @return
     */
    List<DifferenceResultDetailedQueryOutData> getInventoryDataDetail(DifferenceResultQueryInVo inData);
}
