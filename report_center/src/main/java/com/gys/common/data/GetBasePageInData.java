//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.gys.common.data;

import java.beans.ConstructorProperties;
import java.io.Serializable;

public class GetBasePageInData implements Serializable {
    private static final long serialVersionUID = 7401087426616024367L;
    public int pageSize;
    private int pageNum;
    private String createTime;
    private String updateTime;
    private String loginAccount;

    public int getPageSize() {
        return this.pageSize;
    }

    public int getPageNum() {
        return this.pageNum;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public String getUpdateTime() {
        return this.updateTime;
    }

    public String getLoginAccount() {
        return this.loginAccount;
    }

    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    public void setPageNum(final int pageNum) {
        this.pageNum = pageNum;
    }

    public void setCreateTime(final String createTime) {
        this.createTime = createTime;
    }

    public void setUpdateTime(final String updateTime) {
        this.updateTime = updateTime;
    }

    public void setLoginAccount(final String loginAccount) {
        this.loginAccount = loginAccount;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof GetBasePageInData)) {
            return false;
        } else {
            GetBasePageInData other = (GetBasePageInData)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getPageSize() != other.getPageSize()) {
                return false;
            } else if (this.getPageNum() != other.getPageNum()) {
                return false;
            } else {
                label52: {
                    Object this$createTime = this.getCreateTime();
                    Object other$createTime = other.getCreateTime();
                    if (this$createTime == null) {
                        if (other$createTime == null) {
                            break label52;
                        }
                    } else if (this$createTime.equals(other$createTime)) {
                        break label52;
                    }

                    return false;
                }

                Object this$updateTime = this.getUpdateTime();
                Object other$updateTime = other.getUpdateTime();
                if (this$updateTime == null) {
                    if (other$updateTime != null) {
                        return false;
                    }
                } else if (!this$updateTime.equals(other$updateTime)) {
                    return false;
                }

                Object this$loginAccount = this.getLoginAccount();
                Object other$loginAccount = other.getLoginAccount();
                if (this$loginAccount == null) {
                    if (other$loginAccount != null) {
                        return false;
                    }
                } else if (!this$loginAccount.equals(other$loginAccount)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof GetBasePageInData;
    }

    public int hashCode() {

        int result = 1;
        result = result * 59 + this.getPageSize();
        result = result * 59 + this.getPageNum();
        Object $createTime = this.getCreateTime();
        result = result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
        Object $updateTime = this.getUpdateTime();
        result = result * 59 + ($updateTime == null ? 43 : $updateTime.hashCode());
        Object $loginAccount = this.getLoginAccount();
        result = result * 59 + ($loginAccount == null ? 43 : $loginAccount.hashCode());
        return result;
    }

    public String toString() {
        return "GetBasePageInData(pageSize=" + this.getPageSize() + ", pageNum=" + this.getPageNum() + ", createTime=" + this.getCreateTime() + ", updateTime=" + this.getUpdateTime() + ", loginAccount=" + this.getLoginAccount() + ")";
    }

    public GetBasePageInData() {
    }

    @ConstructorProperties({"pageSize", "pageNum", "createTime", "updateTime", "loginAccount"})
    public GetBasePageInData(final int pageSize, final int pageNum, final String createTime, final String updateTime, final String loginAccount) {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.loginAccount = loginAccount;
    }
}
