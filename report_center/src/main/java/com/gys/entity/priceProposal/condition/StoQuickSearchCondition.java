package com.gys.entity.priceProposal.condition;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author Jinwencheng
 * @Version 1.0
 * @Description 门店快捷选择需要的参数
 * @CreateTime 2022-01-12 10:37:00
 */
@Data
public class StoQuickSearchCondition {

    @ApiModelProperty("是否医保店1.是 0.否")
    private Integer[] ifMedicalcare;

    private int contained;

    @ApiModelProperty("门店属性1.单体 2.连锁 3.加盟 4.门诊")
    private String[] stoAttribute;

    @ApiModelProperty("分类类型编码")
    private String[][] gssgType;

    private List<String> gssgTypes;

    private List<String> gssgIds;

    @ApiModelProperty("加盟商id")
    private String clientId;

}
