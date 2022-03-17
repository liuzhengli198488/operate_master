package com.gys.entity.data.dropdata;

import com.gys.util.csv.annotation.CsvCell;
import com.gys.util.csv.annotation.CsvRow;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @desc: 统计维度为加盟商级别
 * @author: flynn
 * @createTime: 2021/8/29 18:46
 */
@CsvRow("下货率报表")
@Data
public class DropRateRes implements Serializable {


    private static final long serialVersionUID = -6552787976881946465L;

    //    /**
//     * 类型：1-开单配货率 2-仓库发货率 3-最终下货率
//     */
//    private Integer rateType;
    @CsvCell(title = "日期", index = 1, fieldNo = 1)
    private String showTime;//根据不同维度展示不同，可能为天，可能为年月，可能为周次

    @CsvCell(title = "过账数量", index = 3, fieldNo = 1)
    private BigDecimal upQuantity;//分子数量：开单数量、过账数量

    @CsvCell(title = "订单原数量", index = 4, fieldNo = 1)
    private BigDecimal downQuantity;//分母数量：订单数量、开单数量
    @CsvCell(title = "出库原数量", index = 2, fieldNo = 1)
    private BigDecimal rawQuantity; //出库原数量



    /**
     * 数量满足率
     */
    private BigDecimal quantityRate;

    @CsvCell(title = "数量下货率", index = 5, fieldNo = 1)
    private String quantityRateStr;

    public String getQuantityRateStr() {
        String res = "";
        if(quantityRate!=null){
            res = quantityRate.setScale(2, RoundingMode.HALF_UP).toPlainString() + "%";
        }
        return res;
    }

    @CsvCell(title = "过账金额", index = 6, fieldNo = 1)
    private BigDecimal upAmount;//分子金额：开单金额、配送金额

    @CsvCell(title = "订单原金额", index = 7, fieldNo = 1)
    private BigDecimal downAmount;//分母金额：订单金额、开单金额

    @CsvCell(title = "出库原金额", index = 5, fieldNo = 1)
    private BigDecimal rawAmount; //出库原金额

    /**
     * 金额满足率
     */
    private BigDecimal amountRate;

    @CsvCell(title = "金额下货率", index = 8, fieldNo = 1)
    private String amountRateStr;

    public String getAmountRateStr() {
        String res = "";
        if(amountRate!=null){
            res = amountRate.setScale(2, RoundingMode.HALF_UP).toPlainString() + "%";
        }
        return res;
    }
    @CsvCell(title = "过账品项", index = 10, fieldNo = 1)
    private BigDecimal upProductNum;//分子品项数：开单品项、过账品项
    @CsvCell(title = "订单原品项", index = 11, fieldNo = 1)
    private BigDecimal downProductNum;//分母品项数：订单品项、开单品项
    @CsvCell(title = "出库原品项", index = 9, fieldNo = 1)
    private BigDecimal rawProductNum; //出库原品项

    /**
     * 品项满足率
     */
    private BigDecimal productRate;

    @CsvCell(title = "品项下货率", index = 12, fieldNo = 1)
    private String productRateStr;

    public String getProductRateStr() {
        String res = "";
        if(productRate!=null){
            res = productRate.setScale(2, RoundingMode.HALF_UP).toPlainString() + "%";
        }
        return res;
    }

    private BigDecimal averageUpProductNum;//分子品项数：开单品项、过账品项,此处汇总所有加盟商维度的总计

    private BigDecimal averageDownProductNum;//分母品项数：订单品项、开单品项,此处汇总所有加盟商维度的总计
    /**
     * 品项行业平均值---统计全表的数据进行汇总计算平均值
     */
    private BigDecimal averageRate;

    @CsvCell(title = "品项平均下货率", index = 13, fieldNo = 1)
    private String averageRateStr;

    public String getAverageRateStr() {
        String res = "";
        if(averageRate!=null){
            res = averageRate.setScale(2, RoundingMode.HALF_UP).toPlainString() + "%";
        }
        return res;
    }

    /**
     * 对比
     */
    private BigDecimal diff;

    @CsvCell(title = "对比", index = 15, fieldNo = 1)
    private String diffStr;

    public String getDiffStr() {
        String res = "";
        if(diff!=null){
            res = diff.setScale(2, RoundingMode.HALF_UP).toPlainString() + "%";
        }
        return res;
    }

    //行业平均值
    private BigDecimal industryAverage;
    @CsvCell(title = "品项行业平均下货率", index = 14, fieldNo = 1)
    private BigDecimal industryAverageStr;

    public String getIndustryAverageStr(){
        String res = "";
        if(industryAverage!=null){
            res = industryAverage.setScale(2, RoundingMode.HALF_UP).toPlainString() + "%";
        }
        return res;
    }

    private BigDecimal count;
}
