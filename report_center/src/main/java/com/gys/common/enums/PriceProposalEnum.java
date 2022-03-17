package com.gys.common.enums;

public enum PriceProposalEnum {

    PRODUCT_PRICE_PROPOSAL_CODE("价格建议", "GAIA_MM_010313"),
    PRODUCT_PRICE_PROPOSAL_PAGE("价格建议", "priceProposal");

    private String name;

    private String code;

    PriceProposalEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
