package com.gys.entity.data.salesSummary;

import com.gys.report.entity.GetPayTypeOutData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@ApiModel
public class WebStoreSaleDateInData {
    @ApiModelProperty(value = "加盟商")
    private String client;

    @ApiModelProperty(value = "用户")
    private String userId;

    @ApiModelProperty(value = "门店编码")
    private String stoCode;

    @ApiModelProperty(value = "地点批量查询")
    private String[] siteArr;

    @ApiModelProperty(value = "支付类型")
    private String[] payName;

    @NotBlank(message = "开始时间必填")
    @ApiModelProperty(value = "开始时间")
    private String startDate;

    @NotBlank(message = "结束时间必填")
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
}
