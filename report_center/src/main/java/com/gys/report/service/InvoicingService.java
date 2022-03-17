package com.gys.report.service;


import com.gys.report.entity.InvoicingInData;
import com.gys.report.entity.InvoicingOutDataObj;

import javax.servlet.http.HttpServletResponse;

public interface InvoicingService {
    InvoicingOutDataObj getInvoicingList(InvoicingInData inData);


    /**
     * 进销存明细导出
     * @param inData
     * @param response
     * @return
     */
    void invoicingExport(InvoicingInData inData, HttpServletResponse response);
}
