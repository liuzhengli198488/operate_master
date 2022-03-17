package com.gys.controller.app.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @desc:
 * @author: Ryan
 * @createTime: 2021/8/29 23:38
 */
@Data
public class WeekDropRateVO {
    /**下货率曲线图*/
    private List<BaseRateChart> rateCharts;
    /**下货率表格*/
    private List<BaseRate> rateTables;

    public WeekDropRateVO() {
        this.rateCharts = new ArrayList<>();
        this.rateCharts = new ArrayList<>();
    }
}
