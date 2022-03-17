package com.gys.entity.data.suggestion.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @Auther: tzh
 * @Date: 2022/1/12 14:49
 * @Description: QueryAdjustmentDto
 * @Version 1.0.0
 */
@Data
public class QueryAdjustmentDto {
    /**
     * 省份list
     */
    private List<String> provinceIds;

    /**
     * 城市list
     */
    private List<String> cityIds;

    /**
     * 客户list
     */
    private List<String> clientIds;

    /**
     *  报表类型  type 1 日报  type 2 周报   type 3 月报
     */
    @NotBlank(message = "请选择报表类型")
    private String type;

    /**
     * 日期选择：开始时间 格式20220112 8位
     */
    @NotBlank(message = "请选择报开始日期")
    private String  beginDate;

    /**
     * 日期选择：结束时间 格式20220112 8位
     */
    @NotBlank(message = "请选择结束日期")
    private String   endDate;
    /**
     * 页数
     */
    private Integer pageNum;
    /**
     * 页大小
     */
    private Integer  pageSize;

}
