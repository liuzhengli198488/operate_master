package com.gys.entity.wk.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * GAIA_EMPLOYEE_EDUCATIONAL_RECORD
 * @author 
 */
@Data
public class EmployeeEducationalRecordVo implements Serializable {


    /**
     * 编号
     */
    @ExcelProperty(value = "编号")
    private String voucherId;

    /**
     * 门店名称
     */
    @ExcelProperty(value = "门店名称")
    private String storeName;

    /**
     * 员工工号
     */
    @ExcelProperty(value = "职员编号")
    private String empId;

    /**
     * 员工姓名
     */
    @ExcelProperty(value = "姓名")
    private String empName;

    /**
     * 开始日期
     */
    @ExcelProperty(value = "起始日期")
    private String  startDate;

    /**
     * 结束日期
     */
    @ExcelProperty(value = "终止日期")
    private String  endDate;

    /**
     * 培训类别
     */
    @ExcelProperty(value = "培训类别")
    private String trainCategory;



    /**
     * 培训内容
     */
    @ExcelProperty(value = "培训内容")
    private String trainContent;

    /**
     * 主办机构
     */
    @ExcelProperty(value = "主办单位")
    private String hostOrg;

    /**
     * 培训教师
     */
    @ExcelProperty(value = "培训教师")
    private String trainTeacher;

    /**
     * 课时
     */
    @ExcelProperty(value = "课时")
    private String classHour;

    /**
     * 培训地点
     */
    @ExcelProperty(value = "培训地点")
    private String trainLocation;



    /**
     * 培训分数
     */
    @ExcelProperty(value = "成绩")
    private BigDecimal score;

    /**
     * 员工岗位
     */
    @ExcelProperty(value = "岗位")
    private String empPost;

    /**
     * 员工职称
     */
    @ExcelProperty(value = "职称")
    private String empJob;

    /**
     * 学历
     */
    @ExcelProperty(value = "学历")
    private String educational;

    /**
     * 培训类型
     */
    @ExcelProperty(value = "培训类型")
    private String trainType;

    private static final long serialVersionUID = 1L;
}