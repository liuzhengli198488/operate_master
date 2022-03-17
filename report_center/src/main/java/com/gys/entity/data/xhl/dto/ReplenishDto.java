package com.gys.entity.data.xhl.dto;

import lombok.Data;

import javax.annotation.sql.DataSourceDefinition;
import java.util.List;

/**
 * @Auther: tzh
 * @Date: 2021/12/19 16:42
 * @Description: ReplenishDto
 * @Version 1.0.0
 */
@Data
public class ReplenishDto {
    private List<String> clients;
    private String beginDate;
    private String endDate;
}
