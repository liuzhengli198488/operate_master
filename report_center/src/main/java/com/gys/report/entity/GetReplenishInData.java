
package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class GetReplenishInData implements Serializable {
    private static final long serialVersionUID = 5525835479681983307L;
    @ApiModelProperty(value = "加盟商")
    private String clientId;
    @ApiModelProperty(value = "补货单号")
    private String gsrhVoucherId;
    @ApiModelProperty(value = "预生成补货单号")
    private String preGsrhVoucherId;
    @ApiModelProperty(value = "补货日期")
    private String gsrhDate;
    @ApiModelProperty(value = "补货类型")
    private String gsrhType;
    @ApiModelProperty(value = "补货方式 0正常补货，1紧急补货,2铺货,3互调")
    private String gsrhPattern;
    @ApiModelProperty(value = "补货金额")
    private BigDecimal gsrhTotalAmt;
    @ApiModelProperty(value = "补货数量")
    private String gsrhTotalQty;
    @ApiModelProperty(value = "补货人员")
    private String gsrhEmp;
    @ApiModelProperty(value = "补货状态是否审核")
    private String gsrhStatus;
    @ApiModelProperty(value = "采购单号")
    private String gsrhPoid;
    @ApiModelProperty(value = "门店")
    private String stoCode;
    @ApiModelProperty(value = "商品编码")
    private String productCode;
    @ApiModelProperty(value = "国际条形码")
    private String productBarCode;
    @ApiModelProperty(value = "")
    private String date;
    @ApiModelProperty(value = "")
    private String paramCode;
    @ApiModelProperty(value = "第三方编码")
    private String proThreeCode;
    private List<GetReplenishDetailInData> detailList;
    private List<GetReplenishDetailOutData> detailInList;
    private Integer pageNum;
    private Integer pageSize;
    @ApiModelProperty(value = "商品编码 通用名称 国际条形码1 国际条形码2 助记码 商品名（模糊匹配）")
    private String gspgProId;
    private String startDate;
    private String endDate;
    @ApiModelProperty(value = "清货单号数组")
    private String[] gsrhVoucherIds;
    @ApiModelProperty(value = "商品编码数组")
    private String[] gspgProIds;
    @ApiModelProperty(value = "门店编码数组")
    private String[] stoCodes;
    @ApiModelProperty(value = "连锁公司ID")
    private String stoChainHead;
    @ApiModelProperty(value = "配送中心")
    private String dcCode;
    @ApiModelProperty(value = "供应商编码")
    private String supCode;
    @ApiModelProperty(value = "开始时间")
    private String queryStartDate;
    @ApiModelProperty(value = "结束时间")
    private String queryEndDate;
    private String voucherId;
    @ApiModelProperty(value = "比价单号")
    private String ratioCode;
    @ApiModelProperty(value = "是否全网比价入口")
    private String isPublish;
    private String type;
    private String userId;
    @ApiModelProperty(value = "补货页面已选数据")
    private List<GetReplenishDetailOutData> tableInfoList;
    @ApiModelProperty(value = "补货页面新增数据")
    private List<GetReplenishInData> pickInfoList;
    @ApiModelProperty(value = "是否允许补 禁采商品")
    private String flag;
}
