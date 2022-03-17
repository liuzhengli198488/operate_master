package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class GetReplenishDetailOutData implements Serializable {
    private static final long serialVersionUID = -5446464835263694493L;

    @ApiModelProperty(value = "加盟商")
    private String clientId;

    @ApiModelProperty(value = "补货门店")
    private String gsrdBrId;

    @ApiModelProperty(value = "补货门店名")
    private String gsrdBrName;

    @ApiModelProperty(value = "补货单号")
    private String gsrdVoucherId;

    @ApiModelProperty(value = "补货日期")
    private String gsrdDate;

    @ApiModelProperty(value = "行号")
    private String gsrdSerial;

    @ApiModelProperty(value = "商品编码")
    private String gsrdProId;

    @ApiModelProperty(value = "商品第三方编码")
    private String proThreeCode;

    @ApiModelProperty("第三方编码是否存在 0 不存在 1 存在  不存在则此行标红")
    private String hasProThreeCode;

    @ApiModelProperty(value = "移动平均价")
    private BigDecimal gsrdAverageCost;

    @ApiModelProperty(value = "毛利率 批次成本")
    private BigDecimal gsrdGrossMargin;

    @ApiModelProperty(value = "90天销量")
    private String gsrdSalesDays1;

    @ApiModelProperty(value = "30天销量")
    private String gsrdSalesDays2;

    @ApiModelProperty(value = "7天销量")
    private String gsrdSalesDays3;

    @ApiModelProperty(value = "建议补货量")
    private String gsrdProposeQty;

    @ApiModelProperty(value = "补货量")
    private String gsrdNeedQty;

    @ApiModelProperty(value = "订单价")
    private String voucherAmt;

    @ApiModelProperty(value = "本店库存")
    private String gsrdStockStore;

    @ApiModelProperty(value = "仓库库存")
    private String gsrdStockDepot;

    @ApiModelProperty(value = "最小陈列")
    private String gsrdDisplayMin;

    @ApiModelProperty(value = "中包装")
    private String gsrdPackMidsize;

    @ApiModelProperty(value = "商品名")
    private String gsrdProName;

    @ApiModelProperty(value = "商品通用名")
    private String gsrdProCommonName;

    @ApiModelProperty(value = "规格")
    private String gsrdProPec;

    @ApiModelProperty(value = "计量单位")
    private String gsrdProUnit;

    @ApiModelProperty(value = "生产企业")
    private String gsrdProCompany;

    @ApiModelProperty(value = "产地")
    private String gsrdProPlace;

    @ApiModelProperty(value = "零售价")
    private String gsrdProPrice;

    @ApiModelProperty(value = "序号")
    private Integer index;

    @ApiModelProperty(value = "商品仓储分区")
    private String proStorageArea;

    @ApiModelProperty(value = "中包装量")
    private String proMidPackage;

    @ApiModelProperty(value = "最后一次供应商编码")
    private String gsrdLastSupid;

    @ApiModelProperty(value = "最后一次供应商名")
    private String gsrdLastSupName;

    @ApiModelProperty(value = "剂型")
    private String gsrdpProForm;

    @ApiModelProperty(value = "保质期")
    private String gsrdProLife;

    @ApiModelProperty(value = "批准文号")
    private String gsrdPegisterNo;

    @ApiModelProperty(value = "补货门店数")
    private String stoNum;

    @ApiModelProperty(value = "补货供应商编码")
    private String gsrdSupid;

    @ApiModelProperty(value = "补货供应商")
    private String gsrdSupName;

    @ApiModelProperty(value = "采购订单单价")
    private String poPrice;

    @ApiModelProperty(value = "最后一次进货价")
    private String gsrdLastPrice;

    @ApiModelProperty(value = "进项税率编码")
    private String gsrdInputTax;

    @ApiModelProperty(value = "进项税率")
    private String gsrdInputTaxValue;

    @ApiModelProperty(value = "付款条件")
    private String gsrdPayTerm;

    @ApiModelProperty(value = "销项税率编码")
    private String gsrdTaxTate;

    @ApiModelProperty(value = "供应商编码1")
    private String supCode1;
    @ApiModelProperty(value = "供应商名称1")
    private String supName1;
    @ApiModelProperty(value = "供应商单价1")
    private String supPrice1;
    @ApiModelProperty(value = "供应商最后获取数据时间1")
    private String supUpdateDate1;
    @ApiModelProperty(value = "供应商编码2")
    private String supCode2;
    @ApiModelProperty(value = "供应商名称2")
    private String supName2;
    @ApiModelProperty(value = "供应商单价2")
    private String supPrice2;
    @ApiModelProperty(value = "供应商最后获取数据时间2")
    private String supUpdateDate2;
    @ApiModelProperty(value = "供应商编码3")
    private String supCode3;
    @ApiModelProperty(value = "供应商名称3")
    private String supName3;
    @ApiModelProperty(value = "供应商单价3")
    private String supPrice3;
    @ApiModelProperty(value = "供应商最后获取数据时间3")
    private String supUpdateDate3;
    @ApiModelProperty(value = "最低供应商编码")
    private String gsrdMinSupid;
    @ApiModelProperty(value = "最低供应商名")
    private String gsrdMinSupName;
    @ApiModelProperty(value = "最低进货价")
    private String gsrdMinPrice;
    @ApiModelProperty(value = "委托开单数量")
    private String gsrdDnQty;
    @ApiModelProperty(value = "商品定位")
    private String proPosition;
    @ApiModelProperty(value = "大类")
    private String bigClass;
    @ApiModelProperty(value = "中类")
    private String midClass;
    @ApiModelProperty(value = "小类")
    private String minClass;
    @ApiModelProperty(value = "禁止采购 0 不禁止 1 禁止")
    private String proNoPurchase;
    @ApiModelProperty(value = "是否手动 1 手工输入")
    private String gsrdFlag;
    @ApiModelProperty(value = "是否特殊商品 0 否 1 是")
    private String isSpecial;
    @ApiModelProperty(value = "配送中心编码")
    private String dcCode;
    @ApiModelProperty(value = "总库存")
    private String matTotalQty;
    @ApiModelProperty(value = "总金额")
    private String matTotalAmt;
    @ApiModelProperty(value = "移动平均价")
    private String matMovPrice;
    @ApiModelProperty(value = "税金")
    private String matRateAmt;
    private String apAddAmt;
    private String apAddRate;
    private String apCataloguePrice;
    private String addAmt;
    private String addRate;
    @ApiModelProperty(value = "加点后出库价")
    private String checkOutAmt;
    @ApiModelProperty("是否是中药")
    private String isChineseMedicine;
    @ApiModelProperty("是否医保 0 否 1 是")
    private String isMed;
    @ApiModelProperty("禁止采购 0 否 1 是")
    private String noPurchase;
    @ApiModelProperty("所有门店库存")
    private BigDecimal allStoreStock;
    @ApiModelProperty("在途量")
    private BigDecimal onWayNumber = BigDecimal.ZERO;

    @ApiModelProperty("是否原始单")
    private Boolean isOriginal = Boolean.TRUE;

    @ApiModelProperty("是否修改原始单")
    private Boolean isEditOriginal = Boolean.FALSE;
    @ApiModelProperty("返回页面商品集合")
    private List<GetReplenishDetailOutData> tableInfoList;
    @ApiModelProperty("重复商品集合")
    private List<GetReplenishDetailOutData> repeatInfoList;
}
