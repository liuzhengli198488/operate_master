package com.gys.service;

import com.gys.common.data.PageInfo;
import com.gys.common.response.Result;
import com.gys.entity.data.InventoryInquiry.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

public interface InventoryInquiryService {

    PageInfo inventoryInquiryByRow(InventoryInquiryInData inData);

    List<LinkedHashMap<String,Object>> inventoryInquiryByRowExecl(InventoryInquiryInData inData);

    PageInfo selectEndingInventory(EndingInventoryInData inData);

    List<EndingInventoryOutData> selectEndingInventoryCSV(EndingInventoryInData inData);

    PageInfo selectEffectiveGoods(EffectiveGoodsInData inData);

    PageInfo inventoryInquiryListByStoreAndDc(InventoryInquiryInData inData);
    PageInfo inventoryInquiryListByClient(InventoryInquiryInData inData);

    Result querySalesInfo(InventoryInquiryInData inData);

    void inventoryInquiryListByStoreExport(InventoryInquiryInData inData, HttpServletResponse response) throws IOException;
}
