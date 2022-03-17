package com.gys.entity.data.commodityInventoryReport;

import com.gys.util.csv.annotation.CsvCell;
import com.gys.util.csv.annotation.CsvRow;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author ：liuzhiwen.
 * @Date ：Created in 20:11 2021/11/24
 * @Description：
 * @Modified By：liuzhiwen.
 * @Version:
 */
@Data
@CsvRow("商品调库汇总表")
public class CommoditySummaryOutData {
    /**
     * 商品调库时间
     */
    @CsvCell(title = "商品调库时间",index = 1,fieldNo = 1)
    private String billDate;

    /**
     * 省
     */
    @CsvCell(title = "省",index = 2,fieldNo = 1)
    private String prov;

    /**
     * 市
     */
    @CsvCell(title = "市",index = 3,fieldNo = 1)
    private String city;

    /**
     * 客户
     */
    @CsvCell(title = "客户",index = 4,fieldNo = 1)
    private String client;

    /**
     * 客户名称
     */
    @CsvCell(title = "客户名称",index = 5,fieldNo = 1)
    private String clientName;

    /**
     * 调库单号
     */
    @CsvCell(title = "调库单号",index = 6,fieldNo = 1)
    private String billCode;

    /**
     * 调库完成时间
     */
    @CsvCell(title = "调库完成时间",index = 7,fieldNo = 1)
    private String finishTime;

    /**
     * 调库次数
     */
    @CsvCell(title = "调库次数",index = 8,fieldNo = 1)
    private Integer pushNum;

    /**
     * 商品调库品项数
     */
    @CsvCell(title = "商品调库品项数",index = 9,fieldNo = 1)
    private Integer itemsQty;

    /**
     * 商品调库品次数
     */
    @CsvCell(title = "商品调库品次数",index = 10,fieldNo = 1)
    private Integer goodsQty;

    /**
     * 商品调库门店数
     */
    @CsvCell(title = "商品调库门店数",index = 11,fieldNo = 1)
    private Integer storeQty;

    /**
     * 建议调库成本额（万）
     */
    @CsvCell(title = "建议调库成本额（万）",index = 12,fieldNo = 1)
    private BigDecimal suggestReturnCost;

    /**
     * 实际调库成本额（万）
     */
    @CsvCell(title = "实际调库成本额（万）",index = 13,fieldNo = 1)
    private BigDecimal realReturnCost;

    /**
     * 单据状态
     */
    @CsvCell(title = "单据状态",index = 14,fieldNo = 1)
    private String status;

    /**
     * 按门店推送次数
     */
    @CsvCell(title = "按门店推送次数",index = 15,fieldNo = 1)
    private Integer pushStoreNum;

    /**
     * 按线路推送次数
     */
    @CsvCell(title = "按线路推送次数",index = 16,fieldNo = 1)
    private Integer pushRouteNum;

    /**
     *推送门店数
     */
    @CsvCell(title = "推送门店数",index = 17,fieldNo = 1)
    private Integer pushSuccessStoreNum;

    /**
     * 推送商品调库品次数
     */
    @CsvCell(title = "推送商品调库品次数",index = 18,fieldNo = 1)
    private Integer pushSuccessGoodsQty;

    /**
     * 是否导出
     */
    @CsvCell(title = "是否导出",index = 19,fieldNo = 1)
    private String isExport;
}
