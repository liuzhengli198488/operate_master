package com.gys.common.constant;

import java.util.Arrays;
import java.util.List;

public interface CommonConstant {

    /**
     * 门店补货公式差异汇总导出Excel表头
     */
    String[] REPLENISH_DIFF_SUM_EXPORT_HEAD = {"日期", "省", "市", "客户", "客户名称", "补货方式", "补货门店数", "单店单次原补货品项", "单店单次实际补货品项",
            "原单品项数", "新增品项数", "新增占比", "删除品项数", "删除占比", "一致品项数", "一致占比", "修改品项数", "修改占比", "补货公式零售额 (万)", "实际零售额 (万)",
            "零售额差异 (万)", "补货公式成本额 (万)", "实际成本额 (万)", "成本额差异 (万)"};

    /**
     * 门店补货公式差异汇总导出Sheet名称
     */
    String REPLENISH_DIFF_SUM_SHEET_NAME = "门店补货公式差异汇总";


    /**
     * 质量体系内部评审记录表导出Excel表头
     */
    String[] GAIA_INTERNAL_QUALITY_REVIEW = {"序号","gsp编号","评审日期","评审人员","评审内容","评审质量","建议",
            "实施情况","审核目的","审核依据","审核范围","审核组织","受审部门"};
    /**
     *质量体系内部评审记录表名称
     */
    String GAIA_INTERNAL_QUALITY_REVIEW_NAME = "质量体系内部评审记录表";

    String  YEAR ="年";
    String  WEEK ="周";
    String  MONTH ="月";
    /**
     * WEB端联合用药查询报表用户
     */
    String[] COMBINED_MEDICATION_QUERY_REPORT_USERS =
            {"序号","门店编码", "门店简称", "销售天数", "实收金额", "交易次数", "客单价","客品次", "品单价","关联弹出次数","弹出率","关联成交次数","成交率","关联销售额","关联销售占比"};

    /**
     *WEB端联合用药查询报表用户
     */
    String COMBINED_MEDICATION_QUERY_REPORT_NAME = "WEB端用户联合用药查询报表";

    /**
     * 不展示字段
     */
    List<String> COMMISSION_NOT_SHOW_FIELD = Arrays.asList("costAmt", "grossProfitAmt", "grossProfitRate");

    /**
     *门店折扣销售汇总表
     */
    String STORE_DISCOUNT_SUMMARY_REPORT_NAME = "门店折扣销售汇总表";

    /**
     *门店折扣销售汇总表
     */
    String[] STORE_DISCOUNT_SUMMARY_REPORT_USERS =
            {"销售日期","门店编码","门店名称","销售天数","成本额","应收金额","实收金额","毛利额","毛利率","折扣率","折扣总额","税率","门店属性","是否医保店","DTP","纳税属性","铺货店型","店效级别","是否直营管理","管理区域"};
}
