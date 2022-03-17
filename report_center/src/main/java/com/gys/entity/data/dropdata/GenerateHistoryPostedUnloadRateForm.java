package com.gys.entity.data.dropdata;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GenerateHistoryPostedUnloadRateForm {

	@ApiModelProperty(value = "统计日期,格式:yyyyMMdd")
	private String statisticDate;
}
