package com.gys.entity;

import lombok.Data;

@Data
public class ProductSaleAmt {
    private String clientId;
    private String brId;
    private String brName;
    private String billNo;
    private String proCode;
    private String saleQty;
    private String saleAmt;
    private String proPrice;
    private String salerId;
    private String tcAmt;

    // 销售毛利
    private String saleGrossProfit;
}
