package com.gys.entity.data.productMatch;

import lombok.Data;

@Data
public class MatchProductInData {
    /**
     * 加盟商
     */
    private String clientId;
    /**
     * 门店编码字符串（多选）
     */
    private String[] stoCodes;
    /**
     * 导入时间开始时间
     */
    private String startDate;
    /**
     * 导入时间结束时间
     */
    private String endDate;
    /**
     * 匹配类型 0-未匹配，1-部分匹配，2-完全匹配
     */
    private String matchType;
    /**
     * 筛选状态 0-未处理，1-自动处理，2-手动处理
     */
    private String matchStatus;
    /**
     * 抽取比例
     */
    private String decimationRatio;
    /**
     * 是否门店匹配 0 否 1 是(1时门店编码必填)
     */
    private String type;

    private Integer pageNum;

    private String setSelect; //不为空 不查询手动确认 为空查询手动确认

    /**
     * 单门店查询
     */
    private String stoCode;

    /**
     * 已选中商品编码
     */
    private String proCode;

    /**
     * 模糊匹配商品信息
     */
    private String nameOrCode;

    /**
     * 导入批次号
     */
    private String matchBatch;

    /** add by jinwencheng on 2022-01-07 添加MATCH_CODE属性 **/
    private String matchCode;
    /** add end **/

    // 最小匹配度
    private String minMatchDegree;

    // 最大匹配度
    private String maxMatchDegree;

    // 商品分类
    private String proClass;

    // 成分分类
    private String proCompClass;
}
