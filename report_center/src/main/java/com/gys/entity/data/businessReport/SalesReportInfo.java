package com.gys.entity.data.businessReport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


/**
 * 出参
 *
 * @author XiaoZY
 * @date 2021/4/1
 */
@Data
public class SalesReportInfo {

    private List<WeeklySalesReportInfo> list;

    private WeeklySalesReportInfo total;
}
