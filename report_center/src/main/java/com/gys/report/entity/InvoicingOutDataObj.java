package com.gys.report.entity;

import lombok.Data;

import java.util.List;

@Data
public class InvoicingOutDataObj {
    private List<InvoicingOutData> resultList;

    private String totalDec;
}
