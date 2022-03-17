package com.gys.common.enums;

/**
 * @author zhangdong
 * @date 2021/7/21 17:30
 */
public enum O2OorderStatusEnum {

    //订单状态（0:新订单[正常订单]1:已确认订单2:已配送订单3:已完成订单4:用户申请退单5:门店申请退单6:用户催单）
    NEW_ORDER(0,"新订单"),
    CONFIRM_ORDER(1,"已确认"),
    SEND_ORDER(2,"已配送"),
    FINISH_ORDER(3,"已完成"),
    USER_RETURN_ORDER(4,"用户申请退单"),
    STO_RETURN_ORDER(5,"门店申请退单"),
    REMIDER_ORDER(6,"用户催单");

    private Integer index;
    private String status;

    O2OorderStatusEnum(Integer index, String status) {
        this.index = index;
        this.status = status;
    }

    public static String getStatus(Integer index) {
        O2OorderStatusEnum[] values = O2OorderStatusEnum.values();
        for (O2OorderStatusEnum value : values) {
            if (value.index.equals(index)) {
                return value.status;
            }
        }
        return null;
    }

}
