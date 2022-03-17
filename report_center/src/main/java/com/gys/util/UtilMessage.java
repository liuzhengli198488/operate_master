package com.gys.util;


import org.apache.ibatis.annotations.One;

/**
 * @author xiaoyuan
 */
public interface UtilMessage {
    String AMOUNT_REGULAR = "^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,4})?$";
    String AMOUNT_REGULAR2 = "^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$";
    String NUMBER_REGULAR = "^\\+?[1-9][0-9]*$";
    String LOCAL = "local";
    Integer One = 1;
    String Suffix1 = "1";
    String Y = "Y";
    String N = "N";
    String WEB = "web";
    String ZERO = "0";
    String ONE = "1";
    String CODE = "code";
    String HHMMSS = "HHmmss";
    String YYYYMMDD = "yyyyMMdd";
    String ONE_THOUSAND = "1000";
    String Token ="Token不能为空,请联系管理员!";
    String TOKEN_ILEGAL ="Token不合法,请刷新页面后操作!";
    String RESTRICT_ERROR ="数据权限获取失败!";
    String OK ="操作成功";

}
