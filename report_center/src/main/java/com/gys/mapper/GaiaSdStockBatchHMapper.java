package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.entity.GaiaSdStockBatchH;
import com.gys.entity.data.InventoryInquiry.EffectiveGoodsInData;
import com.gys.entity.data.InventoryInquiry.EffectiveGoodsOutData;
import com.gys.entity.data.InventoryInquiry.EndingInventoryInData;
import com.gys.entity.data.InventoryInquiry.EndingInventoryOutData;

import java.util.List;

public interface GaiaSdStockBatchHMapper extends BaseMapper<GaiaSdStockBatchH> {
    List<EndingInventoryOutData> selectEndingInventoryByBatch(EndingInventoryInData inData);

    List<EffectiveGoodsOutData> selectEffectiveGoodsByHistory(EffectiveGoodsInData inData);

    List<EffectiveGoodsOutData> selectEffectiveGoodsByCurrent(EffectiveGoodsInData inData);
}