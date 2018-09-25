package com.intramirror.web.controller.apimq;

import com.intramirror.common.Helper;
import com.intramirror.common.parameter.EnabledType;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.order.api.vo.MailModelVO;
import com.intramirror.order.core.utils.MailSendUtil;
import com.intramirror.user.api.model.PasswordMail;
import com.intramirror.user.api.model.User;
import com.intramirror.user.api.model.Vendor;
import com.intramirror.user.api.model.VendorApplication;
import com.intramirror.user.api.service.PasswordMailService;
import com.intramirror.user.api.service.UserService;
import com.intramirror.user.api.service.VendorService;
import com.intramirror.web.controller.BaseController;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pk.shoplus.common.utils.StringUtil;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static pk.shoplus.common.utils.StringUtil.byteArrayToHexString;

@CrossOrigin
@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

//    private String jwtSecret = "qazxswedcvfr543216yhnmju70plmjkiu89";

    //@Resource(name = "userServiceImpl")
//    @Autowired
//    private UserService userService;

    @Autowired
    private VendorService vendorService;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordMailService passwordMailService;

    @Value("${changePwdUrl}")
    private String changePwdUrl;

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
    public Map forgetPwd(@RequestParam(required = true) String email) throws Exception {
        User user = userService.getUserByEmail(email, EnabledType.USED);
        Map<String, Object> stringObjectMap = new HashMap<String, Object>();
        if (Helper.checkNotNull(user)) {
            /*String pwd = StringUtil.getStringRandom(8);
            userService.changePwd(user.getUserId(),StringUtil.md5Hex(pwd));*/
            Date now = new Date();
            PasswordMail passwordMail = new PasswordMail();
            passwordMail.setEmail(email);
            passwordMail.setCreate_date(DateUtils.getDateByStr(DateUtils.getStrDate(now,"yyyy-MM-dd HH:mm:ss")));
            passwordMail.setIs_change(0);
            StringBuffer url = new StringBuffer(changePwdUrl);
            url.append("?createDate=").append(DateUtils.getStrDate(now,"yyyy-MM-dd HH:mm:ss")).append("&email=")
                    .append(email);
            passwordMail.setUrl(url.toString());
            passwordMailService.insert(passwordMail);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendEmail(email," Forget password for Boutique Portal system",
                            "Dear "+user.getUsername()+",<br>Please click the link below，and input your new password immediately , thank you!\n" +
                                    "<a href='"+url.toString()+"'>"+changePwdUrl+"</a>");
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

    @RequestMapping(value = "/change4forget", method = RequestMethod.GET)
    @ResponseBody
    public Map change4forget(@RequestParam(required = true) String email, @RequestParam(required = true)String createDate,@RequestParam String newPwd,
                             @RequestParam(required = true)String confirmPwd) throws Exception {
        Map<String, Object> result = new HashMap<>();
        try{
            User user = userService.getUserByEmail(email, EnabledType.USED);
            Date create = DateUtils.getDateByStr(createDate);
            if(StringUtil.isEmpty(email)||createDate==null||create==null||user==null){
                result.put("status",StatusType.FAILURE);
                result.put("message","link error");
                return result;
            }
            PasswordMail passwordMail = passwordMailService.getNewPasswordMail(email);
            if(passwordMail==null){
                result.put("status",StatusType.FAILURE);
                result.put("message","link error");
                return result;
            }
            if(!DateUtils.getformatDate(create).equals(DateUtils.getformatDate(passwordMail.getCreate_date()))||passwordMail.getIs_change()==1||DateUtils.getDatePool4Hour(create,new Date())>2){
                result.put("status",StatusType.MAIL_URL_EXPRIRED);
                result.put("message","This link has been expired,please back to login to send link for changing password");
                return result;
            }
            userService.changePwd(user.getUserId(),newPwd);
            passwordMailService.updateIschange(passwordMail.getPassword_mail_id());
            result.put("status",StatusType.SUCCESS);
        }catch (Exception e){
            logger.error("发邮件改密码异常"+e.getMessage(),e);
            result.put("status",StatusType.FAILURE);
            result.put("message","System error");
        }
        return result;

    }

    @RequestMapping(value = "/checkforget", method = RequestMethod.GET)
    @ResponseBody
    public Map change4forget(@RequestParam(required = true) String email, @RequestParam(required = true)String createDate) throws Exception {
        Map<String, Object> result = new HashMap<>();
        try{
            User user = userService.getUserByEmail(email, EnabledType.USED);
            Date create = DateUtils.getDateByStr(createDate);
            if(StringUtil.isEmpty(email)||createDate==null||create==null||user==null){
                result.put("status",StatusType.FAILURE);
                result.put("message","link error");
                return result;
            }
            PasswordMail passwordMail = passwordMailService.getNewPasswordMail(email);
            if(passwordMail==null){
                result.put("status",StatusType.FAILURE);
                result.put("message","link error");
                return result;
            }
            System.out.println(!DateUtils.getformatDate(create).equals(DateUtils.getformatDate(passwordMail.getCreate_date())));
            System.out.println(DateUtils.getDatePool4Hour(create,new Date())>2);
            if(!DateUtils.getformatDate(create).equals(DateUtils.getformatDate(passwordMail.getCreate_date()))||passwordMail.getIs_change()==1||DateUtils.getDatePool4Hour(create,new Date())>2){
                result.put("status",StatusType.MAIL_URL_EXPRIRED);
                result.put("message","This link has been expired,please back to login to send link for changing password");
                return result;
            }
            result.put("status",StatusType.SUCCESS);
        }catch (Exception e){
            logger.error("check发邮件改密码异常"+e.getMessage(),e);
            result.put("status",StatusType.FAILURE);
            result.put("message","System error");
        }
        return result;

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