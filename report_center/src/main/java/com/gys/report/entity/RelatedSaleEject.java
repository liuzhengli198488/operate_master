package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * <p>
 * 关联销售弹出率统计表
 * </p>
 *
 * @author flynn
 * @since 2021-08-18
 */
@Data
@Table( name = "GAIA_SD_RELATED_SALE_EJECT")
public class RelatedSaleEject implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键uuid")
    @Id
    @Column(name = "ID")
    private String id;

    @ApiModelProperty(value = "加盟商")
    @Column(name = "CLIENT")
    private String client;

    @ApiModelProperty(value = "门店编码")
    @Column(name = "SITE_CODE")
    private String siteCode;

    @ApiModelProperty(value = "商品编码")
    @Column(name = "PRO_CODE")
    private String proCode;

    @ApiModelProperty(value = "是否关联弹出标识1表示是0表示否")
    @Column(name = "RELATED_SALE_FLAG")
    private String relatedSaleFlag;

    @ApiModelProperty(value = "创建日期")
    @Column(name = "CREATE_DATE")
    private String createDate;

    @ApiModelProperty(value = "创建时间")
    @Column(name = "CREATE_TIME")
    private String createTime;

    @ApiModelProperty(value = "创建人")
    @Column(name = "CREATE_BY")
    private String createBy;


}
