package com.gys.entity.data.productMatch;

import lombok.Data;

import java.util.Date;

@Data
public class ProductMatchZ {
    private String client;
    private String matchCode;
    private String matchBatch;
    private String stoCode;
    private String matchCreater;
    private String matchCreateDate;
    private String matchType;
    private String matchUpdateDate;
    private String matchUpdater;
    private String matchDataCheckTouchTel;
    private String matchDataCheckFlag;
    private Date matchDataCheckTime;
}
