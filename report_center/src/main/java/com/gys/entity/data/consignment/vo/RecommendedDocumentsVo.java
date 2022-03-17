package com.gys.entity.data.consignment.vo;

import com.gys.report.entity.GetPayTypeOutData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;

@Data

public class RecommendedDocumentsVo {
    private BigDecimal amountReceivable;
    private BigDecimal amt;
    private String billNo;
    private String cashier;
    private String  cashierName;
    private String datePart;
    private BigDecimal grossProfitMargin;
    private String  grossProfitRate;
    private String  grossProfitRateStr;
    private  BigDecimal  movPrices;
    private  String payName;
    private BigDecimal s1000;
    private BigDecimal s2000;
    private BigDecimal s2002;
    private BigDecimal s2004;
    private BigDecimal s3001;
    private BigDecimal s3002;
    private BigDecimal s3003;
    private BigDecimal s3004;
    private BigDecimal s4000;
    private BigDecimal s6001;
    private BigDecimal s7007;
    private BigDecimal s9006;
    private String saleDate;
    private String stoCode;
    private String stoName;
    private String recommendCode;
    private String recommendName;
    private BigDecimal zkDyq;
    private BigDecimal zkDzq;
    private BigDecimal zkJfdh;
    private BigDecimal zkJfdx;
    private BigDecimal zkPm;
}
