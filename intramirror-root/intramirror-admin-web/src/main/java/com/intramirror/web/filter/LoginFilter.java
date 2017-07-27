package com.intramirror.web.filter;

import com.intramirror.web.common.CommonsProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.Base64Codec;

import java.io.IOException;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.intramirror.common.Helper;

@Component
public class LoginFilter implements Filter{
	
    private String jwtSecret = "qazxswedcvfr543216yhnmju70plmjkiu89";
	private static Logger logger = LoggerFactory.getLogger(LoginFilter.class);

	@Resource(name = "commonsProperties")
	private CommonsProperties commonsProperties;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
//    	logger.info("start doFilter");
		boolean status = true;
		
		HttpServletResponse response = (HttpServletResponse) res;
		HttpServletRequest  request = (HttpServletRequest) req;
		
//	    response.setHeader("Access-Control-Allow-Origin", "http://192.168.31.250"); 
		response.setHeader("Access-Control-Allow-Origin", "*");
	    response.setHeader("Access-Control-Allow-Headers", "token,Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With,userId");
        response.setHeader("Access-Control-Allow-Methods","PUT,POST,GET,DELETE,OPTIONS"); 
        
        //设置时间   controller 可以去掉@CrossOrigin注解
        response.addHeader("Access-Control-Max-Age", "3600");
        
        //获取url地址  
        String reqUrl=request.getRequestURI().replace(request.getContextPath(), "");  

        //当url地址为登录的url的时候跳过拦截器  
        if(reqUrl.contains("/login")){    
         
        //会请求两次，第一次是options 类型,但第一次状态返回200 才会第二次请求
        //先请求OPTIONS，得到许可后就可以跨域访问POST请求
        }else if(request.getMethod().equals("OPTIONS") && !reqUrl.contains("/login")){  
             
        }else if(commonsProperties.getFilterEnabled().equals("0")){
            // 跳过
        } else {
        	
            Long userId = null;

            try {
                String jwt = request.getHeader("token");
                if (StringUtils.isEmpty(jwt)) {
                    throw new JwtException("header not found,token is null");
                }
                
                //解析Jwt内容
                Claims claims = this.parseBody(jwt);

                Date expireation = claims.getIssuedAt();
                //如果信息过期则提示重新登录。
                if (System.currentTimeMillis() > expireation.getTime()) {
                	status = false;
                }
                //获取用户详情
                userId = Long.valueOf(claims.getSubject());

                //如果匿名访问则跳过
                if (!Helper.checkNotNull(userId)) {
                	status = false;
                }
            } catch (Exception e) {
            	status =  false;
            }
            
        }
        if(!status){
        	logger.info("LoginInterceptor: User not logged in");
           	response.getWriter().write("token Check failed,Please log in again");
        	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }else{
        	response.reset();
            chain.doFilter(request, response); 
        }
		
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
