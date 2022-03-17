package com.gys.entity;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "GAIA_WMS_YAOJIAN")
public class GaiaWmsYaojian implements Serializable {
    /**
     * ID
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 加盟商
     */
    @Column(name = "CLIENT")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT LAST_INSERT_ID()")
    private String client;

    /**
     * 供应商编号
     */
    @Column(name = "WM_GYS_BH")
    private String wmGysBh;

    /**
     * 商品编码
     */
    @Column(name = "WM_SP_BM")
    private String wmSpBm;

    /**
     * 批号
     */
    @Column(name = "WM_PH")
    private String wmPh;

    /**
     * 有无报告0无/1有
     */
    @Column(name = "WM_YSR")
    private String wmYsr;

    /**
     * 修改日期
     */
    @Column(name = "WM_XGRQ")
    private String wmXgrq;

    /**
     * 修改时间
     */
    @Column(name = "WM_XGSJ")
    private String wmXgsj;

    /**
     * 修改人ID
     */
    @Column(name = "WM_XGR_ID")
    private String wmXgrId;

    /**
     * 修改人
     */
    @Column(name = "WM_XGR")
    private String wmXgr;

    /**
     * 图片地址
     */
    @Column(name = "WM_TPDZ")
    private String wmTpdz;

    /**
     * 图片张数
     */
    @Column(name = "WM_TPZS")
    private String wmTpzs;

    /**
     * 地点
     */
    @Column(name = "PRO_SITE")
    private String proSite;

    private static final long serialVersionUID = 1L;

    /**
     * 获取ID
     *
     * @return ID - ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置ID
     *
     * @param id ID
     */
    public void setId(Long id) {
        this.id = id;
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
     * 获取供应商编号
     *
     * @return WM_GYS_BH - 供应商编号
     */
    public String getWmGysBh() {
        return wmGysBh;
    }

    /**
     * 设置供应商编号
     *
     * @param wmGysBh 供应商编号
     */
    public void setWmGysBh(String wmGysBh) {
        this.wmGysBh = wmGysBh;
    }

    /**
     * 获取商品编码
     *
     * @return WM_SP_BM - 商品编码
     */
    public String getWmSpBm() {
        return wmSpBm;
    }

    /**
     * 设置商品编码
     *
     * @param wmSpBm 商品编码
     */
    public void setWmSpBm(String wmSpBm) {
        this.wmSpBm = wmSpBm;
    }

    /**
     * 获取批号
     *
     * @return WM_PH - 批号
     */
    public String getWmPh() {
        return wmPh;
    }

    /**
     * 设置批号
     *
     * @param wmPh 批号
     */
    public void setWmPh(String wmPh) {
        this.wmPh = wmPh;
    }

    /**
     * 获取有无报告0无/1有
     *
     * @return WM_YSR - 有无报告0无/1有
     */
    public String getWmYsr() {
        return wmYsr;
    }

    /**
     * 设置有无报告0无/1有
     *
     * @param wmYsr 有无报告0无/1有
     */
    public void setWmYsr(String wmYsr) {
        this.wmYsr = wmYsr;
    }

    /**
     * 获取修改日期
     *
     * @return WM_XGRQ - 修改日期
     */
    public String getWmXgrq() {
        return wmXgrq;
    }

    /**
     * 设置修改日期
     *
     * @param wmXgrq 修改日期
     */
    public void setWmXgrq(String wmXgrq) {
        this.wmXgrq = wmXgrq;
    }

    /**
     * 获取修改时间
     *
     * @return WM_XGSJ - 修改时间
     */
    public String getWmXgsj() {
        return wmXgsj;
    }

    /**
     * 设置修改时间
     *
     * @param wmXgsj 修改时间
     */
    public void setWmXgsj(String wmXgsj) {
        this.wmXgsj = wmXgsj;
    }

    /**
     * 获取修改人ID
     *
     * @return WM_XGR_ID - 修改人ID
     */
    public String getWmXgrId() {
        return wmXgrId;
    }

    /**
     * 设置修改人ID
     *
     * @param wmXgrId 修改人ID
     */
    public void setWmXgrId(String wmXgrId) {
        this.wmXgrId = wmXgrId;
    }

    /**
     * 获取修改人
     *
     * @return WM_XGR - 修改人
     */
    public String getWmXgr() {
        return wmXgr;
    }

    /**
     * 设置修改人
     *
     * @param wmXgr 修改人
     */
    public void setWmXgr(String wmXgr) {
        this.wmXgr = wmXgr;
    }

    /**
     * 获取图片地址
     *
     * @return WM_TPDZ - 图片地址
     */
    public String getWmTpdz() {
        return wmTpdz;
    }

    /**
     * 设置图片地址
     *
     * @param wmTpdz 图片地址
     */
    public void setWmTpdz(String wmTpdz) {
        this.wmTpdz = wmTpdz;
    }

    /**
     * 获取图片张数
     *
     * @return WM_TPZS - 图片张数
     */
    public String getWmTpzs() {
        return wmTpzs;
    }

    /**
     * 设置图片张数
     *
     * @param wmTpzs 图片张数
     */
    public void setWmTpzs(String wmTpzs) {
        this.wmTpzs = wmTpzs;
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
}