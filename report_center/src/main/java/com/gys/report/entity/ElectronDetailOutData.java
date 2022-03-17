package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ElectronDetailOutData implements Serializable {
    @ApiModelProperty(value = "加盟商")
    private String client;

//    @ApiModelProperty(value = "是否启用 N为否，Y为是")
//    private String gsebStatus;

//    @ApiModelProperty(value = "有效时长 按天")
//    private String gsebDuration;

    @ApiModelProperty(value = "会员卡号")
    private String gsecMemberId;

    @ApiModelProperty(value = "电子券号")
    private String gsecId;

    @ApiModelProperty(value = "电子券活动号")
    private String gsebId;

    @ApiModelProperty(value = "主题号")
    private String gsetsId;

    @ApiModelProperty(value = "电子券金额")
    private BigDecimal gsecAmt;

    @ApiModelProperty(value = "电子券活动描述")
    private String gsecName;

    @ApiModelProperty(value = "是否已使用 N为否，Y为是")
    private String gsecStatus;

    @ApiModelProperty(value = "途径 0为线下，1为线上")
    private String gsecFlag;

    @ApiModelProperty(value = "销售单号")
    private String gsecBillNo;

    @ApiModelProperty(value = "销售门店")
    private String gsecBrId;

    @ApiModelProperty(value = "销售日期")
    private String gsecSaleDate;

    @ApiModelProperty(value = "创建日期")
    private String gsecCreateDate;

    @ApiModelProperty(value = "失效日期")
    private String gsecFailDate;

    @ApiModelProperty(value = "业务类型 0为送券，1为用券")
    private String gsebsType;

    @ApiModelProperty(value = "业务单号")
    private String gsebsId;

    @ApiModelProperty(value = "起始日期")
    private String gsebsBeginDate;

    @ApiModelProperty(value = "结束日期")
    private String gsebsEndDate;

    @ApiModelProperty(value = "起始时间")
    private String gsebsBeginTime;

    @ApiModelProperty(value = "结束时间")
    private String gsebsEndTime;

    @ApiModelProperty(value = "创建日期")
    private String gsebsCreateDate;

    @ApiModelProperty(value = "创建时间")
    private String gsebsCreateTime;

    @ApiModelProperty(value = "是否审核 N为否，Y为是")
    private String gsebsStatus;

    @ApiModelProperty(value = "是否停用 N为否，Y为是")
    private String gsebsFlag;

    @ApiModelProperty(value = "促销商品是否参与")
    private String gsebsProm;

    @ApiModelProperty(value = "达到数量1")
    private String gsecsReachQty1;

    @ApiModelProperty(value = "达到金额1")
    private BigDecimal gsecsReachAmt1;

    @ApiModelProperty(value = "送券数量1")
    private String gsecsResultQty1;

    @ApiModelProperty(value = "达到数量2")
    private String gsecsReachQty2;

    @ApiModelProperty(value = "达到金额2")
    private BigDecimal gsecsReachAmt2;

    @ApiModelProperty(value = "送券数量2")
    private String gsecsResultQty2;

    @ApiModelProperty(value = "是否重复 N为否，Y为是")
    private String gsecsFlag1;

    @ApiModelProperty(value = "是否全场商品 N为否，Y为是")
    private String gsecsFlag2;

    @ApiModelProperty(value = "商品组名")
    private String gsecsProGroup;

    @ApiModelProperty(value = "商品组")
    private List<GaiaSdProductsGroup> proGroup;

    @ApiModelProperty(value = "主题已送数")
    private String gsetsQty;

    @ApiModelProperty(value = "主题最大送数")
    private String gsetsMaxQty;

    @ApiModelProperty(value = "门店最大送数")
    private String gsebsMaxQty;

    @ApiModelProperty(value = "门店已送数")
    private String gsebsQty;

    /**
     * 用券截止时间
     */
    private String gsecUseExpirationDate;
}
