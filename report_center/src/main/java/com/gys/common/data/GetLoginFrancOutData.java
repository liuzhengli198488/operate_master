//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.gys.common.data;

public class GetLoginFrancOutData {
    private String client;
    private String francName;

    public GetLoginFrancOutData() {
    }

    public String getClient() {
        return this.client;
    }

    public String getFrancName() {
        return this.francName;
    }

    public void setClient(final String client) {
        this.client = client;
    }

    public void setFrancName(final String francName) {
        this.francName = francName;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof GetLoginFrancOutData)) {
            return false;
        } else {
            GetLoginFrancOutData other = (GetLoginFrancOutData)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$client = this.getClient();
                Object other$client = other.getClient();
                if (this$client == null) {
                    if (other$client != null) {
                        return false;
                    }
                } else if (!this$client.equals(other$client)) {
                    return false;
                }

                Object this$francName = this.getFrancName();
                Object other$francName = other.getFrancName();
                if (this$francName == null) {
                    if (other$francName != null) {
                        return false;
                    }
                } else if (!this$francName.equals(other$francName)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof GetLoginFrancOutData;
    }

    public int hashCode() {
        
        int result = 1;
        Object $client = this.getClient();
        result = result * 59 + ($client == null ? 43 : $client.hashCode());
        Object $francName = this.getFrancName();
        result = result * 59 + ($francName == null ? 43 : $francName.hashCode());
        return result;
    }

    public String toString() {
        return "GetLoginFrancOutData(client=" + this.getClient() + ", francName=" + this.getFrancName() + ")";
    }
}
