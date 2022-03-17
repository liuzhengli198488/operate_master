package com.gys.common.data;

/**
 * 商品大类
 */
public enum CommodityClassification {
	RX("1", "处方药"),
	OTC("2", "OTC"),
	CHINESE_MEDICINE("3", "中药"),
	HEALTH_FOOD("4", "保健食品"),
	MEDICAL_EQUIPMENT("5", "医疗器械及护理"),
	PERSONAL_CARE ("6", "个人护理"),
	HOUSEHOLD_EFFECTS("7", "家庭用品"),
	COMPLIMENTARY("8", "赠品"),
	UNKNOWN("", "");

	@Override
	public String toString() {
		return "CommodityClassification{" +
			"value='" + value + '\'' +
			", comment='" + comment + '\'' +
			'}';
	}

	private String value;
	private String comment;

	CommodityClassification(String value, String comment) {
		this.value = value;
		this.comment = comment;
	}

	public static CommodityClassification getEnum(String value) {
		for (CommodityClassification t : CommodityClassification.values()) {
			if (t.getValue().equals(value) || t.name().equals(value)) {
				return t;
			}
		}

		// invalid value MUSt be passed
//		assert false : "CommodityClassification, Invalid value: " + value;
		return UNKNOWN;
	}

	public static String getNameByValue(String value) {
		for (CommodityClassification t : CommodityClassification.values()) {
			if (t.getValue().equals(value)) {
				return t.getComment();
			}
		}
		return value;
	}

	public String getComment() {
		return comment;
	}

	public String getValue() {
		return value;
	}

	boolean equals(String value) {
		return getValue().equals(value) || name().equals(value);
	}

	boolean equals(CommodityClassification other) {
		assert other != null : "other passed is NULL!";
		return this == other || getValue().equals(other.getValue());
	}
}

