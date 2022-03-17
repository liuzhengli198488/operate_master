package com.gys.entity.data.suggestion.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Auther: tzh
 * @Date: 2022/1/12 15:08
 * @Description: AdjustmentSummaryVo
 * @Version 1.0.0
 */
@Data
public class AdjustmentSummaryVo {
    /**
     * 调剂时间
     */
    @ExcelProperty(value = "门店调剂建议时间",index = 0)
    private String date;
    /**
     * 省份名称
     */
    @ExcelProperty(value = "省",index = 1)
    private String  provinceName;

    /**
     * 城市名称
     */
    @ExcelProperty(value = "市",index = 2)
    private String  cityName;

    /**
     * 客户id
     */
    @ExcelProperty(value = "客户",index = 3)
    private  String clientId;


    /**
     * 客户名称
     */
    @ExcelProperty(value = "客户名称",index = 4)
    private  String clientName;

    /**
     * 调出门店数
     */
    @ExcelProperty(value = "调出门店数",index = 5)
    private  Integer callOutStoresNumber;

    /**
     * 关联一家门店数量
     */
    @ExcelProperty(value = "关联1家门店数",index = 6)
    private Integer associateOneStore;

    /**
     * 关联二家门店数量
     */
    @ExcelProperty(value = "关联2家门店数",index = 7)
    private Integer associateTwoStore;

    /**
     * 关联三家门店数量
     */
    @ExcelProperty(value = "关联3家门店数",index = 8)
    private Integer associateThreeStore;

    /**
     * 调入门店反馈门店数
     */
    @ExcelProperty(value = "调入门店反馈门店数",index = 9)
    private Integer adjustFeedbackStores;

    /**
     *调出门店调剂确认前导出次数
     */
    @ExcelProperty(value = "调出门店调剂确认前导出次数",index = 10)
    private  Integer beforeCallOutConfirmationStoreExport;

    /**
     *调出门店调剂确认后导出次数
     */
    @ExcelProperty(value = "调出门店调剂确认后导出次数",index = 11)
    private Integer afterCallOutConfirmationStoreExport;


    /**
     *调入门店调剂确认前导出次数
     */
    @ExcelProperty(value = "调入门店调剂确认前导出次数",index = 12)
    private  Integer beforeCallInConfirmationStoreExport;

    /**
     *调入门店调剂确认后导出次数
     */
    @ExcelProperty(value = "调入门店调剂确认后导出次数",index = 13)
    private Integer afterCallInConfirmationStoreExport;

    /**
     * 门店调剂品项数
     */
    @ExcelProperty(value = "门店调剂品项数",index = 14)
    private Integer storeItem;

    /**
     * 门店调剂实际品项数
     */
    @ExcelProperty(value = "门店调剂实际品项数",index = 15)
   private  Integer storeAdjustActualItem;

    /**
     * 调剂满足率
     */
    @ExcelProperty(value = "调剂满足率",index = 16)
    private BigDecimal adjustmentSatisfactionRate;

    /**
     * 调出门店完成门店数
     */
    @ExcelProperty(value = "调出门店完成门店数",index = 17)
    private Integer callOutCompleteStore;

    public AdjustmentSummaryVo() {
            this.setAssociateOneStore(0);
            this.setAssociateTwoStore(0);
            this.setAssociateThreeStore(0);
    }
}
