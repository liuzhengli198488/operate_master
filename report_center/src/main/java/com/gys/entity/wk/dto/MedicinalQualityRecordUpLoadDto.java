package com.gys.entity.wk.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import java.util.Date;

/**
 * @Auther: tzh
 * @Date: 2021/12/30 11:08
 * @Description: MedicinalQualityRecordDto
 * @Version 1.0.0
 */
@Data
public class MedicinalQualityRecordUpLoadDto {
    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

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
