package com.gys.entity.wk.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * GAIA_EMPLOYEE_EDUCATIONAL_RECORD
 * @author 
 */
@Data
public class GaiaEmployeeEducationalRecord implements Serializable {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 加盟商
     */
    private String client;

    /**
     * 编号
     */
    private String voucherId;

    /**
     * 门店编码
     */
    private String storeId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 员工工号
     */
    private String empId;

    /**
     * 员工姓名
     */
    private String empName;

    /**
     * 开始日期
     */
    private Date startDate;

    /**
     * 结束日期
     */
    private Date endDate;

    /**
     * 培训类别
     */
    private String trainCategory;

    /**
     * 培训类型
     */
    private String trainType;

    /**
     * 培训内容
     */
    private String trainContent;

    /**
     * 主办机构
     */
    private String hostOrg;

    /**
     * 培训教师
     */
    private String trainTeacher;

    /**
     * 培训地点
     */
    private String trainLocation;

    /**
     * 课时
     */
    private String classHour;

    /**
     * 培训分数
     */
    private BigDecimal score;

    /**
     * 员工岗位
     */
    private String empPost;

    /**
     * 员工职称
     */
    private String empJob;

    /**
     * 学历
     */
    private String educational;

    /**
     * 删除标记
     */
    private Integer isDelete;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建者
     */
    private String createUser;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 修改者
     */
    private String updateUser;

    private static final long serialVersionUID = 1L;
}