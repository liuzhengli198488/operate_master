package com.gys.entity.data.xhl.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @Auther: tzh
 * @Date: 2021/12/10 18:07
 * @Description: ReportInfoDto
 * @Version 1.0.0
 */
@Data
public class ReportInfoDto {
    //省id
    private  String provinceId;
    //市id
    private  String cityId;

    private List<String>  provinceList;
    private List<String>  cityList;
    //供应商id
    @NotBlank(message = "供应商不为空")
    private List<String> clientIds;
    //门店id
    private List<String> storeIds;
    // 查看方式
    private  List<String>  viewSource;

    //开始时间
    @NotBlank(message ="开始时间不为空")
    private String beginDate;
    //结束时间
    @NotBlank(message ="结束时间不为空")
    private String endDate;

    private Integer pageNum;

    private Integer pageSize;
}
