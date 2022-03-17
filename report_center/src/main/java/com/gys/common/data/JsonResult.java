package com.gys.common.data;

import com.gys.util.UtilConst;
import lombok.Data;

import java.io.Serializable;

@Data
public class JsonResult<T> implements Serializable {
    private static final String ERROR_MESSAGE = "服务器出现了一些小状况，正在修复中！";
    private static final long serialVersionUID = -3781934088499015325L;
    private Integer code;
    private Object data;
    private String message;

    public JsonResult(Integer code, Object data, String message) {
        this.data = data;
        this.message = message;
        this.code = code;
    }


    public static JsonResult success(Object data, String desc) {
        return new JsonResult(UtilConst.CODE_0, data, desc);
    }

    public static JsonResult success() {
        return success(null,"成功");
    }

    public static JsonResult success(Integer code ,Object data, String desc) {
        return new JsonResult(code, data, desc);
    }

    public static JsonResult fail(Integer code, String desc) {
        return new JsonResult(code, null, desc);
    }

    public static JsonResult process(Integer code, Object data, String desc) {
        return new JsonResult(code, data, desc);
    }

    public static JsonResult error() {
        return new JsonResult(UtilConst.CODE_500, null, ERROR_MESSAGE);
    }
    public static JsonResult error(String msg) {
        return new JsonResult(UtilConst.CODE_500, null, msg);
    }

    public static JsonResult error(Integer code, String msg) {
        return new JsonResult(code, null, msg);
    }

}
