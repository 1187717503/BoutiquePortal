package com.intramirror.web.controller.CorsFilter;

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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

public class CorsFilter implements Filter {

    private static Logger LOGGER = LoggerFactory.getLogger(CorsFilter.class);

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        LOGGER.info("RequestURI: {} , RequestMethod: {}", request.getRequestURI(), request.getMethod());

        if (request.getHeader(HttpHeaders.ORIGIN) != null) {

//            response.setHeader("Access-Control-Allow-Origin","*");
//            response.setHeader("Access-Control-Allow-Credentials", "true");
//            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT, HEAD");
//            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers",
                    "Content-Type,Authorization");
//            response.setHeader("Access-Control-Allow-Credentials","true");
        }
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        chain.doFilter(request, response);

    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // TODO Auto-generated method stub

    }

}
