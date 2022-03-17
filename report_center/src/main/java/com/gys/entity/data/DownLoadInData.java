package com.gys.entity.data;

import lombok.Data;

/**
 * @desc:
 * @author: ZhangChi
 * @createTime: 2021/12/6 18:03
 */
@Data
public class DownLoadInData {
    private String client;
    /**
     * 商品编码
     */
    private String proCode;

    /**
     * 商品名称
     */
    private String proName;

    /**
     * 批号
     */
    private String batchNo;

    /**
     * 供应商编码
     */
    private String supplierCoder;
}
