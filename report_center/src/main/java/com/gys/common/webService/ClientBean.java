package com.gys.common.webService;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class ClientBean {
    /**
     * WebService接口请求地址
     */
    private String endpointUrl;
    /**
     * WebService接口请求的命名空间
     */
    private String targetNamespace;
    /**
     * WebService接口请求actionUrl
     */
    private String actionUrl;
    /**
     * WebService接口请求方法名称
     */
    private String methodName;
    /**
     * WebService请求参数集合
     */
    private List<ClientField> clientFields;

}
