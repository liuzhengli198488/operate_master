package com.gys.entity;

import lombok.Data;


@Data
public class ImportProductKuCunInfo{


    private String client;


    private String site;


    private String siteName;


    private String proCode;

    /**
     * 国际条形码1 PRO_BARCODE
     */
    private String proBarcode;

    /**
     * 商品名 PRO_NAME
     */
    private String proName;

    private String kuCunNum;

}
