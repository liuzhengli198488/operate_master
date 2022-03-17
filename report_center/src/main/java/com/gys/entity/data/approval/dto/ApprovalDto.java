package com.gys.entity.data.approval.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Auther: tzh
 * @Date: 2021/12/3 15:06
 * @Description: ApprovalDto
 * @Version 1.0.0
 */
@Data
public class ApprovalDto {
    //商品编码
    private String proSelfCode;

    //商品名称
    private String proName;

    //助记码
    private String proPym;

    //出库单号
    private String deliveryNumber;

    /**
     *拣货单号
     */
    private String  whereWmJhdh;

    private String client;

    private List<String> proCodes;
    private List<String> clients;
    @ApiModelProperty(value = "分页页码", example = "1")
    private Integer pageNum;
    @ApiModelProperty(value = "分页大小:默认20", example = "20")
    private Integer pageSize;
}
