package com.gys.entity.data.suggestion.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @Auther: tzh
 * @Date: 2022/1/12 14:49
 * @Description: UpdateExportDto
 * @Version 1.0.0
 */
@Data
public class UpdateExportDto {
    /**
     * 主键
     */
    private String id;

    /**
     * 加盟商id
     */
    private String clientId;

    /**
     * 门店id
     */
    private String storeId;



}
