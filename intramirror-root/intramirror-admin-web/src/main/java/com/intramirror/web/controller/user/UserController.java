package com.intramirror.web.controller.user;

import com.intramirror.common.Helper;
import com.intramirror.common.parameter.EnabledType;
import com.intramirror.common.parameter.StatusType;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    private String jwtSecret = "qazxswedcvfr543216yhnmju70plmjkiu89";

    //@Resource(name = "userServiceImpl")
    @Autowired
    private UserService userService;

    /**
     * Wang
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getUser", method = RequestMethod.POST)
    @ResponseBody
    public Map getUser(HttpServletRequest httpRequest) throws Exception {
        // 返回数据初始化
        int status = StatusType.FAILURE;
        Map<String, Object> result = new HashMap<String, Object>();
        Long userId = null;

        try {
            String jwt = httpRequest.getHeader("token");
            if (StringUtils.isEmpty(jwt)) {
                throw new JwtException("header not found,token is null");
            }
            //解析Jwt内容
            Claims claims = this.parseBody(jwt);

            Date expireation = claims.getIssuedAt();
            //如果信息过期则提示重新登录。
            if (System.currentTimeMillis() > expireation.getTime()) {
                result.put("userStatus", "1000002");
                return result;
            }
            //获取用户详情
            userId = Long.valueOf(claims.getSubject());

            //如果匿名访问则跳过
            if (!Helper.checkNotNull(userId)) {
                result.put("userStatus", "1000003");
                return result;
            }
        } catch (Exception e) {
            result.put("status", status);
            return result;
        }
        User user = userService.getUserById(userId, EnabledType.USED);
        if (user != null) {
            result.put("user", user);
            status = StatusType.SUCCESS;
        }
        result.put("status", status);
        return result;
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
