package com.gys.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * GAIA_STORE_IN_SUGGESTION_H
 * @author 
 */
@Data
public class GaiaStoreOutSuggestionH implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 加盟商
     */
    private String client;

    /**
     * 门店编码
     */
    private String stoCode;

    /**
     * 门店名称
     */
    private String stoName;

    /**
     * 商品调出单号
     */
    private String billCode;

    /**
     * 单据日期
     */
    private Date billDate;

    /**
     * 计算周期 周-week 月-month
     */
    private String type;

    /**
     * 商品调库失效日期
     */
    private Date invalidDate;

    /**
     * 单据状态：0-未推送 1-已推送 2-待确认 3-已确认 4-失效
     */
    private Integer status;

    /**
     * 商品调库品项数
     */
    private Integer itemsQty;

    /**
     * 完成日期
     */
    private Date finishTime;

    /**
     * 是否删除：0-正常 1-删除
     */
    private Integer isDelete;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建者
     */
    private String createUser;

    /**
     * 更新者
     */
    private Date updateTime;

    /**
     * 更新时间
     */
    private String updateUser;

    /**
     * 0 未导出 1 导出
     */
    private Integer exportBeforeConfirm;

    /**
     * 0 未导出 1 导出
     */
    private Integer exportAfterConfirm;

    private static final long serialVersionUID = 1L;
}