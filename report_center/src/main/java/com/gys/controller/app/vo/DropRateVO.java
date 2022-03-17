package com.gys.controller.app.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @desc:
 * @author: Ryan
 * @createTime: 2021/8/29 18:46
 */
@Data
public class DropRateVO {
    /**统计日期：2021年08月15日*/
    private String date;
    /**统计星期：星期五*/
    private String week;
    /**下货率列表*/
    private List<BaseRateInfo> rateInfoList;

    @Data
    public static class BaseRateInfo {
        /**类型：1-开单配货率 2-仓库发货率 3-最终下货率*/
        private Integer rateType;
        /**日期：20210805*/
        private String rateDate;
        /**数量满足率*/
        private BigDecimal quantityRate;
        /**金额满足率*/
        private BigDecimal amountRate;
        /**品项满足率*/
        private BigDecimal productRate;
    }

}
