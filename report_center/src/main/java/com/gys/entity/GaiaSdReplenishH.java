package com.gys.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * GAIA_SD_REPLENISH_H
 * @author 
 */
@Data
public class GaiaSdReplenishH extends GaiaSdReplenishHKey implements Serializable {
    /**
     * 补货日期（创建日期）
     */
    private String gsrhDate;

    /**
     * 补货类型(1自采, 2总部配送, 3委托配送, 4内部批发)
     */
    private String gsrhType;

    /**
     * 补货地点
     */
    private String gsrhAddr;

    /**
     * 补货金额
     */
    private BigDecimal gsrhTotalAmt;

    /**
     * 补货数量
     */
    private BigDecimal gsrhTotalQty;

    /**
     * 补货人员
     */
    private String gsrhEmp;

    /**
     * 补货状态是否审核
     */
    private String gsrhStatus;

    /**
     * 是否转采购订单
     */
    private String gsrhFlag;

    /**
     * 采购单号
     */
    private String gsrhPoid;

    /**
     * 补货方式 0正常补货，1紧急补货,2铺货,3互调,4直配
     */
    private String gsrhPattern;

    /**
     * 开单人
     */
    private String gsrhDnBy;

    /**
     * 开单日期
     */
    private String gsrhDnDate;

    /**
     * 开单时间
     */
    private String gsrhDnTime;

    /**
     * 开单标记 0未开单，1已开单
     */
    private String gsrhDnFlag;

    /**
     * 比价获取状态 0获取中, 1待获取，2已获取
     */
    private String gsrhGetStatus;

    /**
     * 配送单号
     */
    private String gsrhDnId;

    /**
     * 审核时间
     */
    private String gsrhTime;

    /**
     * 特殊商品单 0 否 1 是
     */
    private String gsrhSpecial;

    /**
     * 创建时间
     */
    private String gsrhCreTime;

    /**
     * 审核日期
     */
    private String gsrhCheckDate;

    /**
     * 补货来源 1门店缺断货补充，2门店品类补充，3二次开单，4-必备品满足度铺货
     */
    private String gsrhSource;

    private static final long serialVersionUID = 1L;
}