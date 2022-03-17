package com.gys.feign.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 此类不允许更改，如果需要更改，联系《flynn》
 * @author: flynn
 * @date: 2022年02月07日 下午4:46
 */
@Data
public class UserRestrictInfo implements Serializable {

    private static final long serialVersionUID = 2598086098100745022L;

    // 0表示所有公司 1表示当前所在公司 2表示区域 3表示选择公司 4表示没有设置数据权限（此时按设计数据权限为当前公司）
    private String restrictType;

    //数据权限涉及到门店编码的集合
    private List<String> restrictStoCodes;
}

