package com.wiqer.coordina.tm.intercept;


import com.wiqer.coordina.tm.transactional.GlobalTransactionManager;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @desc XID拦截器
 */
public class RequestInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String xid = request.getHeader("xid");
        if(!StringUtils.isEmpty(xid))
            GlobalTransactionManager.setCurrentXid(xid);
        return true;
    }
}
