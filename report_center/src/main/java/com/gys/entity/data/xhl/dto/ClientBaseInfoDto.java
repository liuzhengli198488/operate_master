package com.gys.entity.data.xhl.dto;

import com.gys.entity.data.xhl.vo.ClientBaseInfoVo;
import lombok.Data;

import java.util.List;

/**
 * @Auther: tzh
 * @Date: 2021/12/15 23:56
 * @Description: ClientBaseInfoVo
 * @Version 1.0.0
 */
@Data
public class ClientBaseInfoDto {
     private List<ClientBaseInfoVo> voList;
}
