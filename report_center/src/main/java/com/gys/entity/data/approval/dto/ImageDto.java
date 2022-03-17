package com.gys.entity.data.approval.dto;

import lombok.Data;

import java.util.List;

/**
 * @Auther: tzh
 * @Date: 2021/12/6 16:03
 * @Description: ImageDto
 * @Version 1.0.0
 */
@Data
public class ImageDto {
    private String client;
    private  List<UrlDto> dtos;
}
