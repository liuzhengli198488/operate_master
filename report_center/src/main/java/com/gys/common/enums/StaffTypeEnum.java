package com.gys.common.enums;

import java.util.*;

/**
 * @author wu mao yin
 * @Description: 员工类型
 * @date 2021/12/3 14:01
 */
public enum StaffTypeEnum {

    /**
     * 1表示营业员 2表示收银员 3表示医生
     */
    CASHIER("2", "收银员", Arrays.asList("GAIA_SD_MDDR", "SD_01", "GAIA_SD_MDSY", "GAIA_SD_MDYS", "GAIA_SD_ZLGL")),
    ASSISTANT("1", "营业员", Arrays.asList("GAIA_SD_MDDR", "SD_01", "GAIA_SD_MDSY", "GAIA_SD_MDYS", "GAIA_SD_ZLGL")),
    DOCTOE("3", "医生", Arrays.asList("GAIA_SD_MDDR")),

    ;

    private String code;
    private String message;
    private List<String> groupId;

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public List<String> getGroupId() {
        return groupId;
    }

    public void setGroupId(List<String> groupId) {
        this.groupId = groupId;
    }

    StaffTypeEnum(String code, String message, List<String> groupId) {
        this.code = code;
        this.message = message;
        this.groupId = groupId;
    }

    public static List<String> getValue(String code) {
        for (StaffTypeEnum ele : values()) {
            if (ele.getCode().equals(code)) {
                return ele.getGroupId();
            }
        }
        return null;
    }

    public static List getList() {
        List list = new ArrayList();
        Map<String, String> map = null;
        for (StaffTypeEnum ele : values()) {
            map = new HashMap<>();
            map.put(ele.code, ele.message);
            list.add(map);
        }
        return list;
    }
}
