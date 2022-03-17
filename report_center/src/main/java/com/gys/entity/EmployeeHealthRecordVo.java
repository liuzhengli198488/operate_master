package com.gys.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
public class EmployeeHealthRecordVo implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 档案编号
     */
    private String voucherId;

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
     * 备注
     */
    private String remark;

    /**
     * 性别 0-女 1-男
     */
    private String empGender;

    /**
     * 职务
     */
    private String empPosition;

    /**
     * 入司日期
     */
    private String joinDate;


    /**
     * 出生日期
     */
    private String empBirthday;

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
     * 创建者
     */
    private String createUser;

    /**
     * 创建时间
     */
    private Date createTime;
}