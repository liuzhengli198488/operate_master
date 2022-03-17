package com.gys.entity.data.dropdata;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GetHistoryPostedUnloadRateTotalDTO {

	private BigDecimal total;

	//订单数量合计
	private BigDecimal orderQuantityTotal;
	//配送单原数量
	private BigDecimal deliveryQuantityTotal;
	//过账数量合计
	private BigDecimal deliveryPostedQuantityRateTotal;
}
