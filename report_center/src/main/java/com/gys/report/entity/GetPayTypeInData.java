package com.gys.report.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GetPayTypeInData {
    private String clientId;
    private String gspmBrId;
    private String gspmId;
    private String gspmName;
    private String gspmType;
    private BigDecimal payAmount;
    private String gspmRecharge;
    private String gspmFiId;
    private String gspmRemark;
    private String gspmRemark1;
}
