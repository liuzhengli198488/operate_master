package com.gys.entity.data.consignment.vo;

import lombok.Data;

import java.util.List;

/**
 * @Auther: tzh
 * @Date: 2021/12/8 14:56
 * @Description: StoreReport
 * @Version 1.0.0
 */
@Data
public class StoreReport {
    private List<StoreReportVo> reportVos;
    private StoreReportTotalVo reportTotalVo;
}
