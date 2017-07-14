package com.intramirror.web.controller.apimq;

import com.google.gson.Gson;
import com.intramirror.common.Helper;
import com.intramirror.common.parameter.EnabledType;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.user.api.model.User;
import com.intramirror.user.api.model.Vendor;
import com.intramirror.user.api.model.VendorApplication;
import com.intramirror.user.api.service.UserService;
import com.intramirror.user.api.service.VendorService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.Base64Codec;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/login")
public class LoginController {

    private String jwtSecret = "qazxswedcvfr543216yhnmju70plmjkiu89";

    private int jwtExpireIn = 172800;

    //@Resource(name = "userServiceImpl")
    @Autowired
    private UserService userService;

    @Autowired
    private VendorService vendorService;

    /**
     * Wang
     * @param email
     * @param userPwd
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/do_login", method = RequestMethod.POST)
    @ResponseBody
    public String do_login(String email, String userPwd) throws Exception {
        // 返回数据初始化
        int status = StatusType.FAILURE;
        Map<String, Object> stringObjectMap = new HashMap<String, Object>();

        // check param
        if (Helper.isNullOrEmpty(email)) {
            stringObjectMap.put("status", StatusType.PARAM_EMPTY_OR_NULL);
            return new Gson().toJson(stringObjectMap);
        }
        if (!Helper.checkEmail(email)) {
            stringObjectMap.put("status", StatusType.EMAIL_ADDRESS_ERROR);
            return new Gson().toJson(stringObjectMap);
        }
        if (userPwd.length() > 64) {
            stringObjectMap.put("status", StatusType.PASSWORD_LENGTH_ERROR);
            return new Gson().toJson(stringObjectMap);
        }

        // 根据user_id获取用户数据，如没有，返回状态
        User user = userService.getUserByEmail(email, EnabledType.USED);
        if (Helper.checkNotNull(user)) {
            VendorApplication vendorApplication = vendorService.getVendorApplicationByUserId(user.getUserId());
            if (Helper.checkNotNull(vendorApplication)) {
                if (user.getPassword().equals(Helper.md5Jdk(userPwd))) {
                    Vendor vendor = vendorService.getVendorByUserId(user.getUserId());
                    if (Helper.checkNotNull(vendor)) {
                        //计算过期时间
                        DateTime dateTime = DateTime.now().plusSeconds(jwtExpireIn);
                        String token = Jwts.builder().setSubject(String.valueOf(user.getUserId())).setIssuedAt(dateTime.toDate()).signWith(SignatureAlgorithm.HS512, getJwtBase64Key()).compact();
                        stringObjectMap.put("user", user);
                        stringObjectMap.put("token", token);
                        status = StatusType.SUCCESS;
                    }
                } else {
                    // 用户名密码不正确
                    status = StatusType.INCORRECT_PASSWORD;
                }
            } else {
                status = StatusType.NO_VENDOR_APPLICATION;
            }
        } else {
            // 用户名不存在或者已废弃
            status = StatusType.USERNAMENOTEXIST;
        }
        stringObjectMap.put("status", status);
        return new Gson().toJson(stringObjectMap);
    }

    public String getJwtBase64Key() {
        return Base64Codec.BASE64.encode(jwtSecret);
    }
}
