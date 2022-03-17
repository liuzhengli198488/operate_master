package com.gys.entity;

import lombok.Data;

import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @desc:
 * @author: Ryan
 * @createTime: 2021/8/30 23:15
 */
@Table(name = "GAIA_XHL_H")
@Data
public class GaiaXhlH {
    private Long id;
    private String client;
    private Date tjDate;
    //年份
    private String yearName;
    //当年周次：35周 36周等
    private Integer weekNum;
    //统计类型 1-配货 2-发货 3-最终
    private Integer tjType;
    //分子数量：开单数量、过账数量
    private BigDecimal upQuantity;

    //分子数量：开单数量、过账数量(排除铺货)
    private BigDecimal upQuantityLess;

    //分母数量：订单数量、开单数量
    private BigDecimal downQuantity;

    //分母数量：订单数量、开单数量(排除铺货)
    private BigDecimal downQuantityLess;

    //数量满足率
    private BigDecimal quantityRate;

    //数量满足率(排除铺货)
    private BigDecimal quantityRateLess;

    //开单金额、配送金额
    private BigDecimal upAmount;


    //开单金额、配送金额(排除铺货)
    private BigDecimal upAmountLess;

    //订单金额、开单金额
    private BigDecimal downAmount;

    //订单金额、开单金额(排除铺货)
    private BigDecimal downAmountLess;

    //金额满足率
    private BigDecimal amountRate;

    //金额满足率(排除铺货)
    private BigDecimal amountRateLess;

    //开单品项、过账品项
    private BigDecimal upProductNum;


    //开单品项、过账品项(排除铺货)
    private BigDecimal upProductNumLess;

    //订单品项、开单品项
    private BigDecimal downProductNum;

    //订单品项、开单品项(排除铺货)
    private BigDecimal downProductNumLess;

    //品项满足率
    private BigDecimal productRate;

    //品项满足率(排除铺货)
    private BigDecimal productRateLess;

    //行业平均值
    private BigDecimal industryAverage;

    private Integer deleteFlag;
    private Date createTime;
    private Date updateTime;
    private Integer version;
    /**
     * 查看
     */
    private Integer viewSource;
}
