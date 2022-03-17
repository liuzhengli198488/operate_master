package com.gys.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 下货率实体类
 *
 * @author Blade
 * @since 2020-05-20
 */
@Data
@Table(name = "GAIA_WMS_TJ_XHL")
@ApiModel(value = "TjXhl对象", description = "下货率")
public class TjXhl implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    @Column(name = "ID")
    private Long id;
    /**
     * 加盟商
     */
    @ApiModelProperty(value = "加盟商")
    @Column(name = "CLIENT")
    private String client;
    /**
     * 地点
     */
    @ApiModelProperty(value = "地点")
    @Column(name = "PRO_SITE")
    private String proSite;
    /**
     * 作业日期
     */
    @ApiModelProperty(value = "作业日期")
    @Column(name = "ZY_RQ")
    private String zyRq;
    /**
     * 作业类型:1-配货 2-发货 3-最终
     */
    @ApiModelProperty(value = "作业类型:1-配货 2-发货 3-最终")
    @Column(name = "ZY_XL")
    private String zyXl;
    /**
     * 数量满足率
     */
    @ApiModelProperty(value = "数量满足率")
    @Column(name = "SL_MZL")
    private BigDecimal slMzl;
    /**
     * 金额满足率
     */
    @ApiModelProperty(value = "金额满足率")
    @Column(name = "JE_MZL")
    private BigDecimal jeMzl;
    /**
     * 品相满足率
     */
    @ApiModelProperty(value = "品相满足率")
    @Column(name = "PX_MZL")
    private BigDecimal pxMzl;


}
