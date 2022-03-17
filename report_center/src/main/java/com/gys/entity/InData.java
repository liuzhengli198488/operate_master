package com.gys.entity;

import com.gys.report.entity.GaiaStoreCategoryType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description 通用的入参类
 * @Author huxinxin
 * @Date 2021/4/19 16:40
 * @Version 1.0.0
 **/
@Data
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
public class InData {
    @ApiModelProperty(value = "加盟商")
    private String client;
    @ApiModelProperty(value = "门店")
    private String stoCode;
    @ApiModelProperty(value = "门店数组")
    private String[] stoArr;
    @ApiModelProperty(value = "仓库")
    private String dcCode;
    @ApiModelProperty(value = "地点(包括门店和仓库)")
    private String siteCode;
    @ApiModelProperty(value = "地点(包括门店和仓库)")
    private List<String> siteArr;
    //自定义用途 注释自己写到代码里
    @ApiModelProperty(value = "类型")
    private String type;
    @ApiModelProperty(value = "起始时间")
    private String startDate;
    @ApiModelProperty(value = "结束时间")
    private String endDate;
    private int pageSize;
    private int pageNum;

    @ApiModelProperty(value = "分类id")
    private String gssgId;

    @ApiModelProperty(hidden = true)
    private List<String> gssgIds;

    @ApiModelProperty(value = "分类类型")
    private String stoGssgType;

    @ApiModelProperty(hidden = true)
    private List<GaiaStoreCategoryType> stoGssgTypes;

    @ApiModelProperty(value = "门店属性")
    private String stoAttribute;

    @ApiModelProperty(hidden = true)
    private List<String> stoAttributes;

    @ApiModelProperty(value = "是否医保店")
    private String stoIfMedical;

    @ApiModelProperty(hidden = true)
    private List<String> stoIfMedicals;

    @ApiModelProperty(value = "纳税属性")
    private String stoTaxClass;

    @ApiModelProperty(hidden = true)
    private List<String> stoTaxClasss;

    @ApiModelProperty(value = "DTP")
    private String stoIfDtp;

    @ApiModelProperty(hidden = true)
    private List<String> stoIfDtps;

    /**
     * 销售单号
     */
    private String billNo;
    /**
     * 医保结算Id
     */
    private String setlId;
    /**
     * 医保交易流水号
     */
    private String msgId;
    /**
     * 清算区划
     */
    private String clrOptins;

    public String getClrOptins() {
        return clrOptins;
    }

    public void setClrOptins(String clrOptins) {
        this.clrOptins = clrOptins;
    }

    public String getGssgId() {
        return gssgId;
    }

    public void setGssgId(String gssgId) {
        this.gssgId = gssgId;
    }

    public String getStoGssgType() {
        return stoGssgType;
    }

    public void setStoGssgType(String stoGssgType) {
        this.stoGssgType = stoGssgType;
    }

    public String getStoAttribute() {
        return stoAttribute;
    }

    public void setStoAttribute(String stoAttribute) {
        this.stoAttribute = stoAttribute;
    }

    public String getStoIfMedical() {
        return stoIfMedical;
    }

    public void setStoIfMedical(String stoIfMedical) {
        this.stoIfMedical = stoIfMedical;
    }

    public String getStoTaxClass() {
        return stoTaxClass;
    }

    public void setStoTaxClass(String stoTaxClass) {
        this.stoTaxClass = stoTaxClass;
    }

    public String getStoIfDtp() {
        return stoIfDtp;
    }

    public void setStoIfDtp(String stoIfDtp) {
        this.stoIfDtp = stoIfDtp;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getStoCode() {
        return stoCode;
    }

    public void setStoCode(String stoCode) {
        this.stoCode = stoCode;
    }

    public String[] getStoArr() {
        return stoArr;
    }

    public void setStoArr(String[] stoArr) {
        this.stoArr = stoArr;
    }

    public String getDcCode() {
        return dcCode;
    }

    public void setDcCode(String dcCode) {
        this.dcCode = dcCode;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public List<String> getSiteArr() {
        return siteArr;
    }

    public void setSiteArr(List<String> siteArr) {
        this.siteArr = siteArr;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public List<String> getGssgIds() {
        return gssgIds;
    }

    public void setGssgIds(List<String> gssgIds) {
        this.gssgIds = gssgIds;
    }

    public List<GaiaStoreCategoryType> getStoGssgTypes() {
        return stoGssgTypes;
    }

    public void setStoGssgTypes(List<GaiaStoreCategoryType> stoGssgTypes) {
        this.stoGssgTypes = stoGssgTypes;
    }

    public List<String> getStoAttributes() {
        return stoAttributes;
    }

    public void setStoAttributes(List<String> stoAttributes) {
        this.stoAttributes = stoAttributes;
    }

    public List<String> getStoTaxClasss() {
        return stoTaxClasss;
    }

    public void setStoTaxClasss(List<String> stoTaxClasss) {
        this.stoTaxClasss = stoTaxClasss;
    }

    public List<String> getStoIfDtps() {
        return stoIfDtps;
    }

    public void setStoIfDtps(List<String> stoIfDtps) {
        this.stoIfDtps = stoIfDtps;
    }

    public List<String> getStoIfMedicals() {
        return stoIfMedicals;
    }

    public void setStoIfMedicals(List<String> stoIfMedicals) {
        this.stoIfMedicals = stoIfMedicals;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getSetlId() {
        return setlId;
    }

    public void setSetlId(String setlId) {
        this.setlId = setlId;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public InData(String client) {
        this.client = client;
    }

    public InData(String client, String[] stoArr, String type) {
        this.client = client;
        this.stoArr = stoArr;
        this.type = type;
    }
    public InData(String client, String dcCode, String type) {
        this.client = client;
        this.dcCode = dcCode;
        this.type = type;
    }
}
