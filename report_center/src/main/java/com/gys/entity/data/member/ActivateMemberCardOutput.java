package com.gys.entity.data.member;

import com.gys.util.csv.annotation.CsvCell;
import com.gys.util.csv.annotation.CsvRow;
import lombok.Data;

/**
 * @author 胡鑫鑫
 */
@Data
@CsvRow("")
public class ActivateMemberCardOutput {
    /**
    序号
     */
    @CsvCell(title = "序号", index = 1, fieldNo = 1)
    private int index;
    /**
    门店号
     */
    @CsvCell(title = "门店编码", index = 2, fieldNo = 1)
    private String stoCode;
    /**
     * 门店名称
     */
    @CsvCell(title = "门店名称", index = 3, fieldNo = 1)
    private String stoName;
    /**
     * 预备会员激活数量
     */
    @CsvCell(title = "预备会员激活数量", index = 4, fieldNo = 1)
    private String qty;

}
