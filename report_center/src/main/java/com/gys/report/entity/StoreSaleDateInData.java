package com.gys.report.entity;

import com.gys.common.data.RestrictBasicInData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class StoreSaleDateInData extends RestrictBasicInData {
    @ApiModelProperty(value = "开始时间段1030")
    private String statDatePart;
    @ApiModelProperty(value = "结束时间段1130")
    private String endDatePart;
    private String client;
    @ApiModelProperty(value = "门店编码")
    private String stoCode;
    @ApiModelProperty(value = "地点批量查询")
    private String[] siteArr;
    @ApiModelProperty(value = "支付类型")
    private String[] payName;
    @ApiModelProperty(value = "开始时间")
    private String startDate;
    @ApiModelProperty(value = "结束时间")
    private String endDate;
    @ApiModelProperty(value = "收款方式")
    private String saleType;
    @ApiModelProperty(value = "时间类型 1.年 2.月 3.日")
    private String dateType;
    @ApiModelProperty(value = "最大毛利率")
    private String grossProfitRateMax;
    @ApiModelProperty(value = "最小毛利率")
    private String grossProfitRateMin;
    private List<GetPayTypeOutData> payTypeOutData;
    private Integer pageNum;
    private Integer pageSize;
    @ApiModelProperty(value = "按日期还是按门店 false为按门店 true为按日期")
    private Boolean isForDate;

    @ApiModelProperty(value = "分类id")
    private String gssgId;

    @ApiModelProperty(hidden = true)
    private List<String> gssgIds;

    @ApiModelProperty(value = "分类类型")
    private String stoGssgType;

    @ApiModelProperty(hidden = true)
    private List<String> stoGssgTypes;

    @ApiModelProperty(value = "门店属性")
    private String stoAttribute;

    @ApiModelProperty(hidden = true)
    private List<String> stoAttributes;

    @ApiModelProperty(value = "是否医保店")
    private String stoIfMedical;

    @ApiModelProperty(hidden = true)
    private List<String> stoIfMedicals;

    @ApiModelProperty(value = "纳税属性")
    private String stoTaxClass;

    @ApiModelProperty(hidden = true)
    private List<String> stoTaxClasss;

    @ApiModelProperty(value = "DTP")
    private String stoIfDtp;

    @ApiModelProperty(hidden = true)
    private List<String> stoIfDtps;

}
