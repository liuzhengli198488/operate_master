package com.gys.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * GAIA_SD_REPLENISH_H
 * @author 
 */
@Data
public class GaiaSdReplenishHKey implements Serializable {
    /**
     * 加盟商
     */
    private String client;

    /**
     * 补货门店
     */
    private String gsrhBrId;

    /**
     * 补货单号
     */
    private String gsrhVoucherId;

    private static final long serialVersionUID = 1L;
}