package com.gys.entity.data.MonthPushMoney;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: TODO
 * @author: flynn
 * @date: 2021年11月24日 上午9:51
 */
@Data
public class EmpSaleDetailInData implements Serializable {
    private static final long serialVersionUID = 1584563613945288529L;

    private String client;

    @ApiModelProperty(value = "提成方案id")
    private int planId;

    @ApiModelProperty(value = "开始时间")
    private String startDate;
    @ApiModelProperty(value = "结束时间")
    private String endDate;

    private List<String> planDate;

    @ApiModelProperty(value = "方案名称")
    private String planName;

    @ApiModelProperty(value = "提成方案类型 1.销售 2.商品")
    private String type;

    @ApiModelProperty(value = "子方案名称")
    private String cPlanName;

    private String ifShowZplan;//是否展示子方案，1是0否


    @ApiModelProperty(value = "门店数组")
    private List<String> stoArr;

    @ApiModelProperty(value = "提成门店")
    private String stoCode;

    @ApiModelProperty(value = "生产厂家")
    private String factoryName;

    private String nameSearchType;//1表示营业员 2表示收银员 3表示医生

    private List<String> nameSearchIdList;//选择用户的id集合

    @ApiModelProperty(value = "商品编码")
    private String proCode;

    @ApiModelProperty(value = "商品分类")
    private List<String> proClass;

    @ApiModelProperty(value = "剔除商品分配，实时数据专用")
    private List<String> rejectProClass;


    //销售级别
    @ApiModelProperty(value = "销售级别")
    private List<String> saleClass;

    //商品定位
    @ApiModelProperty(value = "商品定位")
    private List<String> proPosition;

    /**
     * 业务员姓名
     */
    @ApiModelProperty(value = "业务员姓名")
    private String gssName;

    /**
     * 供应商自编码
     */
    private String supSelfCode;

    private Integer pageNum;

    private Integer pageSize;

    private String today;

    private String planIfNegative;

    private String settingId;

    @ApiModelProperty(value = "是否管理员 true 是 , false 否")
    private Boolean admin = false;

}

