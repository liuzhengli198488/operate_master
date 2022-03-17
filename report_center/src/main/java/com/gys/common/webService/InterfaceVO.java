package com.gys.common.webService;

import lombok.Data;

@Data
public class InterfaceVO {
    private String success;
    private String result;
    private String message;
    public void success(){
        this.success="Y";
    }
    public void failed(){
        this.success="N";
    }
    public Boolean isSuccess(){
        return "Y".equals(success);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"success\":\"")
                .append(success).append('\"');
        sb.append(",\"result\":\"")
                .append(result).append('\"');
        sb.append(",\"message\":\"")
                .append(message).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
