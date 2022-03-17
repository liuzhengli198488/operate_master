package com.gys.entity;

import lombok.Data;

@Data
public class ProductMatchStatus {
    private boolean barCode;
    private boolean registeredNumber;
    private boolean commName;
    private boolean spec;
    private boolean factory;
    private String matchedCode;
    private String matchValue;
    private String proBarCode;
}
