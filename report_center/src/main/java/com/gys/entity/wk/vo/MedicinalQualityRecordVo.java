package com.gys.entity.wk.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import java.util.Date;

/**
 * @Auther: tzh
 * @Date: 2021/12/30 11:08
 * @Description: MedicinalQualityRecordDto
 * @Version 1.0.0
 */
@Data
public class MedicinalQualityRecordVo {
    /**
     * 编号
     */
    @ExcelProperty(value = "编号",index = 0)
    private String voucherId;


    /**
     * 创建者
     */
    @ExcelProperty(value = "制单人",index = 1)
    private String createUser;

    /**
     * 商品编码
     */
    @ExcelProperty(value = "商品货号",index = 2)
    private String proSelfCode;

    /**
     * 商品名称
     */
    @ExcelProperty(value = "商品名称",index = 3)
    private String proName;

    /**
     * 商品规格
     */
    @ExcelProperty(value = "规格",index = 4)
    private String proSpec;

    /**
     * 商品单位
     */
    @ExcelProperty(value = "单位",index = 5)
    private String proUnit;

    /**
     * 剂量单位
     */
    @ExcelProperty(value = "剂型",index =6)
    private String proDosage;


    /**
     * 生产厂家
     */
    @ExcelProperty(value = "生产厂家",index = 7)
    private String factoryName;


    /**
     * 批准文号
     */
    @ExcelProperty(value = "批准文号",index =8)
    private String approvalNo;

    /**
     * 存储条件
     */
    @ExcelProperty(value = "存储条件",index = 9)
    private String proStorage;

    /**
     * 功能主治
     */

    @ExcelProperty(value = "功能主治",index =10)
    private String functionAttend;

    /**
     * 化学名
     */
    @ExcelProperty(value = "化学名",index = 11)
    private String chemicalName;

    /**
     * 商品类别
     */
    @ExcelProperty(value = "商品类别",index = 12)
    private String proCategory;


    /**
     * 创建时间
     */
    @ExcelProperty(value = "填表日期",index = 13)
    private String createTime;


    /**
     * 说明书
     */
    @ExcelProperty(value = "说明书",index = 14)
    private String proAccount;




    /**
     * 质量情况
     */
    @ExcelProperty(value = "质量情况",index = 15)
    private String qualityRemark;

    /**
     * 包装情况
     */
    @ExcelProperty(value = "包装情况",index = 16)
    private String proPackage;

    /**
     * 质量查询
     */
    @ExcelProperty(value = "质量查询",index = 17)
    private String qualityQuery;


    /**
     * 库存质量
     */
    @ExcelProperty(value = "库存质量",index = 18)
    private String inventQuality;


    /**
     * 有效期
     */
    @ExcelProperty(value = "有效期",index = 19)
    private String proEffective;

    /**
     * 建档目的
     */
    @ExcelProperty(value = "建档目的",index = 20)
    private String purpose;

    /**
     * 首批进货日期
     */
    @ExcelProperty(value = "首批进货日期",index = 21)
    private String firstInDate;

    /**
     * 质量状况
     */
    @ExcelProperty(value = "质量状况",index = 22)
    private String qualityCond;


    /**
     * 原因分析
     */
    @ExcelProperty(value = "原因分析",index = 23)
    private String cause;

    /**
     * 处理情况
     */
    @ExcelProperty(value = "处理情况",index = 24)
    private String handRemark;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注",index =25)
    private String remark;

    /**
     * 质量标准
     */
    @ExcelProperty(value = "质量标准",index = 26)
    private String qualityStand;



}
