package com.gys.entity.data.dropdata;

import com.gys.common.enums.ReplenishStyleEnum;
import com.gys.entity.GaiaViewWmsTjXhl;
import com.gys.util.csv.annotation.CsvCell;
import com.gys.util.csv.annotation.CsvRow;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 获取已过账的下货率详情列表出参
 */
@Data
@NoArgsConstructor
@CsvRow
public class GetHistoryPostedUnloadRateDetailListVO {

    public GetHistoryPostedUnloadRateDetailListVO(GaiaViewWmsTjXhl gaiaViewWmsTjXhl) {
        this.customerNo = gaiaViewWmsTjXhl.getCustomerno();//客户号
        this.customerName = gaiaViewWmsTjXhl.getCustomername();//客户名称
        this.pleaseOrderDate = gaiaViewWmsTjXhl.getPleaseorderdate();//请货日期
        this.deliveryOrderNo = gaiaViewWmsTjXhl.getDeliveryorderno();//配送订单号
        this.openOrderDate = gaiaViewWmsTjXhl.getOpenorderdate();//开单日期
        this.deliveryNo = gaiaViewWmsTjXhl.getDeliveryno();//配送单号
        this.isPost = gaiaViewWmsTjXhl.getIspost();//是否过账,0:是,1:否
        this.commodityCategory = gaiaViewWmsTjXhl.getCommoditycategory();//商品大类
        this.commodityCode = gaiaViewWmsTjXhl.getCommoditycode();//商品编码
        this.commodityName = gaiaViewWmsTjXhl.getCommodityname();//商品名称
        this.specifications = gaiaViewWmsTjXhl.getSpecifications();//规格
        this.manufacturer = gaiaViewWmsTjXhl.getManufacturer();//厂家
        this.unit = gaiaViewWmsTjXhl.getUnit();//单位
        this.postedDate = gaiaViewWmsTjXhl.getPostingdate();//过账日期
        this.clientId = gaiaViewWmsTjXhl.getClientId();//药德客户ID
        this.clientName = gaiaViewWmsTjXhl.getClientName();//药德客户名称
        this.dcId = gaiaViewWmsTjXhl.getDcId();//配送中心ID
        this.dcName = gaiaViewWmsTjXhl.getDcName();//配送中心名称
        this.replenishStyle = gaiaViewWmsTjXhl.getReplenishStyle();//补货方式类型
        this.replenishStyleStr = gaiaViewWmsTjXhl.getReplenishStyleStr();//补货方式名称
        this.orderQuantity = gaiaViewWmsTjXhl.getOrderquantity();//订单数量
        this.deliveryQuantity = gaiaViewWmsTjXhl.getDeliveryquantity();//配货单原数量
        this.deliveryPostedQuantityRate = gaiaViewWmsTjXhl.getDeliverypostedquantityrate();//配送单过账数量
        this.matmovprice = gaiaViewWmsTjXhl.getOrderPrice();//移动平均价
        if(Objects.equals(gaiaViewWmsTjXhl.getReplenishStyle(), ReplenishStyleEnum.NORMAL_REPLENISHMENT.type)){
            this.replenishStyleStr = ReplenishStyleEnum.NORMAL_REPLENISHMENT.name;
        }else if(Objects.equals(gaiaViewWmsTjXhl.getReplenishStyle(), ReplenishStyleEnum.EMERGENCY_REPLENISHMENT.type)){
            this.replenishStyleStr = ReplenishStyleEnum.EMERGENCY_REPLENISHMENT.name;
        }else if(Objects.equals(gaiaViewWmsTjXhl.getReplenishStyle(), ReplenishStyleEnum.SHOP_GOODS.type)){
            this.replenishStyleStr = ReplenishStyleEnum.SHOP_GOODS.name;
        }else if(Objects.equals(gaiaViewWmsTjXhl.getReplenishStyle(), ReplenishStyleEnum.INTERMODULATION.type)){
            this.replenishStyleStr = ReplenishStyleEnum.INTERMODULATION.name;
        }else if(Objects.equals(gaiaViewWmsTjXhl.getReplenishStyle(), ReplenishStyleEnum.DIRECT_DELIVERY.type)){
            this.replenishStyleStr = ReplenishStyleEnum.DIRECT_DELIVERY.name;
        }
    }
    //序号
    @CsvCell(title = "序号", index = 1, fieldNo = 1)
    private String number;
    //药德客户id
    @CsvCell(title = "药德客户ID", index = 2, fieldNo = 1)
    private String clientId;

    //药德客户名称
    @CsvCell(title = "药德客户名称", index = 3, fieldNo = 1)
    private String clientName;

    //配送中心id
    @CsvCell(title = "配送中心ID", index = 4, fieldNo = 1)
    private String dcId;

    //配送中心名称
    @CsvCell(title = "配送中心名称", index = 5, fieldNo = 1)
    private String dcName;

    //补货方式id
    private String replenishStyle;

    //补货方式中文名称
    @CsvCell(title = "补货方式", index = 6, fieldNo = 1)
    private String replenishStyleStr;

    //客户号
    @CsvCell(title = "客户号", index = 7, fieldNo = 1)
    private String customerNo;

    //客户名称
    @CsvCell(title = "客户名称", index = 8, fieldNo = 1)
    private String customerName;

    //请货日期
    @CsvCell(title = "请货日期", index = 9, fieldNo = 1)
    private String pleaseOrderDate;

    //配送订单号
    @CsvCell(title = "配送订单号", index = 10, fieldNo = 1)
    private String deliveryOrderNo;

    //开单日期
    @CsvCell(title = "开单日期", index = 11, fieldNo = 1)
    private String openOrderDate;

    //配送单号
    @CsvCell(title = "配送单号", index = 12, fieldNo = 1)
    private String deliveryNo;

    //是否过账
    private String isPost;

    //商品大类
    @CsvCell(title = "商品大类", index = 13, fieldNo = 1)
    private String commodityCategory;

    //商品编码
    @CsvCell(title = "商品编码", index = 14, fieldNo = 1)
    private String commodityCode;

    //商品名称
    @CsvCell(title = "商品名称", index = 15, fieldNo = 1)
    private String commodityName;

    //规格
    @CsvCell(title = "规格", index = 15, fieldNo = 1)
    private String specifications;

    //厂家
    @CsvCell(title = "厂家", index = 16, fieldNo = 1)
    private String manufacturer;

    //单位
    @CsvCell(title = "单位", index = 17, fieldNo = 1)
    private String unit;

    //订单数量
    @CsvCell(title = "订单数量", index = 18, fieldNo = 1)
    private BigDecimal orderQuantity = BigDecimal.ZERO;

    //配送单原数量
    @CsvCell(title = "配送单原数量", index = 19, fieldNo = 1)
    private BigDecimal deliveryQuantity  = BigDecimal.ZERO;

    //配货数量满足率
    @CsvCell(title = "配货数量满足率", index = 20, fieldNo = 1)
    private String deliveryQuantitySatisfiedRate= "0.00%";

    //配送金额满足率
    @CsvCell(title = "配送金额满足率", index = 21, fieldNo = 1)
    private String deliveryAmountSatisfiedRate= "0.00%";

    //配送品项满足率
    @CsvCell(title = "配送品项满足率", index = 22, fieldNo = 1)
    private String deliveryItemSatisfiedRate;

    //配送单过账数量
    @CsvCell(title = "配送单过账数量", index = 23, fieldNo = 1)
    private BigDecimal deliveryPostedQuantityRate  = BigDecimal.ZERO;

    //过账日期
    @CsvCell(title = "过账日期", index = 24, fieldNo = 1)
    private String postedDate;

    //发货数量满足率
    @CsvCell(title = "发货数量满足率", index = 25, fieldNo = 1)
    private String shipmentQuantitySatisfiedRate= "0.00%";

    //发货金额满足率
    @CsvCell(title = "发货金额满足率", index = 26, fieldNo = 1)
    private String shipmentAmountSatisfiedRate= "0.00%";

    //发货品项满足率
    @CsvCell(title = "发货品项满足率", index = 27, fieldNo = 1)
    private String shipmentItemSatisfiedRate;

    //最终数量满足率
    @CsvCell(title = "最终数量满足率", index = 28 ,fieldNo = 1)
    private String finalQuantitySatisfiedRate= "0.00%";

    //最终金额满足率
    @CsvCell(title = "最终金额满足率", index = 29 ,fieldNo = 1)
    private String finalAmountSatisfiedRate = "0.00%";

    //最终品项满足率
    @CsvCell(title = "最终品项满足率", index = 30 ,fieldNo = 1)
    private String finalItemSatisfiedRate;

    //移动平均价
    //@CsvCell(title = "移动平均价", index = 31 ,fieldNo = 1)
    private BigDecimal matmovprice = BigDecimal.ZERO;




    /*====================================*/
    //订单数量合计

    private BigDecimal orderQuantityTotal;
    //配送单原数量合计

    private BigDecimal deliveryQuantityTotal;
    //配货数量满足率合计

    private String deliveryQuantitySatisfiedRateTotal;
    //配送金额满足率合计

    private String deliveryAmountSatisfiedRateTotal;
    //配货品相满足率合计

    private String deliveryItemSatisfiedRateTotal;
    //配送单过账数量合计

    private BigDecimal deliveryPostedQuantityRateTotal;
    //发货数量满足率合计

    private String shipmentQuantitySatisfiedRateTotal;
    //发货金额满足率合计

    private String shipmentAmountSatisfiedRateTotal;
    //发货品项满足率合计

    private String shipmentItemSatisfiedRateTotal;
    //最终数量满足率合计
    private String finalQuantitySatisfiedRateTotal;
    //最终金额满足率合计
    private String finalAmountSatisfiedRateTotal;
    //最终品项满足率合计
    private String finalItemSatisfiedRateTotal;
}
