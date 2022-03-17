package com.gys.report.entity;

import lombok.Data;

import java.util.List;

/**
 * @Author ：gyx
 * @Date ：Created in 16:27 2021/10/21
 * @Description：进销存库存检查报表入参
 * @Modified By：gyx
 * @Version:
 */
@Data
public class InventoryChangeCheckInData {

    private String client;

    private List<String> siteCodeList;

    private String startDate;

    private String endDate;

    private String currentDate;

    //是否含税
    private String isTax;

    private String siteCode;

    private String qcDate;

    private String gssgId;

    private List<String> gssgIds;

    private String stoGssgType;

    private List<GaiaStoreCategoryType> stoGssgTypes;

    private String stoAttribute;

    private List<String> stoAttributes;

    private String stoIfMedical;

    private List<String> stoIfMedicals;

    private String stoTaxClass;

    private List<String> stoTaxClasss;

    private String stoIfDtp;

    private List<String> stoIfDtps;

}
