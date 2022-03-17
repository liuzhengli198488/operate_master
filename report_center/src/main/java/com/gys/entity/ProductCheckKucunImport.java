package com.gys.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author flynn
 * @since 2021-09-22
 */
@Data
@Table( name = "GAIA_PRODUCT_CHECK_KUCUN_IMPORT")
public class ProductCheckKucunImport implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "加盟商")
    @Column(name = "CLIENT")
    private String client;

    @ApiModelProperty(value = "门店code")
    @Column(name = "STO_CODE")
    private String stoCode;

    @ApiModelProperty(value = "门店name")
    @Column(name = "STO_NAME")
    private String stoName;

    @ApiModelProperty(value = "商品code")
    @Column(name = "PRO_CODE")
    private String proCode;

    @ApiModelProperty(value = "国际条形码1")
    @Column(name = "PRO_BARCODE")
    private String proBarcode;

    @ApiModelProperty(value = "商品名称")
    @Column(name = "PRO_NAME")
    private String proName;

    @ApiModelProperty(value = "商品库存数量")
    @Column(name = "KU_CUN_NUM")
    private String kuCunNum;


}
