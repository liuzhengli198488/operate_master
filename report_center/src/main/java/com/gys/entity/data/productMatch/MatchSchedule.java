package com.gys.entity.data.productMatch;

import lombok.Data;

@Data
public class MatchSchedule {
    private Integer totalLine;
    private Integer matchedLine;
    private Integer unmatchLine;
    private String matchSchedule;
}
