package com.gys.feign.priceProposal;

import lombok.Data;

/**
 * 接口返回实体
 */
@Data
public class Result {
    //返回码
    private String code;
    //提示信息
    private String msg;
    //返回具体内容
    private Object data;
    // 消息
    private String message;
    // 警告
    private Object warning;

    private Object result1;
}
