package com.gys.entity.wk.dto;

import lombok.Data;

import java.util.List;

/**
 * @Auther: tzh
 * @Date: 2021/12/30 11:08
 * @Description: MedicinalQualityRecordDto
 * @Version 1.0.0
 */
@Data
public class EmployeeEducationalRecordDelDto {
    /**
     * 主键
     */
    private List<Integer> ids;

    /**
     * 更新人
     */
    private String updateUser;

}
