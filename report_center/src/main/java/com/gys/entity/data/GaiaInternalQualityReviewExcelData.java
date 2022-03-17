package com.gys.entity.data;

import com.gys.entity.GaiaInternalQualityReview;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author csm
 * @date 1/4/2022 - 6:09 PM
 */
@Data
public class GaiaInternalQualityReviewExcelData extends GaiaInternalQualityReview {
    @ApiModelProperty(value= "序号")
    private Integer index;

}
