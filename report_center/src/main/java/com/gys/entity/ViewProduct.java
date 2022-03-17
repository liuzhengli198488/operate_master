/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package com.gys.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * VIEW实体类
 *
 * @author Blade
 * @since 2020-04-10
 */
@Data
@Table(name = "GAIA_WMS_VIEW_PRODUCT")
@ApiModel(value = "ViewProduct对象", description = "VIEW")
public class ViewProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    //存储条件未冷藏的
    public static String COLD = "3";

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
     * 商品自编码
     */
    @ApiModelProperty(value = "商品自编码")
    @Column(name = "PRO_SELF_CODE")
    private String proSelfCode;

    /**
     * 商品定位
     */

    @ApiModelProperty(value = "商品定位")
    @Column(name = "PRO_POSITION")
    private String proPosition;

    /**
     * 国际条形码2
     */
    @ApiModelProperty(value = "国际条形码2")
    @Column(name = "PRO_BARCODE2")
    private String proBarcode2;

    /**
     * 生产企业编码
     */
    @ApiModelProperty(value = "生产企业")
    @Column(name = "PRO_FACTORY_CODE")
    private String factoryCode;

    /**
     * 商品编码
     */
    @ApiModelProperty(value = "商品编码")
    @Column(name = "PRO_CODE")
    private String proCode;

    /**
     * 商品描述
     */
    @ApiModelProperty(value = "商品描述")
    @Column(name = "PRO_NAME")
    private String proName;

    /**
     * 规格
     */
    @ApiModelProperty(value = "规格")
    @Column(name = "PRO_SPECS")
    private String proSpecs;

    /**
     * 计量单位
     */
    @ApiModelProperty(value = "计量单位")
    @Column(name = "PRO_UNIT")
    private String proUnit;

    /**
     * 生产企业
     */
    @ApiModelProperty(value = "生产企业")
    @Column(name = "PRO_FACTORY_NAME")
    private String proFactoryName;

    /**
     * 产地
     */
    @ApiModelProperty(value = "产地")
    @Column(name = "PRO_PLACE")
    private String proPlace;

    /**
     * 剂型
     */
    @ApiModelProperty(value = "剂型")
    @Column(name = "PRO_FORM")
    private String proForm;

    /**
     * 批准文号
     */

    @ApiModelProperty(value = "批准文号")
    @Column(name = "PRO_REGISTER_NO")
    private String proRegisterNo;


    /**
     * 国际条形码1
     */

    @ApiModelProperty(value = "国际条形码1")
    @Column(name = "PRO_BARCODE")
    private String proBarcode;

    /**
     * 贮存条件
     */
    @ApiModelProperty(value = "贮存条件")
    @Column(name = "PRO_STORAGE_CONDITION")
    private String proStorageCondition;


    @ApiModelProperty(value = "贮存条件")
    private String proStorageConditionName;

    /**
     * 商品仓储分区
     */
    @ApiModelProperty(value = "商品仓储分区")
    @Column(name = "PRO_STORAGE_AREA")
    private String proStorageArea;


    @ApiModelProperty(value = "商品仓储分区")
    private String proStorageAreaName;

    /**
     * 大包装量
     */

    @ApiModelProperty(value = "大包装量")
    @Column(name = "PRO_BIG_PACKAGE")
    private String proBigPackage;

    /**
     * 中包装量
     */

    @ApiModelProperty(value = "中包装量")
    @Column(name = "PRO_MID_PACKAGE")
    private String proMidPackage;

    /**
     * 长（以MM计算）
     */

    @ApiModelProperty(value = "长（以MM计算）")
    @Column(name = "PRO_LONG")
    private String proLong;

    /**
     * 宽（以MM计算）
     */

    @ApiModelProperty(value = "宽（以MM计算）")
    @Column(name = "PRO_WIDE")
    private String proWide;

    /**
     * 高（以MM计算）
     */

    @ApiModelProperty(value = "高（以MM计算）")
    @Column(name = "PRO_HIGH")
    private String proHigh;

    /**
     * 助记码
     */

    @ApiModelProperty(value = "助记码")
    @Column(name = "PRO_PYM")
    private String proPym;

    /**
     * 商品分类
     */

    @ApiModelProperty(value = "商品分类")
    @Column(name = "PRO_CLASS")
    private String proClass;

    /**
     * 生产经营许可证号
     */

    @ApiModelProperty(value = "生产经营许可证号")
    @Column(name = "PRO_QS_CODE")
    private String proQsCode;

    /**
     * 启用电子监管码 0-否，1-是
     */

    @ApiModelProperty(value = "启用电子监管码")
    @Column(name = "PRO_ELECTRONIC_CODE")
    private String proElectronicCode;

    /**
     * 进项税率
     */

    @Column(name = "PRO_INPUT_TAX")
    private String proInputTax;

    /**
     * 保质期
     */

    @ApiModelProperty(value = "保质期")
    @Column(name = "PRO_LIFE")
    private String proLife;

    /**
     * 保质期单位
     */

    @ApiModelProperty(value = "保质期单位")
    @Column(name = "PRO_LIFE_UNIT")
    private String proLifeUnit;

    /**
     * 固定货位号
     */
    @Column(name = "PRO_FIX_BIN")
    private String proFixBin;
}
