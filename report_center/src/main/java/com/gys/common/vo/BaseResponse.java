package com.gys.common.vo;

import com.gys.common.vo.response.BatchInfoRes;
import lombok.Data;

@Data
public class BaseResponse {
    private int code;

    private boolean success;

    private String msg;

    private BatchInfoRes data;
}
