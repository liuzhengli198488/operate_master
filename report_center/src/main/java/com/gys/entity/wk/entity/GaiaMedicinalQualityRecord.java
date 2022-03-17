package com.gys.entity.wk.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * GAIA_MEDICINAL_QUALITY_RECORD  medicinalQuality
 * @author tzh
 */
@Data
@Table(name = "GAIA_MEDICINAL_QUALITY_RECORD")
public class GaiaMedicinalQualityRecord implements Serializable {
    /**
     * 主键
     */
    @Id
    private Long id;

    /**
     * 加盟商
     */
    private String client;

    /**
     * 编号
     */
    private String voucherId;

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
     * 状态：0-未审批 1-审批
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建者
     */
    private String createUser;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 更新者
     */
    private String updateUser;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标记：0-未删除 1-正常
     */
    private Integer isDelete;

    private static final long serialVersionUID = 1L;
}