package com.intramirror.web.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.Base64Codec;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

@Deprecated
public class LoginFilter implements Filter {

    private String jwtSecret = "qazxswedcvfr543216yhnmju70plmjkiu89";
    private static Logger LOGGER = LoggerFactory.getLogger(LoginFilter.class);

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        // logger.info("start doFilter");
        boolean status = true;

        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        LOGGER.info("RequestURI: {} , RequestMethod: {}", request.getRequestURI(), request.getMethod());
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Headers",
                    "token,Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With,userId");
            response.setHeader("Access-Control-Allow-Methods", "PUT,POST,GET,DELETE,OPTIONS");
            response.addHeader("Access-Control-Max-Age", "3600");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        //        if (request.getRequestURI().startsWith("/login")) {
        //            response.reset();
        //            chain.doFilter(request, response);
        //            return;
        //        }
        //        Long userId = null;
        //
        //        String jwt = request.getHeader("token");
        //        if (StringUtils.isEmpty(jwt)) {
        //            response.getWriter().write("token Check failed,Please log in again");
        //            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        //            return;
        //        }
        //
        //        Claims claims = null;
        //        try {
        //            claims = this.parseBody(jwt);
        //        } catch (JwtException e) {
        //            response.getWriter().write("token Check failed,Please log in again");
        //            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        //            return;
        //        }
        //
        //        if (System.currentTimeMillis() > claims.getIssuedAt().getTime()) {
        //            response.getWriter().write("token Check failed,Please log in again");
        //            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        //            return;
        //        }
        //
        //        userId = Long.valueOf(claims.getSubject());
        //
        //        if (userId == null) {
        //            response.getWriter().write("token Check failed,Please log in again");
        //            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        //            return;
        //        }
        //
        //        //response.reset();
        chain.doFilter(request, response);

        //        if (!status) {
        //            logger.info("LoginInterceptor: User not logged in");
        //            response.getWriter().write("token Check failed,Please log in again");
        //            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        //        } else {
        //            response.reset();
        //            chain.doFilter(request, response);
        //        }

    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // TODO Auto-generated method stub

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

}
