package com.gys.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * GAIA_SO_ITEM
 * @author 
 */
@Data
public class GaiaSoItem extends GaiaSoItemKey implements Serializable {
    /**
     * 商品编码
     */
    private String soProCode;

    /**
     * 销售订单数量
     */
    private BigDecimal soQty;

    /**
     * 订单单位
     */
    private String soUnit;

    /**
     * 销售订单单价
     */
    private BigDecimal soPrice;

    /**
     * 订单行金额
     */
    private BigDecimal soLineAmt;

    /**
     * 地点
     */
    private String soSiteCode;

    /**
     * 库存地点
     */
    private String soLocationCode;

    /**
     * 批次
     */
    private String soBatch;

    /**
     * 税率
     */
    private String soRate;

    /**
     * 计划发货日期
     */
    private String soDeliveryDate;

    /**
     * 订单行备注
     */
    private String soLineRemark;

    /**
     * 删除标记
     */
    private String soLineDelete;

    /**
     * 交货已完成标记
     */
    private String soCompleteFlag;

    /**
     * 已交货数量
     */
    private BigDecimal soDeliveredQty;

    /**
     * 已交货金额
     */
    private BigDecimal soDeliveredAmt;

    /**
     * 已开票数量
     */
    private BigDecimal soInvoiceQty;

    /**
     * 已开票金额
     */
    private BigDecimal soInvoiceAmt;

    /**
     * 退货参考原单号
     */
    private String soReferOrder;

    /**
     * 退货参考原单行号
     */
    private String soReferOrderLineno;

    /**
     * 退库原因
     */
    private String soReReason;

    /**
     * 生产批号
     */
    private String soBatchNo;

    /**
     * 开单数量
     */
    private BigDecimal soDnQty;

    /**
     * 货位号
     */
    private String soHwh;

    private static final long serialVersionUID = 1L;
}