package com.gys.service;

import com.gys.common.data.PageInfo;
import com.gys.entity.SupplierSaleMan;
import com.gys.report.entity.SupplierSummaryInData;

import java.util.List;

public interface SupplierSummaryService {
    List<SupplierSaleMan> getSaleManList(String client ,String supplierCode);


    PageInfo getSupplierSummaryList(SupplierSummaryInData summaryData);
}
