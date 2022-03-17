package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 入参
 */
@Data
public class MemberSalesInData {
    //加盟商
    private String client;
    //会员名称
    private String hykName;
    //手机号码
    private String mobile;
    //性别
    private String sex;
    //卡号
    private String hykNo;
    //开始日期
    private String startDate;
    //结束日期
    private String endDate;
    //门店编码
    private String[] stoCode;
    private String stoValue;
    //商品编码
    private String proCode;
    private String[] productCode;
    //商品分类
    private String[][] classArr;
    private List<String> productClass;
    //销售等级
    private String[] saleClass;
    private String saleClassValue;
    //商品定位
    private String[] proPosition;
    private String proPositionvalue;
    //自定义
    private String[] zdy1;
    private String zdy1Value;
    private String[] zdy2;
    private String zdy2Value;
    private String[] zdy3;
    private String zdy3Value;
    private String[] zdy4;
    private String zdy4Value;
    private String[] zdy5;
    private String zdy5Value;
    //商品自分类
    private String[] proClass;
    private String proClassValue;
    //状态(是否选择门店查询:0 明细   1 门店)
    private String state;

    @ApiModelProperty(value = "页数")
    private Integer pageNum;
    @ApiModelProperty(value = "行数")
    private Integer pageSize;
}
