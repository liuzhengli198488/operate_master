package com.gys.entity.data.consignment.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * @Auther: tzh
 * @Date: 2021/12/6 17:55
 * @Description: StoreRecommendedSaleDto  门店推荐销售
 * @Version 1.0.0
 */

@Data
public class StoreRecommendedSaleDto {

    @ApiModelProperty(value = "加盟号")
    private String clientId;

    @ApiModelProperty(value = "店号")
    private List<String> brIds;

    //销售店号集合
    private List<String> saleIds;

    @ApiModelProperty(value = "地点批量查询")
    private String[] siteArr;
    @ApiModelProperty(value = "支付类型")
    private String[] payName;
    @ApiModelProperty(value = "营业员id")
    private String queryUserId;
    @ApiModelProperty(value = "收银员id")
    private String empUserId;
    @ApiModelProperty(value = "生产厂家")
    private String factory;
    @ApiModelProperty(value = "医生id")
    private String queryDoctorId;
    @ApiModelProperty(value = "商品id")
    private String queryProId;
    @ApiModelProperty(value = "商品id")
    private List<String> proArr;
    @ApiModelProperty(value = "起始日期")
    @NotBlank(message = "起始日期不为空")
    private String queryStartDate;

    @ApiModelProperty(value = "结束日期")
    @NotBlank(message = "结束日期不为空")
    private String queryEndDate;

    @ApiModelProperty(value = "会员卡号")
    private String gsshHykNo;

    @Column(name = "商品分类")
    private String proClass;
    @ApiModelProperty(value = "商品分类查询")
    private String[][] classArr;

    private List<String> classArrs;
    @ApiModelProperty(value = "最大毛利率")
    private String grossProfitRateMax;

    @ApiModelProperty(value = "最小毛利率")
    private String grossProfitRateMin;

    @ApiModelProperty(value = "是否医保")
    private String medProdctStatus;
    @ApiModelProperty(value = "会员ID")
    private String  memberId;
    @ApiModelProperty(value = "会员ID")
    private String  memberName;
    @ApiModelProperty(value = "单号集合")
    private List<String> billNoArr;
    @ApiModelProperty(value = "单号")
    private String billNo;
    @ApiModelProperty(value = "批号")
    private String batchNo;
    @ApiModelProperty(value = "效期")
    private String expiryDay;

    @ApiModelProperty(value = "供应商编码")
    private String supplierCode;
    @ApiModelProperty(value = "导出人员类型(导出用)")
    private String userType;
    private Integer pageNum;
    private Integer pageSize;
    private String orderBy;

    /**
     * 是否查询黑名单标志   商品白名单参数 1 为不显示
     */
    private String flag;

    @ApiModelProperty(value = "销售等级")
    private String proSaleClass;
    @ApiModelProperty(value = "商品自定义1")
    private String proZdy1;
    @ApiModelProperty(value = "商品自定义2")
    private String proZdy2;
    @ApiModelProperty(value = "商品自定义3")
    private String proZdy3;

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

    @ApiModelProperty(value = "特殊定义0.药品 1.医疗器械 2.含麻药品 3.保健品")
    private String specialType;
    // 0是推荐未完成  1是推荐完成
    private Integer  tag;
    //是否挂起  推荐完成是不挂起
    private String gsshHideFlag;

}
