package com.gys.entity.data.xhl.vo;

import lombok.Data;

import javax.persistence.Column;

/**
 * @Auther: tzh
 * @Date: 2021/12/15 23:56
 * @Description: ClientBaseInfoVo
 * @Version 1.0.0
 */
@Data
public class ClientBaseInfoVo {
     private  String client;
     private  String francName;
     private String francAddr;
     private String francProv;
     private String provId;
     private String francCity;
     private String cityId;
}
