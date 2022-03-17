package com.gys.entity.data.xhl.dto;

import lombok.Data;

import java.util.List;

/**
 * @Auther: tzh
 * @Date: 2021/12/16 00:15
 * @Description: QueryDto
 * @Version 1.0.0
 */
@Data
public class QueryDto {
   private Integer pageNum;
   private Integer pageSize;
   private List<String> clients;
}
