package com.gys.entity.data.suggestion.vo;

import lombok.Data;

/**
 * @Auther: tzh
 * @Date: 2022/1/24 17:51
 * @Description: CalendarVo
 * @Version 1.0.0
 */
@Data
public class CalendarVo {
    /**
     * 时间 8位
     */
    private String  gcdDate;

    private String gcdYear;
    /**
     * 时间 月份
     */
    private String   gcdMonth;

    /**
     * 时间 周
     */
    private String   gcdWeek;
}
