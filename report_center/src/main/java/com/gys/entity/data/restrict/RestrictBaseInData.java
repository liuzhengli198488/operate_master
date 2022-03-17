package com.gys.entity.data.restrict;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 查询报表集成数据权限基础类
 * 考虑到数据权限涉及到的有门店和仓库，分成两个集合进行处理
 * 此类不允许修改，如果需要修改联系《樊挺》
 * @author: flynn
 * @date: 2022年01月27日 下午4:16
 */
@Data
public class RestrictBaseInData implements Serializable {

    //用于添加用户涉及到的数据权限的门店集合
    private List<String> restrictStoCodes;

    //用于添加用户涉及到的数据权限的仓库集合
    private List<String> restrictDcCodes;

}

