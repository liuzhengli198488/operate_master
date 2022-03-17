package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.report.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface GaiaSdPhysicalCountDiffMapper extends BaseMapper<GaiaSdPhysicalCountDiff> {

    /**
     * 查询所有的
     * @param inData
     * @return
     */
    List<DifferenceResultDetailedQueryOutData> getDifferenceResultDetailedQuery(DifferenceResultQueryInVo inData);

    //盘点数据差异
    List<DifferenceResultDetailedQueryOutData> getGoodsNumber(DifferenceResultQueryInVo inData);

    GaiaDcData getDcData(@Param("client") String client, @Param("brId") String brId);

    /**
     * 加盟商下的 仓库查询
     * @param inData
     * @return
     */
    List<DifferenceResultDetailedQueryOutData> getDifferenceResultDetailedQueryByClientBrCode(DifferenceResultQueryInVo inData);

    /**
     * 查询所有
     * @param inData
     * @return
     */
    List<DifferenceResultQueryOutData> getDifferenceResultQueryByAll(DifferenceResultQueryInVo inData);

    /**
     * 根据门店盘点单据查询
     * @param inData
     * @return
     */
    List<InventoryDocumentsOutData> inventoryDocumentQueryByBrId(DifferenceResultQueryInVo inData);

}
