package com.gys.entity.data;

import lombok.Data;

/**
 * @desc:
 * @author: ZhangChi
 * @createTime: 2021/12/6 13:06
 */
@Data
public class ViewPictureInData {
    private String client;
    //供应商编码
    private String supplierCoder;
    //商品编码
    private String proCode;
    //批号
    private String batchNo;
}
