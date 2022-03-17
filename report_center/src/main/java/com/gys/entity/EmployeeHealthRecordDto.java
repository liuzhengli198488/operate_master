package com.gys.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
public class EmployeeHealthRecordDto implements Serializable {

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
     *  开始时间
     */
    private String startDate;

    /**
     *  结束时间
     */
    private String endDate;

    private Integer pageNum;

    private Integer pageSize;

    /**
     *  开始时间
     */
    private Date queryStartDate;

    /**
     *  结束时间
     */
    private Date queryEndDate;

    private static final long serialVersionUID = 1L;
}