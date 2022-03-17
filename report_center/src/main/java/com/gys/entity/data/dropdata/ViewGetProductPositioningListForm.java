package com.gys.entity.data.dropdata;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewGetProductPositioningListForm {
	/**
	 * 传递的商品各属性
	 */
	@ApiModelProperty(value = "传递的商品各属性")
	private String productInfo;
}
