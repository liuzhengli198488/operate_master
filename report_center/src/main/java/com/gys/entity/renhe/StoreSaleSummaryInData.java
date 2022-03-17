package com.gys.entity.renhe;

import com.gys.report.entity.GaiaStoreCategoryType;
import com.gys.report.entity.GetPayTypeOutData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 销售汇总传参
 *
 * @author xiaoyuan on 2020/7/24
 */
@Data
public class StoreSaleSummaryInData implements Serializable {

    private static final long serialVersionUID = -7740911306970324149L;

    private Integer pageNum;

    private Integer pageSize;

    @ApiModelProperty(value = "开始时间段1030")
    private String statDatePart;
    @ApiModelProperty(value = "结束时间段1130")
    private String endDatePart;
    /**
     * 加盟号
     */
    @ApiModelProperty(value = "加盟商")
    private String client;

    /**
     * 地点
     */
    @ApiModelProperty(value = "店号")
    private String storeCode;

    @ApiModelProperty(value = "地点批量查询")
    private String[] siteArr;
    /**
     * 商品编码
     */
    @ApiModelProperty(value = "商品编码")
    private String gssdProId;
    @ApiModelProperty(value = "支付类型")
    private String[] payName;
    @ApiModelProperty(value = "商品编码批量查询")
    private String[] proArr;
    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称")
    private String  proGlyph;


    /**
     * 商品分类
     */
    @ApiModelProperty(value = "商品分类")
    private String proClass;
    @ApiModelProperty(value = "商品分类查询")
    private String[][] classArr;
    private List<String> classArrs;
    /**
     * 营业员Id
     */
    @ApiModelProperty(value = "营业员id")
    private String gssdSalerId;

    /**
     * 开始日期
     */
//    @NotBlank(message = "起始日期不能为空")
    @ApiModelProperty(value = "起始日期")
    private String startDate;

    /**
     * 结束日期
     */
//    @NotBlank(message = "结束日期不能为空")
    @ApiModelProperty(value = "结束日期")
    private String endDate;

    @ApiModelProperty(value = "活动类型")
    private String proActiveType;

    @ApiModelProperty(value = "属性年月")
    private String sxMonth;

    @ApiModelProperty(value = "最大毛利率")
    private String grossProfitRateMax;

    @ApiModelProperty(value = "最小毛利率")
    private String grossProfitRateMin;

    @ApiModelProperty(value = "是否医保")
    private String medProdctStatus;
    private List<GetPayTypeOutData> payTypeOutData;

    /**
     * 是否开启查询白名单标志   0：不开启  1：开启
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

}
