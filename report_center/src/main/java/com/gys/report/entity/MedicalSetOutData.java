package com.gys.report.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.gys.util.csv.annotation.CsvCell;
import com.gys.util.csv.annotation.CsvRow;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 *  医保结算
 */
@Data
public class MedicalSetOutData implements Serializable {

    @ExcelProperty("门店编号")
    private String stoCode;

    @ExcelProperty("门店名称")
    private String stoName;

    @ExcelProperty("销售单号")
    private String billNo;

    @ExcelProperty("清算经办机构")
    private String clrOptins;

    @ExcelProperty("清算经办机构名称")
    private String clrOptinsName;

    @ExcelProperty("就医地医保区划")
    private String mdtrtareaAdmvs;

    @ExcelProperty("参保地医保区划")
    private String insuplcAdmvs;

    @ExcelProperty("结算ID")
    private String setlId;

    @ExcelProperty("就诊ID")
    private String mdtrtId;

    @ExcelProperty("人员编号")
    private String psnNo;

    @ExcelProperty("人员姓名")
    private String psnName;

    @ExcelProperty("人员证件类型")
    private String psnCertType;

    @ExcelProperty("证件号码")
    private String certno;

    @ExcelProperty("性别")
    private String gend;

    @ExcelProperty("民族")
    private String naty;

    @ExcelProperty("出生日期")
    private String brdy;

    @ExcelProperty("年龄")
    private String age;

    @ExcelProperty("险种类别")
    private String insutype;

    @ExcelProperty("人员类别")
    private String psnType;

    @ExcelProperty("公务员标志")
    private String cvlservFlag;

    @ExcelProperty("结算时间")
    private String setlTime;

    @ExcelProperty("就诊凭证类别")
    private String mdtrtCertType;

    @ExcelProperty("医疗类别")
    private String medType;

    @ExcelProperty("医疗费总额")
    private BigDecimal medfeeSumant;

    @ExcelProperty("全自费金额")
    private BigDecimal fulamtOwnpayAmt;

    @ExcelProperty("超限价自费费用")
    private BigDecimal overlmtSelfpay;

    @ExcelProperty("先行自付金额")
    private BigDecimal preselfpayAmt;

    @ExcelProperty("符合政策范围金额")
    private BigDecimal inscpScpAmt;

    @ExcelProperty("实际支付起付线")
    private BigDecimal actPayDedc;

    @ExcelProperty("基本医疗保险统筹基金支出")
    private BigDecimal hifpPay;

    @ExcelProperty("基本医疗保险统筹基金支付比例")
    private String poolPropSelfpay;

    @ExcelProperty("公务员医疗补助资金支出")
    private BigDecimal cvlservPay;

    @ExcelProperty("企业补充医疗保险基金支出")
    private BigDecimal hifesPay;

    @ExcelProperty("居民大病保险资金支出")
    private BigDecimal hifmiPay;

    @ExcelProperty("职工大额医疗费用补助基金支出")
    private BigDecimal hifobPay;

    @ExcelProperty("医疗救助基金支出")
    private BigDecimal mafPay;

    @ExcelProperty("其他支出")
    private BigDecimal othPay;

    @ExcelProperty("基金支付总额")
    private BigDecimal fundPaySumamt;

    @ExcelProperty("个人负担总金额")
    private BigDecimal psnPartAmt;

    @ExcelProperty("个人账户支出")
    private BigDecimal acctPay;

    @ExcelProperty("个人现金支付")
    private BigDecimal psnCashPayamt;

    @ExcelProperty("余额")
    private BigDecimal balc;

    @ExcelProperty("个人账户共济支付金额")
    private BigDecimal acctMulaidPay;

    @ExcelProperty("医药机构结算ID")
    private String medinsSetlId;



    @ExcelProperty("清算方式")
    private String clrWay;

    @ExcelProperty("清算类别")
    private String clrType;

}
