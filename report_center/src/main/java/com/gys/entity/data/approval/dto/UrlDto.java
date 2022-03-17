package com.gys.entity.data.approval.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Auther: tzh
 * @Date: 2021/12/6 15:28
 * @Description: UrlDto
 * @Version 1.0.0
 */
@Data
public class UrlDto {
    @NotBlank(message = "商品自编码不为空")
    private String proSelfCode;
    @NotBlank(message = "url 不为空")
    private String url;
    @NotBlank(message = "门店不为空")
    private String proSite;
    @NotBlank(message = "商品名不为空")
    private String proName;
}
