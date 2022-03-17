package com.gys.entity.data;

import com.gys.report.entity.GaiaStoreCategoryType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class StoreBudgetPlanInData {
    @ApiModelProperty(value = "开始时间")
    private String startDate;
    @ApiModelProperty(value = "结束时间")
    private String endDate;
    @ApiModelProperty(value = "门店编码")
    private String stoCode;
    @ApiModelProperty(value = "门店编码批量查询")
    private String[] stoCodeArr;
    @ApiModelProperty(value = "折扣率")
    private String stoRebate;
    @ApiModelProperty(value = "税率")
    private String stoTaxRate;
    private String client;
    @ApiModelProperty(value = "时间类型 1.年 2.月 3.日")
    private String dateType;
    private Integer pageNum;
    private Integer pageSize;
    private String orderBy;

    /**
     * 是否开启查询白名单标志   0：不开启  1：开启
     */
    private String flag;


    @ApiModelProperty(value = "分类id")
    private String gssgId;

    @ApiModelProperty(hidden = true)
    private List<String> gssgIds;

    @ApiModelProperty(value = "分类类型")
    private String stoGssgType;

    @ApiModelProperty(hidden = true)
    private List<GaiaStoreCategoryType> stoGssgTypes;

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
    /*
     * 天数
     * */
    private String salesDays;

    private String[] cusSelfCodeList;
}
