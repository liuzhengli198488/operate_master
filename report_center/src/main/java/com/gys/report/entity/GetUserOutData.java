
package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

//@ApiModel(value = "人员信息")
@Data
public class GetUserOutData implements Serializable {
    private static final long serialVersionUID = 5526672905990162488L;
    private String account;
    @ApiModelProperty(value = "用户id")
    private String userId;
    private String client;
    @ApiModelProperty(value = "用户姓名")
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
    private String userLoginSta;
    private String token;
    //private List<GetLoginStoreOutData> storeList;
}
