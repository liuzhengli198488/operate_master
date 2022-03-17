package com.gys.report.entity;

import lombok.Data;

/**
 * 返回参数
 */
@Data
public class MemberSalesOutData {
    private static final long serialVersionUID = -4151234302151668982L;
//    加盟商
    private String client;
//    办卡门店
    private String cardStore;
//    会员卡号
    private String hykNo;
//   会员名
    private String hykName;
//    手机号码
    private String mobile;
//    性别
    private String sex;
//    门店编码
    private String stoCode;
//    门店名称（消费）
    private String saleStore;
//    商品编码
    private String proCode;
//    商品名称
    private String productName;
//    应收金额
    private String gssdnormalAmt;
//    实收金额
    private String ssAmount;
//    折扣金额
    private String discountAmt;
//    折扣率
    private String discountRate;
//    期间天数
    private String totalSelledDays;
//    客单价
    private String visitUnitPrice;
//    含税成本额
    private String allCostAmt;
//    毛利额
    private String grossProfitAmt;
//    毛利率
    private String grossProfitRate;
//    商品大类
    private String bigClass;
//    商品中类
    private String midClass;
//    商品小类
    private String smallClass;
//    销售等级
    private String saleClass;
//    商品定位
    private String proPosition;
//    消费天数
    private String selledDays;
//    消费次数
    private String tradedTime;
//    消费单价
    private String dailyPayAmt;
//    消费频次
    private String dailyPayCount;
//    客品次
    private String proAvgCount;
//    品单价
    private String billAvgPrice;
//    自定义
    private String zdy1;
    private String zdy2;
    private String zdy3;
    private String zdy4;
    private String zdy5;
//    商品自分类
    private String proClass;
//    商品数量
    private String count;
    //商品品项数
    private String proCount;


//    @ApiModelProperty(value = "会员销售额")
//    private String memeberSaleAmt;
//    @ApiModelProperty(value = "会员销售占比")
//    private String memeberSaleRate;
//    @ApiModelProperty(value = "去税成本额")
//    private String costAmt;
}
