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
public class ReportInfoExportVo {
    @ExcelProperty(value = "时间",index = 0)
    private  String  date;
    @ExcelProperty(value = "省",index = 1)
    private  String  province;
    @ExcelProperty(value = "市",index = 2)
    private  String  city;
    @ExcelProperty(value = "客户ID",index = 3)
    private  String  client;
    @ExcelProperty(value = "客户名称",index = 4)
    private  String  clientName;
    @ExcelProperty(value = "店号",index = 5)
    private  String  stoCode;
    @ExcelProperty(value = "店名",index = 6)
    private  String  stoName;

   /* //原订单数量
    private  BigDecimal orderNum;
    //出库原数量
    @ExcelProperty(value = "省",index = 1)
    private  BigDecimal  dnNum;
    @ExcelProperty(value = "省",index = 1)*/
   @ExcelProperty(value = "出库原数量",index = 7)
    private  BigDecimal  sendNum;
    @ExcelProperty(value = "过账原数量",index = 8)
    private  BigDecimal  gzNum;
    //过账数量
    @ExcelProperty(value = "原订单数量",index = 9)
    private BigDecimal finalNum;
    //     配货数量满足率
    @ExcelProperty(value = "配货数量下货率",index = 10)
    private  String  distributionNumRate;
    //     最终数量满足率
    @ExcelProperty(value = "最终数量下货率",index = 11)
    private  String  finalNumRate;
    //     发货数量满足率
    @ExcelProperty(value = "发货数量下货率",index = 12)
    private String sendNumRate;

    //出库原金额
 /*   @ExcelProperty(value = "省",index = 1)
    private BigDecimal dnAmount;*/
    @ExcelProperty(value = "出库原金额",index = 13)
    private BigDecimal sendAmount;
    @ExcelProperty(value = "过账原金额",index = 14)
    private BigDecimal gzAmount;

    @ExcelProperty(value = "原订单金额",index = 15)
    // 过账金额
    private BigDecimal finalOrderAmount;
   /* // 原订单金额
    private BigDecimal orderAmount;*/

    // 配货金额满足率
    @ExcelProperty(value = "配货金额下货率",index = 16)
    private String distributionAmountRate;
    // 发货金额满足率
    @ExcelProperty(value = "发货金额下货率",index = 17)
    private String sendAmountRate;
    // 最终金额满足率
    @ExcelProperty(value = "最终金额下货率",index = 18)
    private String finalAmountRate;
    @ExcelProperty(value = "出库原品项",index = 19)
    private BigDecimal sendProductNum;
    // 出库原品项
   // private BigDecimal dnProductNum;
    @ExcelProperty(value = "过账原品项",index = 20)
    private BigDecimal gzProductNum;


    // 过账品相
    @ExcelProperty(value = "原订单品项",index = 21)
    private BigDecimal finalProductNum;
    // 原订单品项
   // private BigDecimal orderProductNum;
    // 配货品相满足率
    @ExcelProperty(value = "配货品项下货率",index = 22)
    private String distributionProductRate;
    //发货品相满足率
    @ExcelProperty(value = "发货品项下货率",index = 23)
    private String sendProductRate;
    // 最终品相满足率
    @ExcelProperty(value = "最终品项下货率",index = 24)
    private String finalProductRate;
    // 品相平均下货率
    @ExcelProperty(value = "品项平均下货率",index = 25)
    private String averageRate;
    // 行业品相满足率
    @ExcelProperty(value = "行业品项下货率",index = 26)
    private String   industryAverageStr;
    // 对比 差异
    @ExcelProperty(value = "对比",index = 27)
    private String  difference;
    // 是否计入主动铺货
    @ExcelProperty(value = "是否计入主动铺货",index = 28)
    private String tag;
    // 是否导出 Export
    @ExcelProperty(value = "是否导出",index = 29)
    private String  export;
    // 是否查看趋势图
    @ExcelProperty(value = "是否查看趋势图",index = 30)
    private String check;
    // 查看方式
    @ExcelProperty(value = "查看方式",index = 31)
    private Integer viewSource;
    // 操作人员
    @ExcelProperty(value = "操作人员",index = 32)
    private String operator;
}
