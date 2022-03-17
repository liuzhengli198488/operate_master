package com.gys.entity.data.xhl.dto;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @Auther: tzh
 * @Date: 2021/12/14 10:50
 * @Description: ReportSummaryDto
 * @Version 1.0.0
 */
@Data
public class ReportSummaryDto {
    /**
     * 省份id列表
     */
    private  List<String> provinceList;

    /**
     * 城市id列表
     */
    private  List<String> cityList;
    /**
     * 加盟商id列表
     */
    private List<String> clientIds;


    @NotBlank(message = "开始时间不为空")
    private String startDate;

    @NotBlank(message = "结束时间不为空")
    private String endDate;

    /**
     * 报表类型： 1.日报  2.周报 3.月报
     */
    @NotBlank(message = "报表类型不为空")
    private String reportType;

    /**
     * 是否计入主动铺货   0 不计入  1 计入
     */
    private Integer  tag;


}
