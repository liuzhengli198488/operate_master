package com.gys.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * <p>
 * 系统码表，加盟商 门店编号可以不填写，不填写情况下表示全局级别的码表
 * </p>
 *
 * @author flynn
 * @since 2022-01-29
 */
@Data
@Table( name = "GAIA_SYS_DIC")
public class SysDic implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键id")
    @Id
    @Column(name = "ID")
    private Integer id;

    @ApiModelProperty(value = "加盟商编号")
    @Column(name = "CLIENT")
    private String client;

    @ApiModelProperty(value = "门店编号")
    @Column(name = "STO_CODE")
    private String stoCode;

    @ApiModelProperty(value = "一类字典编码")
    @Column(name = "TYPE_CODE")
    private String typeCode;

    @ApiModelProperty(value = "字典明细项目编码")
    @Column(name = "KEY_CODE")
    private String keyCode;

    @ApiModelProperty(value = "字典明细项目值")
    @Column(name = "KEY_VALUE")
    private String keyValue;


}
