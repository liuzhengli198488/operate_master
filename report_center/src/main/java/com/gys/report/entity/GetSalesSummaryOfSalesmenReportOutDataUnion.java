package com.gys.report.entity;

import cn.hutool.core.util.StrUtil;
import com.gys.util.csv.annotation.CsvCell;
import com.gys.util.csv.annotation.CsvRow;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
@CsvRow("联合用药-员工")
public class GetSalesSummaryOfSalesmenReportOutDataUnion extends GetSalesSummaryOfSalesmenReportOutData{

    //交易次数

    private Integer jycs=0;

    //销售品项
    private Integer xspx=0;

    //客品次
    @CsvCell(title = "客品次", index = 7, fieldNo = 1)
    private String  kpc = "0";

    public String getKpc() {
        String res = "0";
        if(StrUtil.isBlank(kpc)){
            return res;
        }
        return kpc;
    }

    //品单价
    @CsvCell(title = "品单价", index = 8, fieldNo = 1)
    private String pdj = "0";

    public String getPdj() {
        String res = "0";
        if(StrUtil.isBlank(pdj)){
            return res;
        }
        return pdj;
    }

    @ApiModelProperty(value = "输入商品编码次数")
    private Integer inputCount = 0;

    @ApiModelProperty(value = "弹出关联商品次数")
    @CsvCell(title = "关联弹出次数", index = 9, fieldNo = 1)
    private Integer unitCount = 0;


    private BigDecimal ejectRate  = BigDecimal.ZERO;//弹出率

    @CsvCell(title = "弹出率", index = 10, fieldNo = 1)
    private String ejectRateStr;

    public String getEjectRateStr() {
        String res = "0%";
        if(this.ejectRate!=null && ejectRate.compareTo(BigDecimal.ZERO)!=0){
            res = ejectRate.toPlainString() + "%";
        }
        return res;
    }

    //    public String getEjectRate() {
//        BigDecimal res = new BigDecimal(0);
//        if(inputCount!=null && unitCount!=null && inputCount!=0 && unitCount!=0){
//            res = new BigDecimal(this.unitCount).divide(new BigDecimal(this.inputCount),4, RoundingMode.HALF_UP).setScale(2,RoundingMode.UP).multiply(new BigDecimal(100));
//        }
//        return res.intValue() + "";
//    }

    @ApiModelProperty(value = "成交次数")
    private Integer businessCount = 0;

    @CsvCell(title = "关联成交次数", index = 11, fieldNo = 1)
    @ApiModelProperty(value = "成交次数-联合用药")
    private Integer unionBusinessCount =0;

    private BigDecimal businessCountRate  = BigDecimal.ZERO;//成交率

    @CsvCell(title = "成交率", index = 12, fieldNo = 1)
    private String businessCountRateStr;//成交率

    public String getBusinessCountRateStr() {
        String res = "0%";
        if(this.businessCountRate!=null && businessCountRate.compareTo(BigDecimal.ZERO)!=0){
            res = businessCountRate.toPlainString() + "%";
        }
        return res;
    }

    //    public String getBusinessCountRate() {
//        BigDecimal res = new BigDecimal(0);
//        if(unionBusinessCount!=null && businessCount!=null && unionBusinessCount!=0 && businessCount!=0){
//            res = new BigDecimal(this.unionBusinessCount).divide(new BigDecimal(this.businessCount),4, RoundingMode.HALF_UP).setScale(2,RoundingMode.UP).multiply(new BigDecimal(100));
//        }
//        return res.intValue() + "";
//    }

    @ApiModelProperty(value = "成交金额")
    private BigDecimal businessAmt  = BigDecimal.ZERO;

    @CsvCell(title = "关联销售金额", index = 13, fieldNo = 1)
    @ApiModelProperty(value = "成交金额-联合用药")
    private BigDecimal unionBusinessAmt  = BigDecimal.ZERO;


    private BigDecimal unionBusinessAmtRate  = BigDecimal.ZERO;
    @CsvCell(title = "关联销售占比", index = 14, fieldNo = 1)
    private String unionBusinessAmtRateStr;

    public String getUnionBusinessAmtRateStr() {
        String res = "0%";
        if(this.unionBusinessAmtRate!=null && unionBusinessAmtRate.compareTo(BigDecimal.ZERO)!=0){
            res = unionBusinessAmtRate.toPlainString() + "%";
        }
        return res;
    }

    //    public String getUnionBusinessAmtRate() {
//        BigDecimal res = new BigDecimal(0);
//        if(businessAmt!=null && unionBusinessAmt!=null && businessAmt.intValue()!=0 && unionBusinessAmt.intValue()!=0){
//            res = unionBusinessAmt.divide(businessAmt,4, RoundingMode.HALF_UP).setScale(2,RoundingMode.UP).multiply(new BigDecimal(100));
//        }
//        return res.intValue() + "";
//    }

    private String siteCode;

    private String client;

    private BigDecimal totalAmt = BigDecimal.ZERO;

    private Integer totalQyt = 0;


    private BigDecimal glcjcs  = BigDecimal.ZERO;//关联成交次数

    private BigDecimal ygltcxpcs = BigDecimal.ZERO;//有关联弹出的小票张数

}
