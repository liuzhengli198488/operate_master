package com.gys.entity.data;

import lombok.Data;

/**
 * @desc:
 * @author: ZhangChi
 * @createTime: 2021/12/6 9:28
 */
@Data
public class YaoJianInData {
    private String client;
    /**
     * 出库单号
     */
    private String billNo;

    /**
     * 商品编码
     */
    private String proCode;

    /**
     * 商品名称
     */
    private String proName;

    /**
     * 助记码
     */
    private String proPYM;

    /**
     * 批号
     */
    private String batchNo;

    /**
     *拣货单号
     */
    private String  whereWmJhdh;

    private Integer pageNum;
    private Integer pageSize;
}
