package com.gys.entity.wk.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * GAIA_QUALITY_MANAGEMENT_SYSTEM_REPORT
 * @author 
 */
@Data
public class QualityManagementSystemReportDelDto implements Serializable {
    /**
     * 主键
     */
    private List<Long> ids;

    /**
     * 更新人
     */
    private String updateUser;
}