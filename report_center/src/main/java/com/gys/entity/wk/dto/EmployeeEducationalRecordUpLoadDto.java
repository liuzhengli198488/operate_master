package com.gys.entity.wk.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @Auther: tzh
 * @Date: 2022/1/4 10:18
 * @Description: EmployeeEducationalRecordUpLoadDto
 * @Version 1.0.0
 */
@Data
public class EmployeeEducationalRecordUpLoadDto {
    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Integer id;

    /**
     * 加盟商
     */
    @ExcelProperty(value = "加盟商")
    private String client;

    /**
     * 编号
     */
    @ExcelProperty(value = "编号")
    private String voucherId;
}
