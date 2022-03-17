package com.gys.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * GAIA_SO_ITEM
 * @author 
 */
@Data
public class GaiaSoItemKey implements Serializable {
    private String client;

    /**
     * 销售订单号
     */
    private String soId;

    /**
     * 订单行号
     */
    private String soLineNo;

    private static final long serialVersionUID = 1L;
}