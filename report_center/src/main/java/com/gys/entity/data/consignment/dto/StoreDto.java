package com.gys.entity.data.consignment.dto;

import com.gys.entity.data.consignment.vo.StoreVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @Auther: tzh
 * @Date: 2021/12/6 17:55
 * @Description: StoreRecommendedSaleDto  门店推荐销售
 * @Version 1.0.0
 */

@Data
public class StoreDto {
    // 0 是未推荐成功  1是推荐成功的
    private Integer tag;
    private String client;

}
