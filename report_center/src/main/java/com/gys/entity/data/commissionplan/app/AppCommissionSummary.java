package com.gys.entity.data.commissionplan.app;

import com.gys.entity.data.commissionplan.StoreCommissionSummary;
import com.gys.util.BigDecimalUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author wu mao yin
 * @Title: app个人销售提成统计
 * @date 2022/1/1710:42
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AppCommissionSummary extends StoreCommissionSummary {

    @ApiModelProperty("员工id")
    private String userId;

    @ApiModelProperty("时间维度")
    private String timeType;

    @ApiModelProperty("单号")
    private String billNo;

    @ApiModelProperty("交易次数")
    private Integer payCount;

    @ApiModelProperty("客单价")
    private BigDecimal customerPrice;

    public AppCommissionSummary summary(AppCommissionSummary appCommissionSummary) {
        this.payCount =
                Optional.ofNullable(this.payCount).orElse(0) + Optional.ofNullable(appCommissionSummary.getPayCount()).orElse(0);
        this.setAmt(BigDecimalUtil.add(this.getAmt(), appCommissionSummary.getAmt()));
        this.setGrossProfitAmt(BigDecimalUtil.add(this.getGrossProfitAmt(), appCommissionSummary.getGrossProfitAmt()));
        this.setGrossProfitRate(BigDecimalUtil.add(this.getGrossProfitRate(), appCommissionSummary.getGrossProfitRate()));
        return this;
    }

}
