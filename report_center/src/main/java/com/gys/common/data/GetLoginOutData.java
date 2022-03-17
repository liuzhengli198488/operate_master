package com.gys.common.data;

import com.gys.feign.vo.UserRestrictInfo;
import lombok.Data;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.List;

@Data
public class GetLoginOutData implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userId;

    private String loginAccount;

    private String client;
    private String loginName;
    private String userSex;
    private String userTel;
    private String userIdc;
    private String userYsId;
    private String userYsZyfw;
    private String userYsZylb;
    private String depId;
    private String depName;
    private String userAddr;
    private String userSta;
    private String token;
    private String dcCode;
    private List<GetLoginFrancOutData> franchiseeList;

    private UserRestrictInfo userRestrictInfo;
}
