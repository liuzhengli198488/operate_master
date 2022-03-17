package com.gys.common.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wangyifan
 */
@Data    //0 上升  1 下降
public class EmployeesData {


    @ApiModelProperty(value = "销售天数")
    private int daysSales = 0;

    @ApiModelProperty(value = "数据对象")
    private Employees employeesData;

    @Data
    public static class Employees {

        @ApiModelProperty(value = "实收金额")
        private String actualAmount = "0.00";

        @ApiModelProperty(value = "实收金额上升下降")
        private Integer actualAmountRanking;

        @ApiModelProperty(value = "毛利额")
        private String grossProfitAmount = "0.00";

        @ApiModelProperty(value = "毛利额上升下降")
        private Integer grossProfitAmountRanking;

        @ApiModelProperty(value = "毛利率")
        private String grossMarginRate = "0.00";

        @ApiModelProperty(value = "毛利率上升下降")
        private Integer grossMarginRateRanking;


        @ApiModelProperty(value = "交易次数")
        private int tradeCount = 0;

        @ApiModelProperty(value = "交易次数上升下降")
        private Integer tradeCountRanking;

        @ApiModelProperty(value = "客单价")
        private String customerPrice = "0.00";

        @ApiModelProperty(value = "客单价上升下降")
        private Integer customerPriceRanking;

        @ApiModelProperty(value = "会员卡")
        private int memberCard = 0;

        @ApiModelProperty(value = "会员卡上升下降")
        private Integer memberCardRanking;


        @ApiModelProperty(value = "提成合计")
        private String commissionTotals = "0.00";

        @ApiModelProperty(value = "提成合计上升下降")
        private Integer commissionTotalsRanking;

        @ApiModelProperty(value = "销售提成")
        private String salesCommissions = "0.00";

        @ApiModelProperty(value = "销售提成上升下降")
        private Integer salesCommissionsRanking;

        @ApiModelProperty(value = "单品提成")
        private String itemFees = "0.00";

        @ApiModelProperty(value = "单品提成上升下降")
        private Integer itemFeesRanking;

        @ApiModelProperty(value = "成本金额")
        private String costingAmt = "0.00";

        @ApiModelProperty(value = "日期")
        private String dateTime;
    }

}
