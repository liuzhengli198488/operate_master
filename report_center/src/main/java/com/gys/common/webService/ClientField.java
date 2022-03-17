package com.gys.common.webService;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ClientField {
    /**
     * 参数名称
     */
    private String paramName;
    /**
     * 参数类型
     */
    private Class<?> paramType;
    /**
     * 参数值
     */
    private Object paramValue;

}
