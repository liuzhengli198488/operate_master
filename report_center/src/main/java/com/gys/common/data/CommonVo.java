package com.gys.common.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommonVo implements Serializable {

    private static final long serialVersionUID = -6311116521300067518L;

    private String lable;

    private Object value;

    public CommonVo(String lable, String value) {
        this.lable = lable;
        this.value = value;
    }
}
