package com.gys.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * GAIA_EMPLOYEE_HEALTH_RECORD
 */
@Data
public class GaiaEmployeeHealthRecord implements Serializable {
    private Long id;

    /**
     * 档案编号
     */
    private String voucherId;

    /**
     * 加盟商
     */
    private String client;

    /**
     * 工号
     */
    private String empId;

    /**
     * 员工姓名
     */
    private String empName;

    /**
     * 检查日期
     */
    private String checkDate;

    /**
     * 检查机构
     */
    private String checkOrg;

    /**
     * 检查项目
     */
    private String checkItem;

    /**
     * 检查结果
     */
    private String checkResult;

    /**
     * 采取措施
     */
    private String measures;

    /**
     * 性别
     */
    private String empGender;

    /**
     * 出生日期
     */
    private String empBirthday;

    /**
     * 入司日期
     */
    private String joinDate;

    /**
     * 职务
     */
    private String empPosition;

    /**
     * 岗位
     */
    private String empPost;

    /**
     * 职称
     */
    private String empJob;

    /**
     * 部门名称
     */
    private String depName;

    /**
     * 组别名称
     */
    private String groupName;

    /**
     * 是否删除：0-正常 1-删除
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

    private static final long serialVersionUID = 1L;
}