package com.gys.entity.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GaiaSalesPayMent {
    @ApiModelProperty(value = "支付类型")
    private String gsspmType;
    @ApiModelProperty(value = "支付名称")
    private String gsspmName;
    @ApiModelProperty(value = "门店编码")
    private String gsspmBrId;
    @ApiModelProperty(value = "支付金额")
    private String gsspmAmt;
    @ApiModelProperty(value = "支付编码")
    private String gsspmId;
}
