package com.gys.entity.data.MonthPushMoney.V2;

import com.gys.entity.SiteBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description TODO
 * @Author huxinxin
 * @Date 2021/7/8 17:19
 * @Version 1.0.0
 **/
@Data
public class TichengZInfoBO {
    @ApiModelProperty(value = "加盟商")
    private String client;
    @ApiModelProperty(value = "方案ID")
    private int planId;
    @ApiModelProperty(value = "方案名称")
    private String planName;
    @ApiModelProperty(value = "开始时间")
    private String startDate;
    @ApiModelProperty(value = "结束时间")
    private String endtDate;
    @ApiModelProperty(value = "提成类型 1：销售提成 2：单品提成")
    private String type;
    @ApiModelProperty(value = "状态")
    private String status;
    @ApiModelProperty(value = "门店集合")
    private List<SiteBO> stoArr;
    @ApiModelProperty(value = "门店数量")
    private int stoCount;
    @ApiModelProperty(value = "单品提成方式 0 不参与销售提成 1 参与销售提成")
    private String planProductWay;

    @ApiModelProperty(value = "剔除折扣率 操作符号")
    private String planRejectDiscountRateSymbol;

    @ApiModelProperty(value = "剔除折扣率 百分比值")
    private String planRejectDiscountRate;

    @ApiModelProperty(value = "子方案名称")
    private String settingId;

}
