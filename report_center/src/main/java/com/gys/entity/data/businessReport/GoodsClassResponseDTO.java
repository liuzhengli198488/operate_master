package com.gys.entity.data.businessReport;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
public class GoodsClassResponseDTO {

    private String proCompBigCode;
    private String proCompBigName;
    private String proCompMidCode;
    private String proCompMidName;
    private String proCompLitCode;
    private String proCompLitName;
    private String proCompCode;
    private String proCompName;

    /**
     * 下一级列表
     */
    private List<GoodsClassResponseDTO> list ;


}
