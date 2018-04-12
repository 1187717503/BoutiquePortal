package com.intramirror.web.interceptor;

import com.intramirror.core.common.exception.StandardExceptions;
import com.intramirror.core.servlet.interceptor.AuthenticationInterceptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Created on 2018/1/31.
 *
 * @author YouFeng.Zhu
 */
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {

    private final static Logger LOGGER = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    private static List<String> passUrlList;

    static {
        passUrlList = new ArrayList<>();
        passUrlList.add("/login");
        passUrlList.add("/rule");
        passUrlList.add("/price");
        passUrlList.add("/priceChangeRule");
        //        passUrlList.add("/file");
    }

    /**
     * This implementation always returns {@code true}.
     *
     * @param request
     * @param response
     * @param handler
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LOGGER.info("RequestURI {}", request.getRequestURI());

        for (String passUrl : passUrlList) {
            if (request.getRequestURI().startsWith(passUrl)) {
                return true;
            }
        }

        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            LOGGER.warn("Fail to get session due to no session or session expired.");
            throw StandardExceptions.UNAUTHORIZED;
        }

        Map<String, Object> sessionUser = (Map<String, Object>) session.getAttribute("user");
        if (sessionUser == null) {
            LOGGER.warn("Fail to get user info from session.");
            throw StandardExceptions.UNAUTHORIZED;
        }

        request.getSession().setAttribute("sessionStorage", sessionUser.get("userId"));
        return true;
    }

}
