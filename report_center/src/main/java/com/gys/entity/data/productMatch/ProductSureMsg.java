package com.gys.entity.data.productMatch;

import lombok.Data;

@Data
public class ProductSureMsg {
    private String clientId;
    private String stoCode;
    private String site;
    private String proCode;
    private String proCodeG;
    private String matchBatch;
    /** add by jinwencheng on 2022-01-7 17:53:11 添加matchCode属性 **/
    private String matchCode;
    /** add end **/


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
