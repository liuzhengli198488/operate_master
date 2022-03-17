package com.gys.entity;

import lombok.Data;

import java.io.Serializable;
import javax.persistence.*;

@Data
@Table(name = "MIB_SETL_INFO")
public class MibSetlInfo implements Serializable {
    /**
     * 加盟商
     */
    @Id
    @Column(name = "CLIENT")
    private String client;

    /**
     * 门店
     */
    @Id
    @Column(name = "BR_ID")
    private String brId;

    @Id
    @Column(name = "BILL_NO")
    private String billNo;

    @Id
    @Column(name = "MSG_ID")
    private String msgId;

    @Column(name = "SETL_ID")
    private String setlId;

    @Column(name = "MDTRT_ID")
    private String mdtrtId;

    @Column(name = "PSN_NO")
    private String psnNo;

    @Column(name = "PSN_NAME")
    private String psnName;

    @Column(name = "PSN_CERT_TYPE")
    private String psnCertType;

    @Column(name = "CERTNO")
    private String certno;

    @Column(name = "GEND")
    private String gend;

    @Column(name = "NATY")
    private String naty;

    @Column(name = "BRDY")
    private String brdy;

    @Column(name = "AGE")
    private String age;

    @Column(name = "INSUTYPE")
    private String insutype;

    @Column(name = "PSN_TYPE")
    private String psnType;

    @Column(name = "CVLSERV_FLAG")
    private String cvlservFlag;

    @Column(name = "SETL_TIME")
    private String setlTime;

    @Column(name = "MDTRT_CERT_TYPE")
    private String mdtrtCertType;

    @Column(name = "MED_TYPE")
    private String medType;

    @Column(name = "MEDFEE_SUMAMT")
    private String medfeeSumamt;

    @Column(name = "FULAMT_OWNPAY_AMT")
    private String fulamtOwnpayAmt;

    @Column(name = "OVERLMT_SELFPAY")
    private String overlmtSelfpay;

    @Column(name = "PRESELFPAY_AMT")
    private String preselfpayAmt;

    @Column(name = "INSCP_SCP_AMT")
    private String inscpScpAmt;

    @Column(name = "ACT_PAY_DEDC")
    private String actPayDedc;

    @Column(name = "HIFP_PAY")
    private String hifpPay;

    @Column(name = "POOL_PROP_SELFPAY")
    private String poolPropSelfpay;

    @Column(name = "CVLSERV_PAY")
    private String cvlservPay;

    @Column(name = "HIFES_PAY")
    private String hifesPay;

    @Column(name = "HIFMI_PAY")
    private String hifmiPay;

    @Column(name = "HIFOB_PAY")
    private String hifobPay;

    @Column(name = "MAF_PAY")
    private String mafPay;

    @Column(name = "OTH_PAY")
    private String othPay;

    @Column(name = "FUND_PAY_SUMAMT")
    private String fundPaySumamt;

    @Column(name = "PSN_PART_AMT")
    private String psnPartAmt;

    @Column(name = "ACCT_PAY")
    private String acctPay;

    @Column(name = "PSN_CASH_PAY")
    private String psnCashPay;

    @Column(name = "CASH_PAYAMT")
    private String cashPayamt;

    @Column(name = "BALC")
    private String balc;

    @Column(name = "ACCT_MULAID_PAY")
    private String acctMulaidPay;

    @Column(name = "MEDINS_SETL_ID")
    private String medinsSetlId;

    @Column(name = "CLR_OPTINS")
    private String clrOptins;

    @Column(name = "CLR_WAY")
    private String clrWay;

    @Column(name = "CLR_TYPE")
    private String clrType;

    @Column(name = "PSN_PAY")
    private String psnPay;

    @Column(name = "STATE")
    private String state;

    @Column(name = "CZ_FLAG")
    private String czFlag;

    @Column(name = "CZ_DATE")
    private String czDate;

    @Column(name = "EMP_NAME")
    private String empName;

    @Column(name = "SETL_ID_RETURN")
    private String setlIdReturn;

    @Column(name = "OINF_NO")
    private String oinfNo;
    private static final long serialVersionUID = 1L;

    public String getOinfNo() {
        return oinfNo;
    }

    public void setOinfNo(String oinfNo) {
        this.oinfNo = oinfNo;
    }

    /**
     * 获取加盟商
     *
     * @return CLIENT - 加盟商
     */
    public String getClient() {
        return client;
    }

    /**
     * 设置加盟商
     *
     * @param client 加盟商
     */
    public void setClient(String client) {
        this.client = client;
    }

    /**
     * 获取门店
     *
     * @return BR_ID - 门店
     */
    public String getBrId() {
        return brId;
    }

    /**
     * 设置门店
     *
     * @param brId 门店
     */
    public void setBrId(String brId) {
        this.brId = brId;
    }

    /**
     * @return BILL_NO
     */
    public String getBillNo() {
        return billNo;
    }

    /**
     * @param billNo
     */
    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    /**
     * @return MSG_ID
     */
    public String getMsgId() {
        return msgId;
    }

    /**
     * @param msgId
     */
    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    /**
     * @return SETL_ID
     */
    public String getSetlId() {
        return setlId;
    }

    /**
     * @param setlId
     */
    public void setSetlId(String setlId) {
        this.setlId = setlId;
    }

    /**
     * @return MDTRT_ID
     */
    public String getMdtrtId() {
        return mdtrtId;
    }

    /**
     * @param mdtrtId
     */
    public void setMdtrtId(String mdtrtId) {
        this.mdtrtId = mdtrtId;
    }

    /**
     * @return PSN_NO
     */
    public String getPsnNo() {
        return psnNo;
    }

    /**
     * @param psnNo
     */
    public void setPsnNo(String psnNo) {
        this.psnNo = psnNo;
    }

    /**
     * @return PSN_NAME
     */
    public String getPsnName() {
        return psnName;
    }

    /**
     * @param psnName
     */
    public void setPsnName(String psnName) {
        this.psnName = psnName;
    }

    /**
     * @return PSN_CERT_TYPE
     */
    public String getPsnCertType() {
        return psnCertType;
    }

    /**
     * @param psnCertType
     */
    public void setPsnCertType(String psnCertType) {
        this.psnCertType = psnCertType;
    }

    /**
     * @return CERTNO
     */
    public String getCertno() {
        return certno;
    }

    /**
     * @param certno
     */
    public void setCertno(String certno) {
        this.certno = certno;
    }

    /**
     * @return GEND
     */
    public String getGend() {
        return gend;
    }

    /**
     * @param gend
     */
    public void setGend(String gend) {
        this.gend = gend;
    }

    /**
     * @return NATY
     */
    public String getNaty() {
        return naty;
    }

    /**
     * @param naty
     */
    public void setNaty(String naty) {
        this.naty = naty;
    }

    /**
     * @return BRDY
     */
    public String getBrdy() {
        return brdy;
    }

    /**
     * @param brdy
     */
    public void setBrdy(String brdy) {
        this.brdy = brdy;
    }

    /**
     * @return AGE
     */
    public String getAge() {
        return age;
    }

    /**
     * @param age
     */
    public void setAge(String age) {
        this.age = age;
    }

    /**
     * @return INSUTYPE
     */
    public String getInsutype() {
        return insutype;
    }

    /**
     * @param insutype
     */
    public void setInsutype(String insutype) {
        this.insutype = insutype;
    }

    /**
     * @return PSN_TYPE
     */
    public String getPsnType() {
        return psnType;
    }

    /**
     * @param psnType
     */
    public void setPsnType(String psnType) {
        this.psnType = psnType;
    }

    /**
     * @return CVLSERV_FLAG
     */
    public String getCvlservFlag() {
        return cvlservFlag;
    }

    /**
     * @param cvlservFlag
     */
    public void setCvlservFlag(String cvlservFlag) {
        this.cvlservFlag = cvlservFlag;
    }

    /**
     * @return SETL_TIME
     */
    public String getSetlTime() {
        return setlTime;
    }

    /**
     * @param setlTime
     */
    public void setSetlTime(String setlTime) {
        this.setlTime = setlTime;
    }

    /**
     * @return MDTRT_CERT_TYPE
     */
    public String getMdtrtCertType() {
        return mdtrtCertType;
    }

    /**
     * @param mdtrtCertType
     */
    public void setMdtrtCertType(String mdtrtCertType) {
        this.mdtrtCertType = mdtrtCertType;
    }

    /**
     * @return MED_TYPE
     */
    public String getMedType() {
        return medType;
    }

    /**
     * @param medType
     */
    public void setMedType(String medType) {
        this.medType = medType;
    }

    /**
     * @return MEDFEE_SUMAMT
     */
    public String getMedfeeSumamt() {
        return medfeeSumamt;
    }

    /**
     * @param medfeeSumamt
     */
    public void setMedfeeSumamt(String medfeeSumamt) {
        this.medfeeSumamt = medfeeSumamt;
    }

    /**
     * @return FULAMT_OWNPAY_AMT
     */
    public String getFulamtOwnpayAmt() {
        return fulamtOwnpayAmt;
    }

    /**
     * @param fulamtOwnpayAmt
     */
    public void setFulamtOwnpayAmt(String fulamtOwnpayAmt) {
        this.fulamtOwnpayAmt = fulamtOwnpayAmt;
    }

    /**
     * @return OVERLMT_SELFPAY
     */
    public String getOverlmtSelfpay() {
        return overlmtSelfpay;
    }

    /**
     * @param overlmtSelfpay
     */
    public void setOverlmtSelfpay(String overlmtSelfpay) {
        this.overlmtSelfpay = overlmtSelfpay;
    }

    /**
     * @return PRESELFPAY_AMT
     */
    public String getPreselfpayAmt() {
        return preselfpayAmt;
    }

    /**
     * @param preselfpayAmt
     */
    public void setPreselfpayAmt(String preselfpayAmt) {
        this.preselfpayAmt = preselfpayAmt;
    }

    /**
     * @return INSCP_SCP_AMT
     */
    public String getInscpScpAmt() {
        return inscpScpAmt;
    }

    /**
     * @param inscpScpAmt
     */
    public void setInscpScpAmt(String inscpScpAmt) {
        this.inscpScpAmt = inscpScpAmt;
    }

    /**
     * @return ACT_PAY_DEDC
     */
    public String getActPayDedc() {
        return actPayDedc;
    }

    /**
     * @param actPayDedc
     */
    public void setActPayDedc(String actPayDedc) {
        this.actPayDedc = actPayDedc;
    }

    /**
     * @return HIFP_PAY
     */
    public String getHifpPay() {
        return hifpPay;
    }

    /**
     * @param hifpPay
     */
    public void setHifpPay(String hifpPay) {
        this.hifpPay = hifpPay;
    }

    /**
     * @return POOL_PROP_SELFPAY
     */
    public String getPoolPropSelfpay() {
        return poolPropSelfpay;
    }

    /**
     * @param poolPropSelfpay
     */
    public void setPoolPropSelfpay(String poolPropSelfpay) {
        this.poolPropSelfpay = poolPropSelfpay;
    }

    /**
     * @return CVLSERV_PAY
     */
    public String getCvlservPay() {
        return cvlservPay;
    }

    /**
     * @param cvlservPay
     */
    public void setCvlservPay(String cvlservPay) {
        this.cvlservPay = cvlservPay;
    }

    /**
     * @return HIFES_PAY
     */
    public String getHifesPay() {
        return hifesPay;
    }

    /**
     * @param hifesPay
     */
    public void setHifesPay(String hifesPay) {
        this.hifesPay = hifesPay;
    }

    /**
     * @return HIFMI_PAY
     */
    public String getHifmiPay() {
        return hifmiPay;
    }

    /**
     * @param hifmiPay
     */
    public void setHifmiPay(String hifmiPay) {
        this.hifmiPay = hifmiPay;
    }

    /**
     * @return HIFOB_PAY
     */
    public String getHifobPay() {
        return hifobPay;
    }

    /**
     * @param hifobPay
     */
    public void setHifobPay(String hifobPay) {
        this.hifobPay = hifobPay;
    }

    /**
     * @return MAF_PAY
     */
    public String getMafPay() {
        return mafPay;
    }

    /**
     * @param mafPay
     */
    public void setMafPay(String mafPay) {
        this.mafPay = mafPay;
    }

    /**
     * @return OTH_PAY
     */
    public String getOthPay() {
        return othPay;
    }

    /**
     * @param othPay
     */
    public void setOthPay(String othPay) {
        this.othPay = othPay;
    }

    /**
     * @return FUND_PAY_SUMAMT
     */
    public String getFundPaySumamt() {
        return fundPaySumamt;
    }

    /**
     * @param fundPaySumamt
     */
    public void setFundPaySumamt(String fundPaySumamt) {
        this.fundPaySumamt = fundPaySumamt;
    }

    /**
     * @return PSN_PART_AMT
     */
    public String getPsnPartAmt() {
        return psnPartAmt;
    }

    /**
     * @param psnPartAmt
     */
    public void setPsnPartAmt(String psnPartAmt) {
        this.psnPartAmt = psnPartAmt;
    }

    /**
     * @return ACCT_PAY
     */
    public String getAcctPay() {
        return acctPay;
    }

    /**
     * @param acctPay
     */
    public void setAcctPay(String acctPay) {
        this.acctPay = acctPay;
    }

    /**
     * @return PSN_CASH_PAY
     */
    public String getPsnCashPay() {
        return psnCashPay;
    }

    /**
     * @param psnCashPay
     */
    public void setPsnCashPay(String psnCashPay) {
        this.psnCashPay = psnCashPay;
    }

    /**
     * @return CASH_PAYAMT
     */
    public String getCashPayamt() {
        return cashPayamt;
    }

    /**
     * @param cashPayamt
     */
    public void setCashPayamt(String cashPayamt) {
        this.cashPayamt = cashPayamt;
    }

    /**
     * @return BALC
     */
    public String getBalc() {
        return balc;
    }

    /**
     * @param balc
     */
    public void setBalc(String balc) {
        this.balc = balc;
    }

    /**
     * @return ACCT_MULAID_PAY
     */
    public String getAcctMulaidPay() {
        return acctMulaidPay;
    }

    /**
     * @param acctMulaidPay
     */
    public void setAcctMulaidPay(String acctMulaidPay) {
        this.acctMulaidPay = acctMulaidPay;
    }

    /**
     * @return MEDINS_SETL_ID
     */
    public String getMedinsSetlId() {
        return medinsSetlId;
    }

    /**
     * @param medinsSetlId
     */
    public void setMedinsSetlId(String medinsSetlId) {
        this.medinsSetlId = medinsSetlId;
    }

    /**
     * @return CLR_OPTINS
     */
    public String getClrOptins() {
        return clrOptins;
    }

    /**
     * @param clrOptins
     */
    public void setClrOptins(String clrOptins) {
        this.clrOptins = clrOptins;
    }

    /**
     * @return CLR_WAY
     */
    public String getClrWay() {
        return clrWay;
    }

    /**
     * @param clrWay
     */
    public void setClrWay(String clrWay) {
        this.clrWay = clrWay;
    }

    /**
     * @return CLR_TYPE
     */
    public String getClrType() {
        return clrType;
    }

    /**
     * @param clrType
     */
    public void setClrType(String clrType) {
        this.clrType = clrType;
    }

    /**
     * @return PSN_PAY
     */
    public String getPsnPay() {
        return psnPay;
    }

    /**
     * @param psnPay
     */
    public void setPsnPay(String psnPay) {
        this.psnPay = psnPay;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCzFlag() {
        return czFlag;
    }

    public void setCzFlag(String czFlag) {
        this.czFlag = czFlag;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getSetlIdReturn() {
        return setlIdReturn;
    }

    public void setSetlIdReturn(String setlIdReturn) {
        this.setlIdReturn = setlIdReturn;
    }
}