package com.gys.entity.data.dropdata;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 检索条件范围dto
 * */
@Data
public class SearchRangeDTO {

	@ApiModelProperty(value = "范围搜索-起始值")
	private String startRange = "";

	@ApiModelProperty(value = "范围搜索-结束值")
	private String endRange = "";

	public SearchRangeDTO() {
	}

	public SearchRangeDTO(String startRange) {
		this.startRange = startRange != null ? startRange : "";
	}

	public SearchRangeDTO(String startRange, String endRange) {
		this.startRange = startRange != null ? startRange : "";
		this.endRange = endRange != null ? endRange : "";
	}
}
