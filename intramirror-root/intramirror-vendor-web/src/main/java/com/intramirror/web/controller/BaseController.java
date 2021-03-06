package com.intramirror.web.controller;

import com.intramirror.common.Helper;
import com.intramirror.common.parameter.EnabledType;
import com.intramirror.user.api.model.User;
import com.intramirror.user.api.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.Base64Codec;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
@RequestMapping("/base")
public class BaseController {

    private String jwtSecret = "qazxswedcvfr543216yhnmju70plmjkiu89";


    @Autowired
    private UserService userService;

    /**
     * wzh
     *
     * @return
     * @throws Exception
     */
    public User getUser(HttpServletRequest httpRequest) {
    	User user = null;
        Long userId = null;

        try {
            String jwt = httpRequest.getHeader("token");
            if (StringUtils.isEmpty(jwt)) {
            	return user;
            }
            //解析Jwt内容
            Claims claims = this.parseBody(jwt);

            Date expireation = claims.getIssuedAt();
            //如果信息过期则提示重新登录。
            if (System.currentTimeMillis() > expireation.getTime()) {
                return user;
            }
            //获取用户详情
            userId = Long.valueOf(claims.getSubject());

            //如果匿名访问则跳过
            if (!Helper.checkNotNull(userId)) {
                return user;
            }

            user = userService.getUserById(userId, EnabledType.USED);
        } catch (Exception e) {
            return user;
        }

        return user;
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
