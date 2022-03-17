package com.gys.report.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 医保销售明细
 */
@Data
public class MedicalSalesOutData implements Serializable {

    /**
     * 门店编号
     */
    private String stoCode;

    /**
     * 门店名称
     */
    private String stoName;

    /**
     * 销售单号
     */
    private String billNo;

    /**
     * 费用明细流水号
     */
    private String feedetlSn;

    /**
     * 处方号
     */
    private String rxno;

    /**
     * 外购处方标志
     */
    private String rxCircFlag;

    /**
     * 费用发生时间
     */
    private String feeOcurTime;

    /**
     * 医疗目录编码
     */
    private String medListCodg;

    /**
     * 医药机构目录编码
     */
    private String medinsListCodg;

    /**
     * 明细项目费用总额
     */
    private String detItemFeeSumamt;

    /**
     * 单次剂量描述
     */
    private String sinDosDscr;

    /**
     * 使用频次描述
     */
    private String usedFrquDscr;

    /**
     * 周期天数
     */
    private String prdDays;

    /**
     * 用药途径描述
     */
    private String medcWayDscr;

    /**
     * 开单医生编码
     */
    private String bilgDrCodg;

    /**
     * 开单医生姓名
     */
    private String bilgDrName;

    /**
     * 中药使用方式
     */
    private String tcmdrugUsedWay;

    /**
     * 数量
     */
    private BigDecimal cnt;

    /**
     * 单价
     */
    private BigDecimal pric;

    /**
     * 定价上限金额
     */
    private BigDecimal pricUplmtAmt;

    /**
     * 自付比例
     */
    private BigDecimal selfpayProp ;

    /**
     * 全自费金额
     */
    private BigDecimal fulamtOwnpayAmt ;

    /**
     * 超限价金额
     */
    private BigDecimal overLmtAmt ;

    /**
     * 先行自付金额
     */
    private BigDecimal preselfpayAmt  ;

    /**
     * 符合政策范围金额
     */
    private BigDecimal inscpScpAmt;

    /**
     * 收费项目等级
     */
    private String chrgimtLv ;

    /**
     * 医疗收费项目类别
     */
    private String medChrgitmType ;

    /**
     * 基本药物标志
     */
    private String basMednFlag ;

    /**
     * 医保谈判药品标志
     */
    private String hiNegoDrugFlag ;

    /**
     * 儿童用药标志
     */
    private String chldMedcFlag;

    /**
     * 目录特项标志
     */
    private String listSpItemFlag;

    /**
     * 直报标志
     */
    private String drtReimFalg;

    /**
     * 备注
     */
    private String memo;

}
