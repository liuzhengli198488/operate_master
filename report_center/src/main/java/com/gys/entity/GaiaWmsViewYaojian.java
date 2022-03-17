package com.gys.entity;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "GAIA_WMS_VIEW_YAOJIAN")
public class GaiaWmsViewYaojian implements Serializable {
    /**
     * 供应商自编码
     */
    @Column(name = "SUPPLIERCODER")
    private String suppliercoder;

    /**
     * 供应商名称
     */
    @Column(name = "SUPPLIERNAME")
    private String suppliername;

    /**
     * 商品编码
     */
    @Column(name = "COMMODITYCODE")
    private String commoditycode;

    /**
     * 商品描述
     */
    @Column(name = "PRO_NAME")
    private String proName;

    /**
     * 规格
     */
    @Column(name = "SPECIFICATIONS")
    private String specifications;

    /**
     * 生产厂家
     */
    @Column(name = "MANUFACTURER")
    private String manufacturer;

    /**
     * 批号
     */
    @Column(name = "PRODUCTBATCHNO")
    private String productbatchno;

    /**
     * 生产日期
     */
    @Column(name = "PRODUCTIONDATE")
    private String productiondate;

    /**
     * 有效期
     */
    @Column(name = "VALIDITYDATE")
    private String validitydate;

    @Column(name = "ISREPORT")
    private Integer isreport;

    /**
     * 验收确认人
     */
    @Column(name = "CREATEUSER")
    private String createuser;

    /**
     * 验收确认日期
     */
    @Column(name = "ACCEPTDATE")
    private String acceptdate;

    /**
     * 修改人
     */
    @Column(name = "UPLOADUSER")
    private String uploaduser;

    /**
     * 加盟商
     */
    @Column(name = "CLIENT")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT LAST_INSERT_ID()")
    private String client;

    /**
     * 地点
     */
    @Column(name = "PRO_SITE")
    private String proSite;

    /**
     * 验收确认人ID
     */
    @Column(name = "WM_YSR_ID")
    private String wmYsrId;

    private static final long serialVersionUID = 1L;

    /**
     * 获取供应商自编码
     *
     * @return SUPPLIERCODER - 供应商自编码
     */
    public String getSuppliercoder() {
        return suppliercoder;
    }

    /**
     * 设置供应商自编码
     *
     * @param suppliercoder 供应商自编码
     */
    public void setSuppliercoder(String suppliercoder) {
        this.suppliercoder = suppliercoder;
    }

    /**
     * 获取供应商名称
     *
     * @return SUPPLIERNAME - 供应商名称
     */
    public String getSuppliername() {
        return suppliername;
    }

    /**
     * 设置供应商名称
     *
     * @param suppliername 供应商名称
     */
    public void setSuppliername(String suppliername) {
        this.suppliername = suppliername;
    }

    /**
     * 获取商品编码
     *
     * @return COMMODITYCODE - 商品编码
     */
    public String getCommoditycode() {
        return commoditycode;
    }

    /**
     * 设置商品编码
     *
     * @param commoditycode 商品编码
     */
    public void setCommoditycode(String commoditycode) {
        this.commoditycode = commoditycode;
    }

    /**
     * 获取商品描述
     *
     * @return PRO_NAME - 商品描述
     */
    public String getProName() {
        return proName;
    }

    /**
     * 设置商品描述
     *
     * @param proName 商品描述
     */
    public void setProName(String proName) {
        this.proName = proName;
    }

    /**
     * 获取规格
     *
     * @return SPECIFICATIONS - 规格
     */
    public String getSpecifications() {
        return specifications;
    }

    /**
     * 设置规格
     *
     * @param specifications 规格
     */
    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    /**
     * 获取生产厂家
     *
     * @return MANUFACTURER - 生产厂家
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * 设置生产厂家
     *
     * @param manufacturer 生产厂家
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * 获取批号
     *
     * @return PRODUCTBATCHNO - 批号
     */
    public String getProductbatchno() {
        return productbatchno;
    }

    /**
     * 设置批号
     *
     * @param productbatchno 批号
     */
    public void setProductbatchno(String productbatchno) {
        this.productbatchno = productbatchno;
    }

    /**
     * 获取生产日期
     *
     * @return PRODUCTIONDATE - 生产日期
     */
    public String getProductiondate() {
        return productiondate;
    }

    /**
     * 设置生产日期
     *
     * @param productiondate 生产日期
     */
    public void setProductiondate(String productiondate) {
        this.productiondate = productiondate;
    }

    /**
     * 获取有效期
     *
     * @return VALIDITYDATE - 有效期
     */
    public String getValiditydate() {
        return validitydate;
    }

    /**
     * 设置有效期
     *
     * @param validitydate 有效期
     */
    public void setValiditydate(String validitydate) {
        this.validitydate = validitydate;
    }

    /**
     * @return ISREPORT
     */
    public Integer getIsreport() {
        return isreport;
    }

    /**
     * @param isreport
     */
    public void setIsreport(Integer isreport) {
        this.isreport = isreport;
    }

    /**
     * 获取验收确认人
     *
     * @return CREATEUSER - 验收确认人
     */
    public String getCreateuser() {
        return createuser;
    }

    /**
     * 设置验收确认人
     *
     * @param createuser 验收确认人
     */
    public void setCreateuser(String createuser) {
        this.createuser = createuser;
    }

    /**
     * 获取验收确认日期
     *
     * @return ACCEPTDATE - 验收确认日期
     */
    public String getAcceptdate() {
        return acceptdate;
    }

    /**
     * 设置验收确认日期
     *
     * @param acceptdate 验收确认日期
     */
    public void setAcceptdate(String acceptdate) {
        this.acceptdate = acceptdate;
    }

    /**
     * 获取修改人
     *
     * @return UPLOADUSER - 修改人
     */
    public String getUploaduser() {
        return uploaduser;
    }

    /**
     * 设置修改人
     *
     * @param uploaduser 修改人
     */
    public void setUploaduser(String uploaduser) {
        this.uploaduser = uploaduser;
    }

    /**
     * 获取加盟商
     *
     * @return CLIENT - 加盟商
     */
    public String getClient() {
        return client;
    }

    /**
     * 设置加盟商
     *
     * @param client 加盟商
     */
    public void setClient(String client) {
        this.client = client;
    }

    /**
     * 获取地点
     *
     * @return PRO_SITE - 地点
     */
    public String getProSite() {
        return proSite;
    }

    /**
     * 设置地点
     *
     * @param proSite 地点
     */
    public void setProSite(String proSite) {
        this.proSite = proSite;
    }

    /**
     * 获取验收确认人ID
     *
     * @return WM_YSR_ID - 验收确认人ID
     */
    public String getWmYsrId() {
        return wmYsrId;
    }

    /**
     * 设置验收确认人ID
     *
     * @param wmYsrId 验收确认人ID
     */
    public void setWmYsrId(String wmYsrId) {
        this.wmYsrId = wmYsrId;
    }
}