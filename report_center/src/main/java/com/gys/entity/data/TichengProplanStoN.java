package com.gys.entity.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 单品提成方案门店表
 * </p>
 *
 * @author flynn
 * @since 2021-11-15
 */
@Data
@Table( name = "GAIA_TICHENG_PROPLAN_STO_N")
public class TichengProplanStoN implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键ID")
    @Id
    @Column(name = "ID")
    private Long id;

    @ApiModelProperty(value = "加盟商")
    @Column(name = "CLIENT")
    private String client;

    @ApiModelProperty(value = "门店编码")
    @Column(name = "STO_CODE")
    private String stoCode;

    @ApiModelProperty(value = "提成方案主表主键ID")
    @Column(name = "PID")
    private Long pid;

    @ApiModelProperty(value = "操作状态 0 未删除 1 已删除")
    @Column(name = "DELETE_FLAG")
    private String deleteFlag;

    @Column(name = "LAST_UPDATE_TIME")
    private Date lastUpdateTime;


}
