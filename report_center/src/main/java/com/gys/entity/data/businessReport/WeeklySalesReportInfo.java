package com.gys.entity.data.businessReport;

import com.gys.util.csv.annotation.CsvCell;
import com.gys.util.csv.annotation.CsvRow;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 单店日均
 * @author XiaoZY
 * @date 2021/4/1
 */

/**
 * 1,2,3,4,5      ->0
 * 1,2,5      ->6
 * 1,2,3,4      ->7
 * 1,2,4,5     ->8
 * 1,3,4,5     ->9
 *
 *
 *
 *
 */

@Data
@CsvRow("销售周报")
public class WeeklySalesReportInfo {
    @CsvCell(title = "客户ID", index = 1, fieldNo = 6)
    @ApiModelProperty(value = "客户Id")
    private String clientId;
    @CsvCell(title = "客户名称", index = 2, fieldNo = 6)
    @ApiModelProperty(value = "客户名称")
    private String francName;
    @CsvCell(title = "门店编码", index = 3, fieldNo = 2)
    @ApiModelProperty(value = "门店编码")
    private String gssdBrId;
    @CsvCell(title = "门店名称", index = 4, fieldNo = 2)
    @ApiModelProperty(value = "门店名称")
    private String stoName;
    @CsvCell(title = "省", index = 5, fieldNo = 11)
    @ApiModelProperty(value = "省code")
    private String province;
    @CsvCell(title = "省", index = 6, fieldNo = 3)
    @ApiModelProperty(value = "省name")
    private String provinceName;
    @CsvCell(title = "省", index = 7, fieldNo = 11)
    @ApiModelProperty(value = "市code")
    private String city;
    @CsvCell(title = "市", index = 8, fieldNo = 4)
    @ApiModelProperty(value = "市name")
    private String cityName;
    @CsvCell(title = "时间", index = 9, fieldNo = 7)
    @ApiModelProperty(value = "年周")
    private String weekNumber;
    @CsvCell(title = "省", index = 2, fieldNo = 11)
    @ApiModelProperty(value = "周")
    private String w;
    @CsvCell(title = "省", index = 2, fieldNo = 11)
    @ApiModelProperty(value = "年")
    private String y;
    @CsvCell(title = "省", index = 2, fieldNo = 11)
    @ApiModelProperty(value = "月")
    private String m;
    @CsvCell(title = "省", index = 2, fieldNo = 11)
    @ApiModelProperty(value = "最小日期")
    private String minDate;
    @CsvCell(title = "省", index = 2, fieldNo = 11)
    @ApiModelProperty(value = "最大日期")
    private String maxDate;
    @CsvCell(title = "动销门店数", index = 10, fieldNo = 9)
    @ApiModelProperty(value = "动销门店数")
    private Double salesStoresQty;
    @CsvCell(title = "动销门店数环比", index = 11, fieldNo = 9)
    @ApiModelProperty(value = "动销门店数环比")
    private Double salesStoresQtyHB;
    @CsvCell(title = "动销门店数同比", index = 12, fieldNo = 9)
    @ApiModelProperty(value = "动销门店数同比")
    private Double salesStoresQtyTB;
    @CsvCell(title = "动销天数", index = 13, fieldNo = 9)
    @ApiModelProperty(value = "动销天数")
    private Double salesDayQty;
    @CsvCell(title = "动销天数环比", index = 14, fieldNo = 9)
    @ApiModelProperty(value = "动销天数环比")
    private Double salesDayQtyHB;
    @CsvCell(title = "动销天数同比", index = 15, fieldNo = 9)
    @ApiModelProperty(value = "动销天数同比")
    private Double salesDayQtyTB;
    @CsvCell(title = "销售总天数", index = 16, fieldNo = 8)
    @ApiModelProperty(value = "销售总天数")
    private Double totalSalesDay;
    @CsvCell(title = "销售总天数环比", index = 17, fieldNo = 8)
    @ApiModelProperty(value = "销售总天数环比")
    private Double totalSalesDayHB;
    @CsvCell(title = "销售总天数同比", index = 18, fieldNo = 8)
    @ApiModelProperty(value = "销售总天数同比")
    private Double totalSalesDayTB;
    @CsvCell(title = "销售额", index = 19, fieldNo = 0)
    @ApiModelProperty(value = "销售额")
    private Double salesAmt;
    @CsvCell(title = "销售额环比", index = 20, fieldNo = 0)
    @ApiModelProperty(value = "销售额环比")
    private Double salesAmtHB;
    @CsvCell(title = "销售额环比率", index = 21, fieldNo = 0)
    @ApiModelProperty(value = "销售额环比率")
    private Double salesAmtHBRate;
    @CsvCell(title = "销售额同比", index = 22, fieldNo = 0)
    @ApiModelProperty(value = "销售额同比")
    private Double salesAmtTB;
    @CsvCell(title = "销售额同比率", index = 23, fieldNo = 0)
    @ApiModelProperty(value = "销售额同比率")
    private Double salesAmtTBRate;
    @CsvCell(title = "日均销售额", index = 24, fieldNo = 7)
    @ApiModelProperty(value = "日均销售额")
    private Double averageDailySalesAmt;
    @CsvCell(title = "日均销售额环比", index = 25, fieldNo = 7)
    @ApiModelProperty(value = "日均销售额环比")
    private Double averageDailySalesAmtHB;
    @CsvCell(title = "日均销售额同比", index = 26, fieldNo = 7)
    @ApiModelProperty(value = "日均销售额同比")
    private Double averageDailySalesAmtTB;
    @ApiModelProperty(value = "毛利额")
    @CsvCell(title = "毛利额", index = 27, fieldNo = 0)
    private Double gross;
    @CsvCell(title = "毛利额环比", index = 28, fieldNo = 0)
    @ApiModelProperty(value = "毛利额环比")
    private Double grossHB;
    @CsvCell(title = "毛利额环比率", index = 29, fieldNo = 0)
    @ApiModelProperty(value = "毛利额环比率")
    private Double grossHBRate;
    @CsvCell(title = "毛利额同比", index = 30, fieldNo = 0)
    @ApiModelProperty(value = "毛利额同比")
    private Double grossTB;
    @CsvCell(title = "毛利额同比率", index = 31, fieldNo = 0)
    @ApiModelProperty(value = "毛利额同比率")
    private Double grossTBRate;
    @CsvCell(title = "日均毛利额", index = 32, fieldNo = 7)
    @ApiModelProperty(value = "日均毛利额")
    private Double averageDailyGross;
    @CsvCell(title = "日均毛利额环比", index = 33, fieldNo = 7)
    @ApiModelProperty(value = "日均毛利额环比")
    private Double averageDailyGrossHB;
    @CsvCell(title = "日均毛利额同比", index = 34, fieldNo = 7)
    @ApiModelProperty(value = "日均毛利额同比")
    private Double averageDailyGrossTB;
    @CsvCell(title = "毛利率", index = 35, fieldNo = 0)
    @ApiModelProperty(value = "毛利率")
    private Double grossRate;
    @CsvCell(title = "毛利率环比", index = 36, fieldNo = 0)
    @ApiModelProperty(value = "毛利率环比")
    private Double grossRateHB;
    @CsvCell(title = "毛利率同比", index = 37, fieldNo = 0)
    @ApiModelProperty(value = "毛利率同比")
    private Double grossRateTB;
    @CsvCell(title = "日均交易次数", index = 38, fieldNo = 0)
    @ApiModelProperty(value = "日均交易次数")
    private Double averageDailyTransactionQty;
    @CsvCell(title = "日均交易次数环比", index = 39, fieldNo = 0)
    @ApiModelProperty(value = "日均交易次数环比")
    private Double averageDailyTransactionQtyHB;
    @CsvCell(title = "日均交易次数同比", index = 40, fieldNo = 0)
    @ApiModelProperty(value = "日均交易次数同比")
    private Double averageDailyTransactionQtyTB;
    @CsvCell(title = "客单价", index = 41, fieldNo = 0)
    @ApiModelProperty(value = "客单价")
    private Double customerPrice;
    @CsvCell(title = "客单销量", index = 44, fieldNo = 5)
    @ApiModelProperty(value = "客单销量")
    private Double customerQty;
    @CsvCell(title = "客单销量环比", index = 45, fieldNo = 5)
    @ApiModelProperty(value = "客单销量环比")
    private Double customerQtyHB;
    @CsvCell(title = "客单销量同比", index = 46, fieldNo = 5)
    @ApiModelProperty(value = "客单销量同比")
    private Double customerQtyTB;
    @CsvCell(title = "客单价环比", index = 42, fieldNo = 0)
    @ApiModelProperty(value = "客单价环比")
    private Double customerPriceHB;
    @CsvCell(title = "客单价同比", index = 43, fieldNo = 0)
    @ApiModelProperty(value = "客单价同比")
    private Double customerPriceTB;
//    @CsvCell(title = "医保销售额", index = 47, fieldNo = 7)
    @ApiModelProperty(value = "医保销售额")
    private Double medicalSalesAmt;
    @CsvCell(title = "医保销售额环比", index = 48, fieldNo = 7)
    @ApiModelProperty(value = "医保销售额环比")
    private Double medicalSalesAmtHB;
    @CsvCell(title = "医保销售额同比", index = 49, fieldNo = 7)
    @ApiModelProperty(value = "医保销售额同比")
    private Double medicalSalesAmtTB;
    @CsvCell(title = "医保销售占比", index = 50, fieldNo = 7)
    @ApiModelProperty(value = "医保销售占比")
    private Double medicalSalesAmtRate;
    @CsvCell(title = "医保销售占比环比", index = 51, fieldNo = 7)
    @ApiModelProperty(value = "医保销售占比环比")
    private Double medicalSalesAmtRateHB;
    @CsvCell(title = "医保销售占比同比", index = 52, fieldNo = 7)
    @ApiModelProperty(value = "医保销售占比同比")
    private Double medicalSalesAmtRateTB;
//    @CsvCell(title = "医保毛利额", index = 53, fieldNo = 7)
    @ApiModelProperty(value = "医保毛利额")
    private Double medicalGross;
//    @CsvCell(title = "医保毛利额环比", index = 54, fieldNo = 7)
    @ApiModelProperty(value = "医保毛利额环比")
    private Double medicalGrossHB;
//    @CsvCell(title = "医保毛利额同比", index = 55, fieldNo = 7)
    @ApiModelProperty(value = "医保毛利额同比")
    private Double medicalGrossTB;
//    @CsvCell(title = "医保毛利占比", index = 56, fieldNo = 7)
    @ApiModelProperty(value = "医保毛利占比")
    private Double medicalGrossPercentage;
//    @CsvCell(title = "医保毛利占比环比", index = 57, fieldNo = 7)
    @ApiModelProperty(value = "医保毛利占比环比")
    private Double medicalGrossPercentageHB;
//    @CsvCell(title = "医保毛利占比同比", index = 58, fieldNo = 7)
    @ApiModelProperty(value = "医保毛利占比同比")
    private Double medicalGrossPercentageTB;
//    @CsvCell(title = "医保毛利率", index = 59, fieldNo = 7)
    @ApiModelProperty(value = "医保毛利率")
    private Double medicalGrossRate;
//    @CsvCell(title = "医保毛利率环比", index = 60, fieldNo = 7)
    @ApiModelProperty(value = "医保毛利率环比")
    private Double medicalGrossRateHB;
//    @CsvCell(title = "医保毛利率同比", index = 61, fieldNo = 7)
    @ApiModelProperty(value = "医保毛利率同比")
    private Double medicalGrossRateTB;
    @CsvCell(title = "关联手动弹出次数", index = 62, fieldNo = 7)
    @ApiModelProperty(value = "关联手动弹出次数")
    private Double manualQty;
    @CsvCell(title = "关联自动弹出次数", index = 63, fieldNo = 7)
    @ApiModelProperty(value = "关联自动弹出次数")
    private Double autoQty;
    @CsvCell(title = "关联总弹出次数", index = 64, fieldNo = 7)
    @ApiModelProperty(value = "关联总弹出次数")
    private Double totalQty;
//    @CsvCell(title = "成功关联次数", index = 65, fieldNo = 7)
    @ApiModelProperty(value = "成功关联次数")
    private Double successQty;
    @CsvCell(title = "关联率", index = 66, fieldNo = 7)
    @ApiModelProperty(value = "关联率")
    private Double associateRate;
    @CsvCell(title = "关联销售额", index = 67, fieldNo = 7)
    @ApiModelProperty(value = "关联销售额")
    private Double associateSalesAmt;
    @CsvCell(title = "关联销售额环比", index = 68, fieldNo = 7)
    @ApiModelProperty(value = "关联销售额环比")
    private Double associateSalesAmtHB;
    @CsvCell(title = "关联销售额同比", index = 69, fieldNo = 7)
    @ApiModelProperty(value = "关联销售额同比")
    private Double associateSalesAmtTB;
    @CsvCell(title = "关联销售占比", index = 70, fieldNo = 7)
    @ApiModelProperty(value = "关联销售占比")
    private Double associateSalesRate;
    @CsvCell(title = "关联销售占比环比", index = 71, fieldNo = 7)
    @ApiModelProperty(value = "关联销售占比环比")
    private Double associateSalesRateHB;
    @CsvCell(title = "关联销售占比同比", index = 72, fieldNo = 7)
    @ApiModelProperty(value = "关联销售占比同比")
    private Double associateSalesRateTB;
    @CsvCell(title = "关联毛利额", index = 73, fieldNo = 7)
    @ApiModelProperty(value = "关联毛利额")
    private Double associateGross;
    @CsvCell(title = "关联毛利额环比", index = 74, fieldNo = 7)
    @ApiModelProperty(value = "关联毛利额环比")
    private Double associateGrossHB;
    @CsvCell(title = "关联毛利额同比", index = 75, fieldNo = 7)
    @ApiModelProperty(value = "关联毛利额同比")
    private Double associateGrossTB;
    @CsvCell(title = "关联毛利占比", index = 76, fieldNo = 7)
    @ApiModelProperty(value = "关联毛利占比")
    private Double associateGrossPercentage;
    @CsvCell(title = "关联毛利占比环比", index = 77, fieldNo = 7)
    @ApiModelProperty(value = "关联毛利占比环比")
    private Double associateGrossPercentageHB;
    @CsvCell(title = "关联毛利占比同比", index = 78, fieldNo = 7)
    @ApiModelProperty(value = "关联毛利占比同比")
    private Double associateGrossPercentageTB;
    @CsvCell(title = "关联毛利率", index = 79, fieldNo = 7)
    @ApiModelProperty(value = "关联毛利率")
    private Double associateGrossRate;
    @CsvCell(title = "关联毛利率环比", index = 80, fieldNo = 7)
    @ApiModelProperty(value = "关联毛利率环比")
    private Double associateGrossRateHB;
    @CsvCell(title = "关联毛利率同比", index = 81, fieldNo = 7)
    @ApiModelProperty(value = "关联毛利率同比")
    private Double associateGrossRateTB;
    @CsvCell(title = "总销售次数", index = 82, fieldNo = 5)
    @ApiModelProperty(value = "总销售次数")
    private Double totalBillCount;
    @CsvCell(title = "总销售次数环比", index = 83, fieldNo = 5)
    @ApiModelProperty(value = "总销售次数环比")
    private Double totalBillCountHB;
    @CsvCell(title = "总销售次数同比", index = 84, fieldNo = 5)
    @ApiModelProperty(value = "总销售次数同比")
    private Double totalBillCountTB;
    @CsvCell(title = "成功关联次数", index = 65, fieldNo = 7)
    @ApiModelProperty(value = "成功关联次数")
    private Double successAssociate;
    @CsvCell(title = "大类", index = 5, fieldNo = 5)
    @ApiModelProperty(value = "大类名称")
    private String proCompBigName;
    @CsvCell(title = "大类Code", index = 86, fieldNo = 11)
    @ApiModelProperty(value = "大类Code")
    private String proCompBigCode;
    @CsvCell(title = "动销品项", index = 87, fieldNo = 5)
    @ApiModelProperty(value = "动销品项")
    private Double salesGoodsQty;
    @CsvCell(title = "动销品项环比", index = 88, fieldNo = 5)
    @ApiModelProperty(value = "动销品项环比")
    private Double salesGoodsQtyHB;
    @CsvCell(title = "动销品项同比", index = 89, fieldNo = 5)
    @ApiModelProperty(value = "动销品项同比")
    private Double salesGoodsQtyTB;
    @CsvCell(title = "销售成本额", index = 90, fieldNo = 11)
    @ApiModelProperty(value = "销售成本额")
    private Double salesCost;
    @CsvCell(title = "库存品项", index = 91, fieldNo = 5)
    @ApiModelProperty(value = "库存品项")
    private Double inventoryItemQty;
    @CsvCell(title = "库存品项环比", index = 92, fieldNo = 11)
    @ApiModelProperty(value = "库存品项环比")
    private Double inventoryItemQtyHB;
    @CsvCell(title = "库存品项同比", index = 93, fieldNo = 11)
    @ApiModelProperty(value = "库存品项同比")
    private Double inventoryItemQtyTB;
    @CsvCell(title = "单品平均销售价", index = 94, fieldNo = 5)
    @ApiModelProperty(value = "单品平均销售价")
    private Double singleAveragePrice;
    @CsvCell(title = "单品平均销售价环比", index = 95, fieldNo = 5)
    @ApiModelProperty(value = "单品平均销售价环比")
    private Double singleAveragePriceHB;
    @CsvCell(title = "单品平均销售价同比", index = 96, fieldNo = 5)
    @ApiModelProperty(value = "单品平均销售价同比")
    private Double singleAveragePriceTB;
    @CsvCell(title = "销量", index = 97, fieldNo = 5)
    @ApiModelProperty(value = "销量")
    private Double salesQty;
    @CsvCell(title = "销量环比", index = 98, fieldNo = 5)
    @ApiModelProperty(value = "销量环比")
    private Double salesQtyHB;
    @CsvCell(title = "销量同比", index = 99, fieldNo = 5)
    @ApiModelProperty(value = "销量同比")
    private Double salesQtyTB;
    @CsvCell(title = "毛利贡献率", index = 100, fieldNo = 5)
    @ApiModelProperty(value = "毛利贡献率")
    private Double grossProfitRate;
    @CsvCell(title = "毛利贡献率环比", index = 101, fieldNo = 5)
    @ApiModelProperty(value = "毛利贡献率环比")
    private Double grossProfitRateHB;
    @CsvCell(title = "毛利贡献率同比", index = 102, fieldNo = 5)
    @ApiModelProperty(value = "毛利贡献率同比")
    private Double grossProfitRateTB;
    @CsvCell(title = "缺货品项预估毛利额", index = 103, fieldNo = 5)
    @ApiModelProperty(value = "缺货品项预估毛利额")
    private Double HYGGross;
    @CsvCell(title = "缺货品项预估销售额", index = 104, fieldNo = 5)
    @ApiModelProperty(value = "缺货品项预估销售额")
    private Double HYGSalesAmt;
    @CsvCell(title = "缺货品项数", index = 105, fieldNo = 5)
    @ApiModelProperty(value = "缺货品项数")
    private Double HQty1;
    @CsvCell(title = "库存总量", index = 106, fieldNo = 11)
    @ApiModelProperty(value = "库存总量")
    private Double inventoryQty;
    @CsvCell(title = "库存成本", index = 107, fieldNo = 5)
    @ApiModelProperty(value = "库存成本")
    private Double inventoryAmt;
    @CsvCell(title = "动销率", index = 108, fieldNo = 5)
    @ApiModelProperty(value = "动销率")
    private Double movableSalesRate;
    @CsvCell(title = "总销售额", index = 109, fieldNo = 11)
    @ApiModelProperty(value = "总销售额")
    private Double totalSalesAmt;
    @CsvCell(title = "销售占比", index = 110, fieldNo = 5)
    @ApiModelProperty(value = "销售占比")
    private Double salesRate;
    @CsvCell(title = "销售占比环比", index = 111, fieldNo = 5)
    @ApiModelProperty(value = "销售占比环比")
    private Double salesRateHB;
    @CsvCell(title = "销售占比同比", index = 112, fieldNo = 5)
    @ApiModelProperty(value = "销售占比同比")
    private Double salesRateTB;
    @CsvCell(title = "交易品项总数", index = 113, fieldNo = 11)
    @ApiModelProperty(value = "交易品项总数")
    private Double salesItemQty;
    @CsvCell(title = "客单品项数", index = 114, fieldNo = 5)
    @ApiModelProperty(value = "客单品项数")
    private Double customerItemQty;
    @CsvCell(title = "客单品项数环比", index = 115, fieldNo = 5)
    @ApiModelProperty(value = "客单品项数环比")
    private Double customerItemQtyHB;
    @CsvCell(title = "客单品项数同比", index = 116, fieldNo = 5)
    @ApiModelProperty(value = "客单品项数同比")
    private Double customerItemQtyTB;
    @CsvCell(title = "周转天数", index = 117, fieldNo = 5)
    @ApiModelProperty(value = "周转天数")
    private Double turnoverDays;
}
