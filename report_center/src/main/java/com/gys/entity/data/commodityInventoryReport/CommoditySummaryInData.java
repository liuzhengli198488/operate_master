package com.gys.entity.data.commodityInventoryReport;

import com.gys.entity.ProvCityInData;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @Author ：liuzhiwen.
 * @Date ：Created in 19:55 2021/11/24
 * @Description：
 * @Modified By：liuzhiwen.
 * @Version:
 */
@Data
public class CommoditySummaryInData extends ProvCityInData {
    /**
     * 加盟商列表
     */
    private List<String> clientList;

    /**
     * 调库单
     */
    private String billCode;

    /**
     * 开始调库日期
     */
    @NotBlank(message = "提示：请选择调库时间")
    private String startBillDate;

    /**
     * 结束调库日期
     */
    @NotBlank(message = "提示：请选择调库时间")
    private String endBillDate;

    /**
     * 开始调库完成日期
     */
    private String startFinishDate;

    /**
     * 结束调库完成日期
     */
    private String endFinishDate;
}
