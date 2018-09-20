package com.intramirror.web.controller.apimq;

import com.intramirror.common.Helper;
import com.intramirror.common.parameter.EnabledType;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.order.api.vo.MailModelVO;
import com.intramirror.order.core.utils.MailSendUtil;
import com.intramirror.user.api.model.User;
import com.intramirror.user.api.model.Vendor;
import com.intramirror.user.api.model.VendorApplication;
import com.intramirror.user.api.service.UserService;
import com.intramirror.user.api.service.VendorService;
import com.intramirror.web.controller.BaseController;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pk.shoplus.common.utils.StringUtil;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static pk.shoplus.common.utils.StringUtil.byteArrayToHexString;

@CrossOrigin
@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

//    private String jwtSecret = "qazxswedcvfr543216yhnmju70plmjkiu89";

    //@Resource(name = "userServiceImpl")
//    @Autowired
//    private UserService userService;

    @Autowired
    private VendorService vendorService;
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
    public Map getUserInfo(HttpServletRequest httpRequest) {
        Map<String, Object> result = new HashMap<>();
        User user = super.getUser(httpRequest);
        if (user == null) {
            result.put("status", StatusType.FAILURE);
            return result;
        }
        try {
            List<Vendor> vendors = vendorService.getVendorsByUserId(user.getUserId());
            if(CollectionUtils.isNotEmpty(vendors)){
                if(CollectionUtils.isNotEmpty(vendors.stream().filter(e -> !e.getUserId().equals(user.getUserId())).collect(Collectors.toList()))){
                    user.setIsParent(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.put("status", StatusType.SUCCESS);
        result.put("user", user);
        return result;
    }

    @RequestMapping(value = "/changePwd", method = RequestMethod.GET)
    @ResponseBody
    public Map changePwd(@RequestParam(required = true) String newPwd, @RequestParam(required = true)String confirmPwd,HttpServletRequest httpRequest) throws Exception {
        User user = this.getUser(httpRequest);
        Map<String, Object> stringObjectMap = new HashMap<String, Object>();
        if (user == null) {
            stringObjectMap.put("status", StatusType.SESSION_USER_NULL);
            stringObjectMap.put("message","Please log in again");
            return stringObjectMap;
        }
        if(!newPwd.equals(confirmPwd)){
            stringObjectMap.put("status", StatusType.FAILURE);
            stringObjectMap.put("message","The two password is different");
            return stringObjectMap;
        }
        stringObjectMap.put("status", StatusType.SUCCESS);
        userService.changePwd(user.getUserId(),newPwd);
        return stringObjectMap;
    }

    @RequestMapping(value = "/forgetPwd", method = RequestMethod.GET)
    @ResponseBody
    public Map changePwd(@RequestParam(required = true) String email) throws Exception {
        User user = userService.getUserByEmail(email, EnabledType.USED);
        Map<String, Object> stringObjectMap = new HashMap<String, Object>();
        if (Helper.checkNotNull(user)) {
            String pwd = StringUtil.getStringRandom(8);
            userService.changePwd(user.getUserId(),StringUtil.md5Hex(pwd));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendEmail(email,"Temporary password for Boutique Portal system",
                            "Dear  "+user.getUsername()+",<br>Your temporary pasword is  "+pwd+" ，" +
                                    " please change your password immediately in the boutique profile once you login, thank you!");
                }
            }).start();
            stringObjectMap.put("status",StatusType.SUCCESS);
        } else {
            // 用户名不存在或者已废弃
            stringObjectMap.put("status",StatusType.USERNAMENOTEXIST);
            stringObjectMap.put("message","email does not exist");
        }
        return stringObjectMap;
    }
    private void sendEmail(String toEmail,String subject,String content){
        MailModelVO mailContent = new MailModelVO();
        mailContent.setToEmails(toEmail);
        mailContent.setSubject(subject);
        mailContent.setContent(content);
        try {
            MailSendUtil.sendMail(mailContent);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}