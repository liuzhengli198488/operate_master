//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.gys.report.entity;

import java.math.BigDecimal;

public class GetDzqInData {
    private String clientId;
    private String proCode;
    private String gspcbActNo;
    private String gspcbCount;
    private BigDecimal gspcbCouponAmt;

    public GetDzqInData() {
    }

    public String getClientId() {
        return this.clientId;
    }

    public String getProCode() {
        return this.proCode;
    }

    public String getGspcbActNo() {
        return this.gspcbActNo;
    }

    public String getGspcbCount() {
        return this.gspcbCount;
    }

    public BigDecimal getGspcbCouponAmt() {
        return this.gspcbCouponAmt;
    }

    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }

    public void setProCode(final String proCode) {
        this.proCode = proCode;
    }

    public void setGspcbActNo(final String gspcbActNo) {
        this.gspcbActNo = gspcbActNo;
    }

    public void setGspcbCount(final String gspcbCount) {
        this.gspcbCount = gspcbCount;
    }

    public void setGspcbCouponAmt(final BigDecimal gspcbCouponAmt) {
        this.gspcbCouponAmt = gspcbCouponAmt;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof GetDzqInData)) {
            return false;
        } else {
            GetDzqInData other = (GetDzqInData)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label71: {
                    Object this$clientId = this.getClientId();
                    Object other$clientId = other.getClientId();
                    if (this$clientId == null) {
                        if (other$clientId == null) {
                            break label71;
                        }
                    } else if (this$clientId.equals(other$clientId)) {
                        break label71;
                    }

                    return false;
                }

                Object this$proCode = this.getProCode();
                Object other$proCode = other.getProCode();
                if (this$proCode == null) {
                    if (other$proCode != null) {
                        return false;
                    }
                } else if (!this$proCode.equals(other$proCode)) {
                    return false;
                }

                label57: {
                    Object this$gspcbActNo = this.getGspcbActNo();
                    Object other$gspcbActNo = other.getGspcbActNo();
                    if (this$gspcbActNo == null) {
                        if (other$gspcbActNo == null) {
                            break label57;
                        }
                    } else if (this$gspcbActNo.equals(other$gspcbActNo)) {
                        break label57;
                    }

                    return false;
                }

                Object this$gspcbCount = this.getGspcbCount();
                Object other$gspcbCount = other.getGspcbCount();
                if (this$gspcbCount == null) {
                    if (other$gspcbCount != null) {
                        return false;
                    }
                } else if (!this$gspcbCount.equals(other$gspcbCount)) {
                    return false;
                }

                Object this$gspcbCouponAmt = this.getGspcbCouponAmt();
                Object other$gspcbCouponAmt = other.getGspcbCouponAmt();
                if (this$gspcbCouponAmt == null) {
                    if (other$gspcbCouponAmt == null) {
                        return true;
                    }
                } else if (this$gspcbCouponAmt.equals(other$gspcbCouponAmt)) {
                    return true;
                }

                return false;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof GetDzqInData;
    }

    public int hashCode() {
        
        int result = 1;
        Object $clientId = this.getClientId();
        result = result * 59 + ($clientId == null ? 43 : $clientId.hashCode());
        Object $proCode = this.getProCode();
        result = result * 59 + ($proCode == null ? 43 : $proCode.hashCode());
        Object $gspcbActNo = this.getGspcbActNo();
        result = result * 59 + ($gspcbActNo == null ? 43 : $gspcbActNo.hashCode());
        Object $gspcbCount = this.getGspcbCount();
        result = result * 59 + ($gspcbCount == null ? 43 : $gspcbCount.hashCode());
        Object $gspcbCouponAmt = this.getGspcbCouponAmt();
        result = result * 59 + ($gspcbCouponAmt == null ? 43 : $gspcbCouponAmt.hashCode());
        return result;
    }

    public String toString() {
        return "GetDzqInData(clientId=" + this.getClientId() + ", proCode=" + this.getProCode() + ", gspcbActNo=" + this.getGspcbActNo() + ", gspcbCount=" + this.getGspcbCount() + ", gspcbCouponAmt=" + this.getGspcbCouponAmt() + ")";
    }
}
