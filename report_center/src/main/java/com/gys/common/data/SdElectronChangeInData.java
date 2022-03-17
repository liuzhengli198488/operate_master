package com.gys.common.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @desc: 电子券异动  报表查询入参
 * @author: ZhangChi
 * @createTime: 2022/1/7 17:22
 */
@Data
public class SdElectronChangeInData {
    /**
     * 加盟商
     */
    private String client;

    /**
     * 送券门店
     */
    private List<String> gsecGiveBrId;

    /**
     * 用券门店
     */
    private List<String> gsecBrId;

    /**
     * 是否使用
     */
    private String gsecStatus;

    /**
     * 主题属性（CM=消费型，NM=新会员，FM=老会员）
     */
    private List<String> gsetsAttribute;

    /**
     * 会员卡号
     */
    private String gsecMemberId;

    /**
     * 送券起始日期
     */
    private String giveStartDate;

    /**
     * 送券结束日期
     */
    private String giveEndDate;

    /**
     * 用券起始日期
     */
    private String useStartDate;

    /**
     * 用券结束日期
     */
    private String useEndDate;

    /**
     * 电子券活动号
     */
    private String gsebId;

    /**
     * 当前页
     */
    private Integer pageNum;

    /**
     * 每页条数
     */
    private Integer pageSize;
}
