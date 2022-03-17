package com.gys.entity.wk.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Auther: tzh
 * @Date: 2021/12/30 11:08
 * @Description: MedicinalQualityRecordDto
 * @Version 1.0.0
 */
@Data
public class MedicinalQualityRecordQueryDto {

    /**
     * 加盟商
     */
    private String client;
    /**
     * 编号
     */
    private String voucherId;

    /**
     * 商品查询
     */
    private String product;


    /**
     * 商品剂型
     */
    private String proDosage;

    /**
     * 生产厂家
     */
    private String factoryName;


    /**
     * 商品类别
     */
    private String proCategory;


    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不为空")
    private String  beginTime;

    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不为空")
    private String  endTime;

    /**
     * 创建者
     */
    private String createUser;
    /**
     * 页数
     */
    private Integer pageNum;
    /**
     *  页大小
     */
    private Integer pageSize;


}
