package com.gys.entity.data.xhl.vo;

import lombok.Data;

import java.io.PrintWriter;
import java.util.Date;

/**
 * @Auther: tzh
 * @Date: 2021/12/19 16:47
 * @Description: ReplenishVo
 * @Version 1.0.0
 */
@Data
public class ReplenishVo {
    private String client;
    private String gsrhBrId;
    private String gsrhDate;
    private Date tjDate;
    // 门店数量
    private Integer num;
    // 周
    private Integer weekNum;
    // 月
    private Integer monthNum;
    private Integer yearNum;
}
