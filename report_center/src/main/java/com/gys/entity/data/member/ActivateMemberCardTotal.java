package com.gys.entity.data.member;

import lombok.Data;

/**
 * @author 胡鑫鑫
 */
@Data
public class ActivateMemberCardTotal {

    /**
    门店号
     */
    private String stoName;
    /**
     * 预备会员激活数量
     */
    private String qty;

}
