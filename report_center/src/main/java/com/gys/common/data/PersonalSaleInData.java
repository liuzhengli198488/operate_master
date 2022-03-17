package com.gys.common.data;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @Author ：liuzhiwen.
 * @Date ：Created in 13:56 2021/9/8
 * @Description：
 * @Modified By：liuzhiwen.
 * @Version:
 */
@Data
public class PersonalSaleInData {
    /**
     * 加盟商
     */
    private String client;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 查询类型 1：日、2：周、3：月
     */
    @NotBlank(message = "查询类型不能为空！")
    @Pattern(regexp = "[1-3]",message = "查询类型只能传1、2、3")
    private String type;
}
