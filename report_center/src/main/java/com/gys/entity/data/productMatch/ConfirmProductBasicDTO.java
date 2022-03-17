package com.gys.entity.data.productMatch;

import lombok.Data;

/**
 * @Author jinwencheng
 * @Desc 确认商品匹配
 * @Date 2021-12-31 14:32:01
 */
@Data
public class ConfirmProductBasicDTO {

    /**
     * 客户编码
     */
    private String clientId;
    /**
     * 门店编码
     */
    private String stoCode;
    /**
     * 用户商品编码
     */
    private String matchProCode;

    /**
     * 药德商品编码
     */
    private String proCode;

    /**
     * 匹配编码
     */
    private String matchCode;
    /**
     * 修改加盟商
     */
    private String updateClient;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 修改时间
     */
    private String updateTime;

}
