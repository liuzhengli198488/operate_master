package com.gys.mapper;

import com.gys.entity.data.commodityInventoryReport.CommoditySummaryInData;
import com.gys.entity.data.commodityInventoryReport.CommoditySummaryOutData;

import java.util.List;

/**
 * @desc:
 * @author: RULAISZ
 * @createTime: 2021/11/26 15:45
 */
public interface GaiaCommodityInventoryHMapper {
    List<CommoditySummaryOutData> summaryList(CommoditySummaryInData inData);
}
