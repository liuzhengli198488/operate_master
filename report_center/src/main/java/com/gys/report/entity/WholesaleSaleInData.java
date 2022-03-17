package com.gys.report.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "批发销售查询")
public class WholesaleSaleInData {
    @ApiModelProperty(value = "加盟商")
    private String client;
    @ApiModelProperty(value = "业务类型")
    private String matType;
    @ApiModelProperty(value = "开始日期")
    private String startDate;
    @ApiModelProperty(value = "截至日期")
    private String endDate;
    @ApiModelProperty(value = "客户编码")
    private String stoCode;
    @ApiModelProperty(value = "客户编码")
    private String[] stoArr;
    @ApiModelProperty(value = "地点编码")
    private String siteCode;
    @ApiModelProperty(value = "地点批量查询")
    private String[] siteArr;
    @ApiModelProperty(value = "业务单据号")
    private String dnId;
    @ApiModelProperty(value = "合计金额")
    private String totalAmount;
    @ApiModelProperty(value = "生产厂家")
    private String proFactoryName;
    @ApiModelProperty(value = "供应商编码")
    private String supplierCode;
    @ApiModelProperty(value = "商品编码")
    private String proCodes;
    @ApiModelProperty(value = "商品编码")
    private String[] proArr;
    //当前页
    private Integer pageNum;
    //页面条数
    private Integer pageSize;
    private List<String> matTypes;

    //商品自分类
    private String[] prosClass;
    //销售级别
    private String[] saleClass;
    //商品定位
    private String[] proPosition;
    //禁止采购
    private String purchase;
    //自定义1
    private String[] zdy1;
    //自定义2
    private String[] zdy2;
    //自定义3
    private String[] zdy3;
    //自定义4
    private String[] zdy4;
    //自定义5
    private String[] zdy5;
    //销售员
    private String gwoOwnerSaleMan;
    //货主
    private String gwoOwner;
    //抬头备注
    private String soHeaderRemark;
    //销售人员ID集合
    private List<String> gwoOwnerSaleMans;
    // 大类 中类 商品分类
    private String [][] classArr;
    private List<String> classArrs;

    /**
     * 0按 客户汇总, 1 按销售人员汇总, 2 按 开单员汇总
     */
    private String condition;
    //开单人员集合
    private List<String> buillingStaffIds;

}
