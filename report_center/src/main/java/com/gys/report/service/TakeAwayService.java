//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gys.report.service;

import com.gys.common.data.JsonResult;
import com.gys.report.entity.WebOrderQueryBean;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface TakeAwayService {
    JsonResult orderQuery(WebOrderQueryBean bean);

    void orderQueryOutput(WebOrderQueryBean bean, HttpServletRequest request, HttpServletResponse response);

    JsonResult orderDetailQuery(WebOrderQueryBean bean);

    void orderDetailQueryOutput(WebOrderQueryBean bean, HttpServletRequest request, HttpServletResponse response);
}
