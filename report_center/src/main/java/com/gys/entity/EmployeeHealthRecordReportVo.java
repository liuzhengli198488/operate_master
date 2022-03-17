package com.gys.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
public class EmployeeHealthRecordReportVo implements Serializable {

    /**
     * 档案编号
     */
    @ExcelProperty("档案编号")
    private String voucherId;

    /**
     * 工号
     */
    @ExcelProperty("员工编号")
    private String empId;

    /**
     * 员工姓名
     */
    @ExcelProperty("员工姓名")
    private String empName;

    /**
     * 检查日期
     */
    @ExcelProperty("检查日期")
    private String checkDate;

    /**
     * 检查机构
     */
    @ExcelProperty("检查机构")
    private String checkOrg;

    /**
     * 检查项目
     */
    @ExcelProperty("检查项目")
    private String checkItem;

    /**
     * 检查结果
     */
    @ExcelProperty("检查结果")
    private String checkResult;

    /**
     * 采取措施
     */
    @ExcelProperty("采取措施")
    private String measures;

    /**
     * 备注
     */
    @ExcelProperty("备注")
    private String remark;

    /**
     * 性别 0-男 1-女
     */
    @ExcelProperty("性别")
    private String empGender;

    /**
     * 职务
     */
    @ExcelProperty("职务")
    private String empPosition;

    /**
     * 入司日期
     */
    @ExcelProperty("入司日期")
    private String joinDate;


    /**
     * 出生日期
     */
    @ExcelProperty("出生日期")
    private String empBirthday;

    /**
     * 岗位
     */
    @ExcelProperty("岗位")
    private String empPost;

    /**
     * 职称
     */
    @ExcelProperty("职称")
    private String empJob;

    /**
     * 部门名称
     */
    @ExcelProperty("部门名称")
    private String depName;

    /**
     * 组别名称
     */
    @ExcelProperty("组别名称")
    private String groupName;

    /**
     * 创建者
     */
    @ExcelProperty("建档人")
    private String createUser;

    /**
     * 创建时间
     */
    @ExcelProperty("建档时间")
    private Date createTime;
}