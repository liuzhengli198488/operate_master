package com.gys.common.data;

import com.gys.feign.vo.UserRestrictInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 此类不允许修改，如果需要修改请联系《flynn》
 * @author: flynn
 * @date: 2022年02月07日 下午5:32
 */
@Data
public class RestrictBasicInData implements Serializable {

    private static final long serialVersionUID = -9121580950110408649L;

    private UserRestrictInfo userRestrictInfo;
}

