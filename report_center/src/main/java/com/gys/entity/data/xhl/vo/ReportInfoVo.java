package com.gys.entity.data.xhl.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Auther: tzh
 * @Date: 2021/12/12 20:51
 * @Description: ReportInfoVo
 * @Version 1.0.0
 */
@Data
public class ReportInfoVo {
    @ExcelProperty(value = "时间",index = 0)
    private  String  date;
    @ExcelProperty(value = "省",index = 1)
    private  String  province;
    @ExcelProperty(value = "省",index = 1)
    private  String  city;
    @ExcelProperty(value = "省",index = 1)
    private  String  client;
    @ExcelProperty(value = "省",index = 1)
    private  String  clientName;
    @ExcelProperty(value = "省",index = 1)
    private  String  stoCode;
    @ExcelProperty(value = "省",index = 1)
    private  String  stoName;
    @ExcelProperty(value = "省",index = 1)
    //原订单数量
    private  BigDecimal orderNum;
    //出库原数量
    @ExcelProperty(value = "省",index = 1)
    private  BigDecimal  dnNum;
    @ExcelProperty(value = "省",index = 1)
    private  BigDecimal  sendNum;
    @ExcelProperty(value = "省",index = 1)
    private  BigDecimal  gzNum;
    //过账数量
    @ExcelProperty(value = "省",index = 1)
    private BigDecimal finalNum;
    //     配货数量满足率
    @ExcelProperty(value = "省",index = 1)
    private  String   distributionNumRate;
    //     最终数量满足率
    @ExcelProperty(value = "省",index = 1)
    private  String  finalNumRate;
    //     发货数量满足率
    @ExcelProperty(value = "省",index = 1)
    private String sendNumRate;
    //出库原金额
    @ExcelProperty(value = "省",index = 1)
    private BigDecimal dnAmount;
    @ExcelProperty(value = "省",index = 1)
    private BigDecimal gzAmount;
    @ExcelProperty(value = "省",index = 1)
    private BigDecimal sendAmount;
    @ExcelProperty(value = "省",index = 1)
    // 过账金额
    private BigDecimal finalOrderAmount;
    // 原订单金额
    private BigDecimal orderAmount;
    // 配货金额满足率
    private String distributionAmountRate;
    // 发货金额满足率
    private String sendAmountRate;
    // 最终金额满足率
    private String finalAmountRate;
    // 出库原品项
    private BigDecimal dnProductNum;
    private BigDecimal gzProductNum;
    private BigDecimal sendProductNum;
    // 过账品相
    private BigDecimal finalProductNum;
    // 原订单品项
    private BigDecimal orderProductNum;
    // 配货品相满足率
    private String distributionProductRate;
    //发货品相满足率
    private String sendProductRate;
    // 最终品相满足率
    private String finalProductRate;
    // 品相平均下货率
    private BigDecimal averageRate;
    private String averageRateStr;
    // 行业品相满足率
    private BigDecimal  industryAverage;
    private String   industryAverageStr;
    // 对比 差异
    private String  difference;

    // 是否计入主动铺货
    private String tag;
    // 是否导出 Export
    private String  export;
    // 是否查看趋势图
    @ExcelProperty(value = "省",index = 1)
    private String check;
    // 查看方式
    @ExcelProperty(value = "省",index = 1)
    private String  viewSource;
    // 操作人员
    @ExcelProperty(value = "省",index = 1)
    private String operator;
}
