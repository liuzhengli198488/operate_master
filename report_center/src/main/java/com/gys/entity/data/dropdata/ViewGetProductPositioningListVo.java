package com.gys.entity.data.dropdata;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ViewGetProductPositioningListVo {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品编码
	 */
	@ApiModelProperty(value = "商品编码")
	private String proCode;

	/**
	 * 商品名称
	 */
	@ApiModelProperty(value = "商品名称")
	private String proName;

	public ViewGetProductPositioningListVo(String proCode, String proName) {
		this.proCode = proCode;
		this.proName = proName;
	}
	public ViewGetProductPositioningListVo(){

	}

}
