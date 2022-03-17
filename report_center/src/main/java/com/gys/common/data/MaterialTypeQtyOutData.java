package com.gys.common.data;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MaterialTypeQtyOutData {
    private String proCode;
    private BigDecimal totalQty;
}
