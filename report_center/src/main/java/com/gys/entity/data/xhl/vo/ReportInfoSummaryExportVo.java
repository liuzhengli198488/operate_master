package com.gys.entity.data.xhl.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @desc: 统计维度为加盟商级别
 * @author: tzh
 * @createTime: 2021/8/29 18:46
 */

@Data
public class ReportInfoSummaryExportVo implements Serializable {


    private static final long serialVersionUID = -6552787976881946465L;

    @ExcelProperty(value = "时间")
    private String date;//根据不同维度展示不同，可能为天，可能为年月，可能为周次
    @ExcelProperty(value = "省")
    private String province;
    @ExcelProperty(value = "市")
    private String city;
    @ExcelProperty(value = "客户ID")
    private String client;
    @ExcelProperty(value = "客户名称")
    private String francName;

    @ExcelProperty(value = "补货门店数")
    private Integer replenishmentStoreNum;



    /**
     * 开单配送数量（出库原数量）
     */
    @ExcelProperty(value = "出库原数量")
    private BigDecimal dnNum;

    /**
     * 开单配送数量（已过账）(出库数量)
     */
    @ExcelProperty(value = "出库数量")
    private BigDecimal sendNum;

    /**
     * 过账数量
     */
    @ExcelProperty(value = "过账数量")
    private BigDecimal gzNum;

    /**
     * 订单原数量
     */
    @ExcelProperty(value = "订单原数量")
    private BigDecimal orderNum;

    /**
     * 订单数量（已过账）
     */
    @ExcelProperty(value = "订单数量")
    private BigDecimal finalNum;

    @ExcelProperty(value = "配货数量下货率")
    private  String  distributionNumRate;



    @ExcelProperty(value = "发货数量下货率")
    private String sendNumRate;



    @ExcelProperty(value = "最终数量下货率")
    private  String  finalNumRate;




    /**
     * 出库原金额 (开单配送金额)
     */
    @ExcelProperty(value = "出库原金额")
    private BigDecimal dnAmount;
    /**
     * 出库金额(开单配送金额(已过账))
     */
    @ExcelProperty(value = "出库金额")
    private BigDecimal sendAmount;
    /**
     * 过账金额
     */
    @ExcelProperty(value = "过账金额")
    private BigDecimal gzAmount;
    /**
     * 原订单金额
     */
    @ExcelProperty(value = "原订单金额")
    private BigDecimal orderAmount;

    /**
     *  订单金额（已过账）
     */
    @ExcelProperty(value = "订单金额")
    private BigDecimal finalOrderAmount;

    // 配货金额满足率
    @ExcelProperty(value = "配货金额下货率")
    private String distributionAmountRate;
    // 发货金额满足率
    @ExcelProperty(value = "发货金额下货率")
    private String sendAmountRate;
    // 最终金额满足率
    @ExcelProperty(value = "最终金额下货率")
    private String finalAmountRate;





    /**
     * 出库原品项（开单配送品项数）
     */
    @ExcelProperty(value = "出库原品项")
    private BigDecimal dnProductNum;

    /**
     * 出库品项 (开单配送品项数(已过账))
     */
    @ExcelProperty(value = "出库品项")
    private BigDecimal sendProductNum;

    /**
     * 过账品项
     */
    @ExcelProperty(value = "过账品项")
    private BigDecimal gzProductNum;

    /**
     * 原订单品项
     */
    @ExcelProperty(value = "原订单品项")
    private BigDecimal orderProductNum;

    /**
     * 订单品项（已过账）
     */
    @ExcelProperty(value = "订单品项")
    private BigDecimal finalProductNum;


    // 配货品相满足率
    @ExcelProperty(value = "配货品项下货率")
    private String distributionProductRate;

    //发货品相满足率
    @ExcelProperty(value = "发货品项下货率")
    private String sendProductRate;
    // 最终品相满足率
    @ExcelProperty(value = "最终品项下货率")
    private String finalProductRate;

    @ExcelProperty(value = "品项平均下货率")
    private String averageRateStr;

    @ExcelProperty(value = "品项行业平均下货率")
    private String  industryAverageStr;

    @ExcelProperty(value = "对比")
    private String diffStr;

    @ExcelProperty(value = "计入主动铺货")
    private Integer shopGoodsNum;

    @ExcelProperty(value = "导出次数")
    private Integer exportNum;

    @ExcelProperty(value = "查看趋势图次数")
    private Integer trendNum;

    @ExcelProperty(value = "手机端查看次数")
    private Integer mobileViewNum;

    @ExcelProperty(value = "网页端查看次数")
    private Integer webViewNum;

    public ReportInfoSummaryExportVo() {
        this.orderNum=BigDecimal.ZERO;
        this.dnNum=BigDecimal.ZERO;
        this.sendNum=BigDecimal.ZERO;
        this.gzNum=BigDecimal.ZERO;
        this.finalNum=BigDecimal.ZERO;

        this.orderAmount=BigDecimal.ZERO;
        this.dnAmount=BigDecimal.ZERO;
        this.sendAmount=BigDecimal.ZERO;
        this.gzAmount=BigDecimal.ZERO;
        this.finalOrderAmount=BigDecimal.ZERO;

        this.orderProductNum=BigDecimal.ZERO;
        this.dnProductNum=BigDecimal.ZERO;
        this.sendProductNum=BigDecimal.ZERO;
        this.gzProductNum=BigDecimal.ZERO;
        this.finalProductNum =BigDecimal.ZERO;
        this.replenishmentStoreNum=0;
    }

}
