package com.gys.entity.data.approval.vo;

import lombok.Data;

/**
 * @Auther: tzh
 * @Date: 2021/12/3 15:19
 * @Description: ApprovalVo
 * @Version 1.0.0
 */
@Data
public class ApprovalVo {
    /*
    出库单号
     */
   private String deliveryNumber;
   /*
   商品编号
    */
   private String proSelfCode;
   /*
   商品名称
    */
   private String proName;
   /*
   规格
    */
   private String proSpecs;
   /*
   单位
    */
   private String proUnit;
   /*
   产地
    */
   private String proPlace;
   /*
   批件图片
    */
   private String proPicturePj;
   /**
    *拣货单号
    */
   private String  whereWmJhdh;

   private String client;

   private String proSite;
}
