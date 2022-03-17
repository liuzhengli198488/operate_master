package com.gys.common.response;

import com.gys.util.UtilConst;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接口返回实体
 */
@Data
@NoArgsConstructor
public class Result {
    //返回码
    private String code;
    //提示信息
    private String msg;
    //返回具体内容
    private Object data;
    // 消息
    private String message;

    public Result(String code, String msg, Object data, String message) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.message = message;
    }

    public static Result error(String msg) {
        return new Result(UtilConst.CODE_STR_1001, msg,null,null );
    }


    public static Result errorMessage(String msg) {
        return new Result(UtilConst.CODE_STR_1001, msg,null,msg );
    }

}
