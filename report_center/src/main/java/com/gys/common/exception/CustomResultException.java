package com.gys.common.exception;

import com.gys.common.response.ResultEnum;
import lombok.Data;

import java.text.MessageFormat;

@Data
public class CustomResultException extends RuntimeException {


    /**
     * 我们希望定位的错误更准确，
     * 希望不同的错误可以返回不同的错误码，所以可以自定义一个Exception
     * <p>
     * <p>
     * 注意要继承自RuntimeException，底层RuntimeException继承了Exception,
     * spring框架只对抛出的异常是RuntimeException才会进行事务回滚，
     * 如果是抛出的是Exception，是不会进行事物回滚的
     */
    public CustomResultException(ResultEnum resultEnum) {
        super(resultEnum.getMsg());
        this.code = resultEnum.getCode();
    }

    /**
     *
     * @param resultEnum
     * @param params
     */
    public CustomResultException(ResultEnum resultEnum, String... params) {
        super(MessageFormat.format(resultEnum.getMsg(), params));
        this.code = resultEnum.getCode();
    }

    /**
     * 验证时发生错误需要回滚
     * @param message
     */
    public CustomResultException(String message) {
        super(message);
        this.code = ResultEnum.VALID_ERROR.getCode();
    }

    private String code;

}