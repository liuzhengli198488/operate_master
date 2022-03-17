package com.gys.entity;

import lombok.Data;

/**
 * 医保销售数据
 * @author 胡鑫鑫
 */
@Data
public class MibInfoOutData {
    private String billNo;//         就诊ID
    private String gsshDate;//        销售时间
    private String mdtrtId;//         就诊ID
    private String msgId;//           报文id
    private String setlId;//          结算id
    private String psnNo;//           人员编号
    private String psnName;//           人员编号
    private String insutype;//        险种类别
    private String insuName;//        险种名称
    private String clrType;//         清算类别
    private String clrName;//         清算名称
    private String medfeeSumamt;//    医疗费总额
    private String fundPaySumamt;//   基金支付总额
    private String acctPay;//         个人账户支付金额
    private String cashPayamt;//      现金支付金额
    private String mdtrtCertType;//   就诊凭证类型
    private String mdtrtCertNo;//     就诊凭证编号
    private String invono;//          invono
    private String diseCodg;//        病种编码
    private String diseName;//        病种名称
    private String acctUsedFlag;//    个人账户使用标志
    private String medType;//         医疗类别
    private String omsgId;//          omsgId
    private String oinfNo;//          原交易编号
    private String righting;//        righting
    private String fixmedinsSetlCnt;//定点医药机构结算笔数
    private String refdSetlFlag;//    退费结算标志
    private String state;//交易状态
    private String clrOptins;// 清算地点
    private String clrOptinsName;// 清算地点名称
    private String czFlag;// 冲正状态
    private String czDate;// 冲正时间
    private String czUser;// 冲正人员
    private String failMsg;// 异常信息
}
