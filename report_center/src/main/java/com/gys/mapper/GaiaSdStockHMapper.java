package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.entity.GaiaSdStockH;
import com.gys.entity.data.InventoryInquiry.EndingInventoryInData;
import com.gys.entity.data.InventoryInquiry.EndingInventoryOutData;

import java.util.List;

public interface GaiaSdStockHMapper extends BaseMapper<GaiaSdStockH> {
    List<EndingInventoryOutData> selectEndingInventoryByPro(EndingInventoryInData inData);
}