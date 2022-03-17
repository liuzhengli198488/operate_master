package com.gys.report.common.constant;

/**
 * @author SunJiaNan
 * @version 1.0
 * @date 2021/10/18 10:18
 * @description 业务常量
 */
public interface CommonConstant {

    String GSSP_ID_IMPRO_DETAIL = "IMPRO_DETAIL";

    String COLON = ":";

    /**
     * 日期格式 yyyy
     */
    String FORMAT_YYYY = "yyyy";

    /**
     * 日期格式 yyyyMMdd
     */
    String FORMAT_YYYYMMDD = "yyyyMMdd";

    /**
     * 日期格式 HHmmss
     */
    String FORMAT_HHMMSS = "HHmmss";

    /**
     * 累加数量 1
     */
    String BIGDECIMAL_ONE = "1";

    /**
     * GAIA_SD_TEMP_HUMI表单号前缀 TH
     */
    String PERFIX_TH = "TH";

    /**
     * GAIA_SD_TEMP_HUMI区域 1
     */
    String AREA_1 = "1";

    /**
     * GAIA_SD_TEMP_HUMI区域 3
     */
    String AREA_3 = "3";

    /**
     * GAIA_SD_TEMP_HUMI区域 5
     */
    String AREA_5 = "5";

    /**
     * 最小检查时间  110000
     */
    String MIN_CHECK_TIME = "11:00:00";

    /**
     * 最大检查时间  115959
     */
    String MAX_CHECK_TIME = "11:59:59";

    /**
     * 室内温度  10
     */
    String INDOOR_TEMP = "10";

    /**
     * 库内湿度  55
     */
    String LIBRARY_HUMI = "55";

    /**
     * 审核状态 1
     */
    String STATE_1 = "1";


    /**
     * 默认页码 1
     */
    int PAGE_NUM = 1;

    /**
     * 默认每页显示条数 1000
     */
    int PAGE_SIZE = 1000;


    /**
     * 会员信息导出表头
     */
    String[] MEMBER_HEAD_LIST = {"办卡门店编码", "办卡门店名称", "录入日期", "最近消费日期", "会员卡号", "姓名", "性别", "卡类别", "手机", "电话",
            "地址", "累计总金额", "年度累计金额", "累计积分", "年度累计积分", "积分", "储值卡余额"};


    /**
     * 会员资料sheet名
     */
    String MEMBER_SHEET_NAME = "会员资料";

    /**
     * 储值卡金额异动sheet名
     */
    String RECHARGE_CHANGE_EXPORT_SHEET_NAME = "储值卡充值消费记录";

    /**
     * 储值卡金额异动file名
     */
    String RECHARGE_CHANGE_EXPORT_FILE_NAME = "储值卡充值消费记录";

    /**
     * 补货类型   2
     */
    String TYPE_2 = "2";

    /**
     * 补货状态是否审核 0-未审核
     */
    String STATUS_0 = "0";

    /**
     * 是否转采购订单 0-否
     */
    String FLAG_0 = "0";

    /**
     * 是否特殊商品 1-特殊商品
     */
    String SPECIAL_1 = "1";

    /**
     * 是否特殊商品 0-普通商品
     */
    String SPECIAL_0 = "0";

    /**
     * 补货来源 1门店缺断货补充，2门店品类补充
     */
    String SOURCE_1 = "1";

    /**
     * 消息类型  补货界面编码
     */
    String MESSAGE_GAIA_SD_0104 = "GAIA_SD_0104";

    /**
     * 消息内容描述
     */
    String MESSAGE_DESC = "您有1条补货未处理！";

    /**
     * 消息是否被查看  N-未查看 Y已查看
     */
    String MESSAGE_FLAG_N = "N";

    /**
     * 点击消息跳转页面
     */
    String MESSAGE_PAGE_REPLENISHMENT = "replenishment";

    /**
     * 消息效期天数
     */
    String MESSAGE_WARNING_DAY_180 = "180";

    /**
     * 平台类型
     */
    String MESSAGE_PLATFORM_TYPE_FX = "FX";

    /**
     * 删除标记 0-未删除 1-已删除
     */
    String MESSAGE_DELETE_FLAG_0 = "0";

    /**
     * 按门店查询
     */
    String MESSAGE_QUERY_STORE = "store";

    /**
     * 商品淘汰字段名称
     */
    String PRO_POSITION_COLUMN_NAME = "PRO_POSITION";

    /**
     * 商品淘汰标识
     */
    String PRO_POSITION_T = "T";

    /**
     * 商品变更审批是否结束 0-未结束 1-结束
     */
    String PRO_SFSP_1 = "1";

    /**
     * 审批状态  1-通过
     */
    String PRO_SFTY_1 = "1";

    /**
     * 缺断货默认倒推天数 90天
     */
    int OUT_OF_STOCK_DAY_90 = 90;

    /**
     * 添加补货计划 计算补货参数 默认比较值 100
     */
    String COMPARE_PRICE_100 = "100";

    /**
     * 添加补货计划  计算补货参数  平均售价>=100元  补货2个
     */
    String REPLENISH_QTY_2 = "2";

    /**
     * 添加补货计划  计算补货参数  平均售价<=100元  补货3个
     */
    String REPLENISH_QTY_3 = "3";


    /**
     * 效期商品默认计算天数 180天
     */
    String VALID_PRODUCT_180 = "180";

    /**
     * 特殊商品导入表头
     */
    String[] PRODUCT_SPECIAL_IMPORT_HEAD = {"药德通用编码", "省", "市", "类型", "标识", "状态"};

    /**
     * 特殊商品导出表头
     */
    String[] PRODUCT_SPECIAL_EXPORT_EXCEL_HEAD = {"药德通用编码", "商品描述", "规格", "生产厂家", "单位", "成分分类", "成分分类描述", "商品分类",
            "商品分类描述", "省", "市", "类型", "标识", "状态", "创建人", "创建日期", "最后一次修改人", "最后一次修改日期"};
    /**
     * 公司级新品评估导出表头
     */
    String[] ENT_NEW_PRODUCT_EXPORT_EXCEL_HEAD = {"评估状态", "通用编码","名称", "规格", "生产厂家", "月均销量", "月均销售额", "月均毛利额", "铺货门店数",
            "动销门店数", "动销率","商品分类","成分分类", "评估建议", "评估结果"};
    /**
     * 公司级新品评估导出表头
     */
    String[] ENT_INFO_EXPORT_EXCEL_HEAD = {"门店ID", "门店名称","门店属性", "店型", "管理属性"};
    /**
     * 特殊商品导出表头
     */
    String PRODUCT_SPECIAL_EXPORT_SHEET_NAME = "特殊商品";
    /**
     * 评估门店明细
     */
    String ENT_INFO_EXPORT_SHEET_NAME = "评估门店明细";

    /**
     * 公司级新品评估导出sheet名
     */
    String ENT_NEW_PRODUCT_EXPORT_SHEET_NAME = "公司级新品评估";

    /**
     * 特殊商品修改记录导出表头
     */
    String[] EXPORT_EDIT_RECORD_EXCEL_HEAD = {"药德通用编码", "商品描述", "规格", "生产厂家", "单位", "成分分类", "成分分类描述", "商品分类",
            "商品分类描述", "类型", "省", "市", "变更字段", "变更前值", "变更后值", "更新日期", "更新人"};

    /**
     * 特殊商品修改记录导出sheet名
     */
    String PRODUCT_SPECIAL_EDIT_RECORD_SHEET_NAME = "特殊商品修改记录";


    /**
     * 特殊商品参数上传表头
     */
    String[] PRODUCT_SPECIAL_PARAM_IMPORT_HEAD = {"序号", "省", "市", "季节", "季节开始日期", "季节结束日期", "分组"};


    /**
     * 特殊商品参数导出表头
     */
    String[] PRODUCT_SPECIAL_PARAM_EXPORT_HEAD = {"省", "市", "季节", "季节开始日期", "季节结束日期", "更新日期", "更新人"};

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
     *WEB端联合用药查询报表用户
     */
    String GAIA_OAS_SICKNESS_TAG_NAME = "疾病分类";
    /**
     * 疾病分类导出
     */
    String[] GAIA_OAS_SICKNESS_TAG ={"疾病大类编码","疾病大类名称", "疾病中类编码", "疾病中类名称", "成分细类编码", "成分细类名称          ", "维护日期","维护人", "状态"};
    /**
     * 供应商明细导出
     */
    String[] GAIA_OAS_SICKNESS_SUPPLIER ={"地点编码","供应商编码", "供应商名称", "二级部门编码", "二级部门名称", "业务类型", "发生日期","单据编号", "结算金额","发票金额"};
    String[] GAIA_OAS_SICKNESS ={"地点编码","供应商编码", "供应商名称", "二级部门编码", "二级部门名称", "结算金额","发票金额","备注"};
    /**
     * 特殊商品导出参数sheet名
     */
    String PRODUCT_SPECIAL_PARAM_SHEET_NAME = "特殊商品参数记录";

    /**
     * 特殊商品参数修改记录导出表头
     */
    String[] PRODUCT_SPECIAL_PARAM_EDIT_RECORD_HEAD = {"省", "市", "季节", "变更字段", "变更前值", "变更后值", "更新日期", "更新人"};

    /**
     * 特殊商品参数修改记录sheet名
     */
    String PRODUCT_SPECIAL_PARAM_EDIT_RECORD_NAME = "特殊商品参数修改记录";

    /**
     * 门店补货对比差异导出表头
     */
    String[] PRODUCT_REPLENISHMENT_DIFFERENCES_EXPORT_EXCEL_HEAD = {"序号", "门店编号", "门店名称", "创建日期", "创建时间", "创建人", "审核日期", "审核时间",
            "审核人", "补货单号", "商品编码", "商品名称", "规格", "单位", "厂家", "零售价", "建议补货数量", "补货量","差异量","状态"};
    /**
     * 门店补货对比差异导出表名
     */
    String PRODUCT_REPLENISHMENT_DIFFERENCES_EXPORT_SHEET_NAME = "门店补货对比差异";

    /**
     * 渠道管理批量导入Excel表头
     */
    String[] CHANNEL_MANAGER_EXCEL_HEAD = {"门店编码", "商品渠道", "商品编码", "渠道售价", "商品状态"};

    /**
     * 渠道管理批量导入Excel表头
     */
    String[] CHANNEL_SICKNESS = {"疾病大类编码", "疾病大类名称", "疾病中类编码", "疾病中类名称", "成分细类编码","成分细类名称"};

    /**
     * 上架
     */
    String CHANNEL_MANAGER_STATUS_UP = "上架";

    /**
     * 上架
     */
    String CHANNEL_MANAGER_STATUS_LOWER = "下架";

    /**
     * 月 或 周 1-月 2-周
     */
    String YEAR_OR_MONTH_1 = "1";

    /**
     * 药品小计编码 - 888
     */
    String SUBTOTAL_CODE_888 = "888";

    /**
     * 合计编码 - 999
     */
    String TOTAL_CODE_999 = "999";

    /**
     * 合计描述  编码 999
     */
    String TOTAL_DESC = "合计";
    
    /**
     * 销售目录维护批量导入Excel表头
     */
    String[] GAIA_SALES_CATALOG_IMPORT = {"商品编码", "批号"};

    /**
     * 重点商品任务-商品维护导入Excel表头
     */
    String[] GAIA_KEY_GOODS_IMPORT = {"商品编码", "本期计划销售额", "本期计划销售量"};
    /**
     * 重点商品任务-门店维护导入Excel表头
     */
    String[] GAIA_KEY_STORES_IMPORT = {"门店编码", "本期计划销售额", "本期计划销售量"};
    /**
     * 批发应收管理-导入Excel表头
     */
    String[] GAIA_AR_BILL_IMPORT = {"仓库编码", "客户编码", "应收方式编码","结算金额","发生日期","备注"};

    /**
     * 铺货单号
     */
    String REPLENISH_MAX_VOUCHER_ID_KEY = "REPLENISH_MAX_VOUCHER_ID:";

    /**
     * 门店必备商品明细导出Sheet名称
     */
    String ESSENTIAL_PRODUCT_EXPORT_SHEET_NAME = "必备商品单据明细";

    /**
     * 门店必备商品明细导出Excel表头
     */
    String[] ESSENTIAL_PRODUCT_EXPORT_HEAD = {"序号", "日期", "商品编码", "商品描述", "规格", "生产厂家", "门店编号", "门店名称", "单位",
            "大类", "大类名称", "中类", "中类名称", "商品分类", "分类名称", "仓库成本价", "门店属性", "店效级别", "建议铺货量", "确认铺货量", "实际铺货量"};

    /**
     * 门店必备满足度权限查询前缀   ESSENTIAL_PRODUCT_
     */
    String ESSENTIAL_PRODUCT_AUTH_PARAM_PREFIX = "ESSENTIAL_PRODUCT_";

    /**
     * 门店必备满足度铺货量成本价界值   ESSENTIAL_PRODUCT_COST_PRICE_CRITICAL_VALUE   默认：100
     */
    String ESSENTIAL_PRODUCT_COST_PRICE_CRITICAL_KEY = "ESSENTIAL_PRODUCT_COST_PRICE_CRITICAL_VALUE";

    /**
     * 门店必备满足度铺货量（≥成本价界值）  ESSENTIAL_PRODUCT_PUSH_MAX_VALUE    默认值：2
     */
    String ESSENTIAL_PRODUCT_PUSH_MAX_KEY = "ESSENTIAL_PRODUCT_PUSH_MAX_VALUE";

    /**
     * 门店必备满足度铺货量（<成本价界值） ESSENTIAL_PRODUCT_PUSH_MIN_VALUE    默认值：3
     */
    String ESSENTIAL_PRODUCT_PUSH_MIN_KEY = "ESSENTIAL_PRODUCT_PUSH_MIN_VALUE";


    /**
     * 新店铺货明细导出Excel表头
     */
    String[] NEW_STORE_DISTRIBUTION_EXPORT_HEAD = {"序号", "商品编码", "大类", "大类名称", "中类", "中类名称", "商品分类", "分类名称",
            "商品描述", "规格", "生产厂家", "单位", "门店编号", "门店名称", "门店属性", "店效级别", "参考成本价",  "中包装", "公司店均月销量",
            "门店店均月销量", "仓库量", "本店库存", "建议铺货量"};

    /**
     * 新店铺货明细导出Sheet名称
     */
    String NEW_STORE_DISTRIBUTION_EXPORT_SHEET_NAME = "新店铺货明细";
}

