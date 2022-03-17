package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zhangdong
 * @date 2021/7/21 15:25
 */
@Data
public class WebOrderQueryBean {

    @ApiModelProperty("门店(门店编码或门店名称)--汇总查询时用到")
    private String[] stoCodeOrName;
    @ApiModelProperty("商品编码--明细查询时用到；可以是多个，用,隔开")
    private String proId;
    @ApiModelProperty("商品编码List，前端无需传值")
    private List<String> proIdList;
    @ApiModelProperty("门店编码List，前端无需传值")
    private List<String> stoCodeList;
    @ApiModelProperty("平台")
    private String platform;
    @ApiModelProperty("创建日期-起始日期, 格式: yyyy-MM-dd")
    private String createStartDate;
    @ApiModelProperty("创建日期-截至日期，格式: yyyy-MM-dd")
    private String createEndDate;
    @ApiModelProperty("平台单号")
    private String platformOrderId;
    @ApiModelProperty("本地单号")
    private String orderId;
    @ApiModelProperty("销售单号")
    private String saleOrderId;

    private String client;
    @ApiModelProperty("第几页，默认从1开始")
    private Integer pageNum = 1;
    @ApiModelProperty("每页多少数据量，默认:100")
    private Integer pageSize = 100;

}
