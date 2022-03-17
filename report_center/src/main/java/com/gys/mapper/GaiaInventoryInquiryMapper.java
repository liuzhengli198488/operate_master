package com.gys.mapper;


import com.gys.entity.data.InventoryInquiry.InventoryInquiryInData;
import com.gys.report.entity.InventoryInquiryByClientAndBatchNoOutData;
import com.gys.report.entity.InventoryInquiryByClientAndSiteOutData;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface GaiaInventoryInquiryMapper{
    List<LinkedHashMap<String, Object>> inventoryInquiryByRow(InventoryInquiryInData inData);
    Map<String, Object> inventoryInquiryByRowTotal(InventoryInquiryInData inData);
    //按加盟商按地点查询
    List<InventoryInquiryByClientAndSiteOutData> inventoryInquiryListByStoreAndDc(InventoryInquiryInData inData);
    //按商品搜索的时候 没有商品信息的门店也要带出来
    List<InventoryInquiryByClientAndSiteOutData> inventoryInquiryListByAllStoreAndDc(InventoryInquiryInData inData);
    //按批号查询各个地点下的库存
    List<InventoryInquiryByClientAndSiteOutData> inventoryInquiryListByStoreAndDcAndBatchNo(InventoryInquiryInData inData);

    //按加盟商按批号查库存
    List<InventoryInquiryByClientAndBatchNoOutData> inventoryInquiryListByClientAndSiteAndBatchNo(InventoryInquiryInData inData);
    //按加盟商按商品查库存
    List<InventoryInquiryByClientAndBatchNoOutData> inventoryInquiryListByClient(InventoryInquiryInData inData);

    List<InventoryInquiryByClientAndSiteOutData> querySalesInfo(InventoryInquiryInData inData);

    List<InventoryInquiryByClientAndSiteOutData> inventoryInquiryListByAllStoreAndDcNew(InventoryInquiryInData inData);
}
