package com.gys.entity.wk.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import java.util.Date;
import java.util.List;

/**
 * @Auther: tzh
 * @Date: 2021/12/30 11:08
 * @Description: MedicinalQualityRecordDto
 * @Version 1.0.0
 */
@Data
public class MedicinalQualityRecordUpdateDto {
    /**
     * 主键
     */
    private Long id;



    /**
     * 商品编码
     */
    private String proSelfCode;

    /**
     * 商品规格
     */
    private String proSpec;

    /**
     * 商品单位
     */
    private String proUnit;

    /**
     * 剂量单位
     */
    private String proDosage;

    /**
     * 生产厂家
     */
    private String factoryName;

    /**
     * 商品名称
     */
    private String proName;

    /**
     * 存储条件
     */
    private String proStorage;

    /**
     * 功能主治
     */
    @Max(value = 100,message = "功能主治字段不能超过100字节！")
    private String functionAttend;

    /**
     * 化学名
     */
    private String chemicalName;

    /**
     * 批准文号
     */
    private String approvalNo;

    /**
     * 说明书
     */
    private String proAccount;

    /**
     * 质量情况
     */
    private String qualityRemark;

    /**
     * 包装情况
     */
    private String proPackage;

    /**
     * 质量查询
     */
    private String qualityQuery;

    /**
     * 商品类别
     */
    private String proCategory;

    /**
     * 有效期
     */
    private String proEffective;

    /**
     * 建档目的
     */
    private String purpose;

    /**
     * 首批进货日期
     */
    private String firstInDate;

    /**
     * 库存质量
     */
    private String inventQuality;

    /**
     * 质量状况
     */
    private String qualityCond;

    /**
     * 原因分析
     */
    private String cause;

    /**
     * 处理情况
     */
    private String handRemark;

    /**
     * 质量标准
     */
    private String qualityStand;

    /**
     * 产品批号
     */
    private String batchNo;


    /**
     * 备注
     */
    private String remark;


    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 更新者
     */
    private String updateUser;


}
