package com.intramirror.web.interceptor;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.Base64Codec;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.intramirror.common.Helper;

@Repository
public class LoginInterceptor implements HandlerInterceptor{
    private String jwtSecret = "qazxswedcvfr543216yhnmju70plmjkiu89";
	private static Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

	@Override
	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2, ModelAndView arg3) throws Exception {
		
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object arg2) throws Exception {
    	logger.info("start LoginInterceptor");
		boolean status = true;
		
        //获取url地址  
        String reqUrl=request.getRequestURI().replace(request.getContextPath(), "");  
        
        //当url地址为登录的url的时候跳过拦截器  
        if(reqUrl.contains("/login")){  
            return true; 
         
        }else{  
        	
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
//        	response.sendRedirect("/login");
        	response.sendError(2001, "User not logged in !");
        	return false;    
        }
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

}
