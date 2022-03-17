package com.gys.common.data;

import cn.hutool.core.util.StrUtil;
import com.gys.util.csv.annotation.CsvCell;
import com.gys.util.csv.annotation.CsvRow;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description: TODO
 * @author: flynn
 * @date: 2021年11月24日 下午4:09
 */
@Data
@CsvRow("员工提成明细")
public class EmpSaleDetailResVo implements Serializable {

    private static final long serialVersionUID = -2947055699095024592L;

    private String client;

    @CsvCell(title = "开始时间", index = 2, fieldNo = 1)
    @ApiModelProperty(value = "开始时间")
    private String startDate;
    @CsvCell(title = "结束时间", index = 3, fieldNo = 1)
    @ApiModelProperty(value = "结束时间")
    private String endDate;

    @CsvCell(title = "方案名称", index = 1, fieldNo = 1)
    @ApiModelProperty(value = "方案名称")
    private String planName;

    @ApiModelProperty(value = "方案id")
    private String planId;

    @ApiModelProperty(value = "提成方案类型 1.销售 2.商品")
    private String type;

    @CsvCell(title = "提成类型", index = 4, fieldNo = 1)
    private String typeValue;

    public String getTypeValue() {
        String res = "";
        if(StrUtil.isNotBlank(type)){
            if("1".equals(type)){
                res = "销售提成";
            }else if("2".equals(type)){
                res = "单品提成";
            }
        }
        return res;
    }

    @CsvCell(title = "子方案名称", index = 5, fieldNo = 1)
    @ApiModelProperty(value = "子方案名称")
    private String CPlanName;

    @ApiModelProperty(value = "子方案id")
    private String CPlanId;

    @CsvCell(title = "营业员工号", index = 6, fieldNo = 1)
    @ApiModelProperty(value = "营业员工号")
    private String salerId;

    @CsvCell(title = "营业员姓名", index = 7, fieldNo = 1)
    @ApiModelProperty(value = "营业员姓名")
    private String salerName;

    @CsvCell(title = "门店编码", index = 8, fieldNo = 1)
    @ApiModelProperty(value = "门店编码")
    private String stoCode;

    @CsvCell(title = "门店名称", index = 9, fieldNo = 1)
    @ApiModelProperty(value = "门店名称")
    private String stoName;

    @CsvCell(title = "销售日期", index = 10, fieldNo = 1)
    @ApiModelProperty(value = "销售日期")
    private String saleDate;

    @CsvCell(title = "销售单号", index = 11, fieldNo = 1)
    @ApiModelProperty(value = "销售单号")
    private String gssdBillNo;

    @CsvCell(title = "商品编码", index = 12, fieldNo = 1)
    @ApiModelProperty(value = "商品编码")
    private String proSelfCode;

    @ApiModelProperty(value = "行号")
    private String serial;

    @CsvCell(title = "商品名称", index = 13, fieldNo = 1)
    @ApiModelProperty(value = "商品名称")
    private String proName;

    @CsvCell(title = "通用名称", index = 14, fieldNo = 1)
    @ApiModelProperty(value = "通用名称")
    private String proCommonName;

    @CsvCell(title = "规格", index = 15, fieldNo = 1)
    @ApiModelProperty(value = "规格")
    private String proSpecs;

    @CsvCell(title = "单位", index = 16, fieldNo = 1)
    @ApiModelProperty(value = "单位")
    private String proUnit;

    @CsvCell(title = "生产厂家", index = 17, fieldNo = 1)
    @ApiModelProperty(value = "生产厂家")
    private String factoryName;

    @CsvCell(title = "零售价", index = 18, fieldNo = 1)
    @ApiModelProperty(value = "参考零售价")
    private String proLsj;

    @CsvCell(title = "批号", index = 19, fieldNo = 1)
    @ApiModelProperty(value = "生产批号")
    private String batBatchNo;

//    @CsvCell(title = "有效期至", index = 20, fieldNo = 1)
    @ApiModelProperty(value = "有效期至")
    private String batExpiryDate;
    @CsvCell(title = "有效期至", index = 20, fieldNo = 1)
    private String gssdValidDate;

//    @CsvCell(title = "效期天数", index = 21, fieldNo = 1)
    @ApiModelProperty(value = "效期天数")
    private Integer validDateDays;


    @CsvCell(title = "销售数量", index = 22, fieldNo = 1)
    @ApiModelProperty(value = "销售数量")
    private String qyt;

    @CsvCell(title = "成本额", index = 23, fieldNo = 1)
    @ApiModelProperty(value = "成本额")
    private String costAmt;

    @CsvCell(title = "应收金额", index = 24, fieldNo = 1)
    @ApiModelProperty(value = "应收金额")
    private String ysAmt;

    @CsvCell(title = "实收金额", index = 25, fieldNo = 1)
    @ApiModelProperty(value = "实收金额")
    private String amt;

    @CsvCell(title = "毛利额", index = 26, fieldNo = 1)
    @ApiModelProperty(value = "毛利额")
    private String grossProfitAmt;

    @CsvCell(title = "毛利率", index = 26, fieldNo = 1)
    @ApiModelProperty(value = "毛利率")
    private String grossProfitRate;

    @CsvCell(title = "折扣金额", index = 27, fieldNo = 1)
    @ApiModelProperty(value = "折扣金额")
    private String zkAmt;

    @CsvCell(title = "折扣率", index = 28, fieldNo = 1)
    @ApiModelProperty(value = "折扣率")
    private String zkl;

    @CsvCell(title = "提成金额", index = 29, fieldNo = 1)
    @ApiModelProperty(value = "提成金额")
    private String tiTotal;

    @CsvCell(title = "提成销售占比", index = 30, fieldNo = 1)
    @ApiModelProperty(value = "提成销售占比")
    private String deductionWageAmtRate;
    @CsvCell(title = "提成毛利占比", index = 31, fieldNo = 1)
    @ApiModelProperty(value = "提成毛利占比")
    private String deductionWageGrossProfitRate;

    @CsvCell(title = "收银员编号", index = 32, fieldNo = 1)
    @ApiModelProperty(value = "收银员编号")
    private String gsshEmpId;

    @CsvCell(title = "收银员姓名", index = 33, fieldNo = 1)
    @ApiModelProperty(value = "收银员姓名")
    private String gsshEmpName;

    @CsvCell(title = "医生工号", index = 34, fieldNo = 1)
    @ApiModelProperty(value = "医生工号")
    private String doctorId;

    @CsvCell(title = "医生姓名", index = 35, fieldNo = 1)
    @ApiModelProperty(value = "医生姓名")
    private String doctorName;

    //商品大类编码
    @CsvCell(title = "商品大类编码", index = 36, fieldNo = 1)
    @ApiModelProperty(value = "商品大类编码")
    private String proBigClassCode;
    @CsvCell(title = "商品中类编码", index = 37, fieldNo = 1)
    @ApiModelProperty(value = "商品中类编码")
    private String proMidClassCode;
    @CsvCell(title = "商品分类编码", index = 38, fieldNo = 1)
    @ApiModelProperty(value = "商品分类编码")
    private String proClassCode;

    @CsvCell(title = "销售级别", index = 39, fieldNo = 1)
    @ApiModelProperty(value = "销售级别")
    private String proSlaeClass;

    @CsvCell(title = "商品定位", index = 40, fieldNo = 1)
    @ApiModelProperty(value = "商品定位")
    private String proPosition;

    @CsvCell(title = "业务员", index = 41, fieldNo = 1)
    @ApiModelProperty(value = "业务员")
    private String batSupplierSalesman;

    /**
     * 供应商编码
     */
    @CsvCell(title = "供应商编码", index = 42, fieldNo = 1)
    @ApiModelProperty(value = "供应商编码")
    private String batSupplierCode;
    /**
     * 供应商名称
     */
    @CsvCell(title = "供应商名称", index = 43, fieldNo = 1)
    @ApiModelProperty(value = "供应商名称")
    private String batSupplierName;

    @ApiModelProperty(value = "是否不显示成本， true是，false否")
    private Boolean notShowAmt;

}

