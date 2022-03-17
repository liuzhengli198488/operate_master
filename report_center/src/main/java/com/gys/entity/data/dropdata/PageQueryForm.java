package com.gys.entity.data.dropdata;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Data
@ApiModel(value = "分页查询")
public class PageQueryForm<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "药德客户")
	private String clientId;

	@ApiModelProperty(value = "配送中心")
	private String dcId;

	@ApiModelProperty(value = "配送方式0正常补货 2铺货 3互调 4直配 null全部")
	private Integer replenishStyle;


	/**
	 * 当前页码
	 */
	@ApiModelProperty(value = "当前页码")
	private String pageIndex;
	/**
	 * 分页大小
	 */
	@ApiModelProperty(value = "分页大小")
	private String pageSize;
	/**
	 * 排序字段
	 */
	@ApiModelProperty(value = "排序字段")
	private String sortBy;
	/**
	 * 排序方式
	 */
	@ApiModelProperty(value = "排序方式")
	private String orderBy;
	/**
	 * 查询条件
	 */
	@Valid
	@ApiModelProperty(value = "查询条件")
	private List<T> conditions;

	/**
	 * 排序字段映射map
	 */
	@ApiModelProperty(value = "排序字段映射map")
	private HashMap<String, String> sortFieldMap;


	public HashMap<String, String> getSortFieldMap() {
		if(sortFieldMap == null){
			sortFieldMap = new HashMap<String, String>(16);
		}
		return sortFieldMap;
	}

	public String transform(String key){
		return Optional.ofNullable(getSortFieldMap().get(key)).orElse(key);
	}
}
