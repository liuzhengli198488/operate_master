package com.gys.report.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Table(name = "GAIA_SD_BATCH_CHANGE")
@EqualsAndHashCode
public class GaiaSdBatchChange implements Serializable {
    private static final long serialVersionUID = 747013593484669787L;

    /**
     * 加盟店
     */
    @Id
    @Column(name = "CLIENT")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String client;

    /**
     * 店号
     */
    @Id
    @Column(name = "GSBC_BR_ID")
    private String gsbcBrId;

    /**
     * 销售单号
     */
    @Id
    @Column(name = "GSBC_VOUCHER_ID")
    private String gsbcVoucherId;

    /**
     * 异动日期
     */
    @Id
    @Column(name = "GSBC_DATE")
    private String gsbcDate;

    /**
     * 行号
     */
    @Id
    @Column(name = "GSBC_SERIAL")
    private String gsbcSerial;

    /**
     * 商品id
     */
    @Id
    @Column(name = "GSBC_PRO_ID")
    private String gsbcProId;

    /**
     * 批次
     */
    @Id
    @Column(name = "GSBC_BATCH")
    private String gsbcBatch;

    /**
     * 批号
     */
    @Column(name = "GSBC_BATCH_NO")
    private String gsbcBatchNo;

    /**
     * 数量
     */
    @Column(name = "GSBC_QTY")
    private BigDecimal gsbcQty;

    /**
     * 原库存数量
     */
    @Column(name = "GSBC_OLD_QTY")
    private BigDecimal gsbcOldQty;

}