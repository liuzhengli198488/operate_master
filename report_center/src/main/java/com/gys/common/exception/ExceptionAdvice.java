package com.gys.common.exception;

import com.gys.common.data.JsonResult;
import com.gys.util.Util;
import com.gys.util.UtilConst;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;

/**
 * @author
 */
@ControllerAdvice
@ResponseBody
public class ExceptionAdvice {
    private static final Logger log = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({RuntimeException.class})
    public JsonResult handleException(HttpServletRequest request, Exception e) {
        log.info(Util.getExceptionInfo(e));
        return JsonResult.error();
    }

    @ExceptionHandler({com.gys.common.exception.BusinessException.class})
    public JsonResult handleException(com.gys.common.exception.BusinessException e) {
        return JsonResult.fail(UtilConst.CODE_1001, e.getMessage());
    }

    /**
     * 参数效验统一拦截
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public JsonResult notValidExceptionHandle(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        Objects.requireNonNull(bindingResult.getFieldError());
        return JsonResult.error(UtilConst.CODE_1001,bindingResult.getFieldError().getDefaultMessage());
    }
}
