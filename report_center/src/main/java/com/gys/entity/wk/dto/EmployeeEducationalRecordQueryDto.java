package com.gys.entity.wk.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * GAIA_EMPLOYEE_EDUCATIONAL_RECORD
 * @author 
 */
@Data
public class EmployeeEducationalRecordQueryDto implements Serializable {

    /**
     * 加盟商
     */
    private String client;

    /**
     * 门店编码
     */
    private List<String> storeIds;

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
     * 起开始日期
     */
    private Date beginStartDate;

    /**
     * 止开始日期
     */
    private Date endStartDate;

    /**
     * 结束日期
     */
    private Date endDate;

    /**
     * 起结束日期
     */
    private Date beginEndDate;

    /**
     * 止结束日期
     */
    private Date endEndDate;

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
     * 页数
     */
    private Integer pageNum;
    /**
     *  页大小
     */
    private Integer pageSize;

}