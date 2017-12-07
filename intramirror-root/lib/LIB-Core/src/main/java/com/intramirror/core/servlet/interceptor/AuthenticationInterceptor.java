package com.intramirror.core.servlet.interceptor;

import com.intramirror.core.common.exception.StandardExceptions;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.Base64Codec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Created on 3/28/2017.
 *
 * @author eyoufzh
 */
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {
    private final static Logger LOGGER = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    private final static String jwtSecret = "qazxswedcvfr543216yhnmju70plmjkiu89";
    private static final String LOGIN_URI = "/login";

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
        if(request.getRequestURI().contains("rule")) {
            return true;
        }

        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        if (request.getRequestURI().startsWith(LOGIN_URI)) {
            return true;
        }
        Long userId = null;

        String jwt = request.getHeader("token");
        if (StringUtils.isEmpty(jwt)) {
            throw StandardExceptions.UNAUTHORIZED;
        }

        Claims claims = null;
        try {
            claims = this.parseBody(jwt);
        } catch (JwtException e) {
            throw StandardExceptions.UNAUTHORIZED;
        }

        if (System.currentTimeMillis() > claims.getIssuedAt().getTime()) {
            throw StandardExceptions.UNAUTHORIZED;
        }

        userId = Long.valueOf(claims.getSubject());
        if (userId == null) {
            throw StandardExceptions.UNAUTHORIZED;
        }

        request.getSession().setAttribute("sessionStorage", userId);

        return true;
    }

    public String getJwtBase64Key() {
        return Base64Codec.BASE64.encode(jwtSecret);
    }

    public JwtParser parseToken() {
        return Jwts.parser().setSigningKey(getJwtBase64Key());
    }

    public Claims parseBody(String jwt) throws JwtException {
        return parseToken().setSigningKey(getJwtBase64Key()).parseClaimsJws(jwt).getBody();
    }

    /**
     * This implementation is empty.
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * This implementation is empty.
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }
}
