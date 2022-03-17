package com.gys.report.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class GiftPromOutData implements Serializable {
    private String index;
    private String gssdPmSubjectId;
    private String gssdPmContent;
    private String promActivity;
    private String gssdPmActivityId;
    private String gssdPmActivityName;
    private String gssdPmActivityFlag;
    private String gssdProId;
    private String proName;
    private BigDecimal reachAmt;
    private BigDecimal reachQty;
    private String proUnit;
    private String proSpecs;
    private BigDecimal gssdPrc1;
    private BigDecimal gssdPrc2;
    private BigDecimal price;
    private BigDecimal discount;
    private BigDecimal maxQty;
    private BigDecimal gssdQty;
    private BigDecimal gssbQty;
    private String proFactory;
    private String proCommonName;
    private String movTax;
    private String para1;
    private String para2;
    private String para3;
    private String para4;
    private String exclusion;
    private String proStorageArea;
}
