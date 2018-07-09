package com.intramirror.web.controller;

import com.intramirror.common.parameter.EnabledType;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.core.common.exception.StandardExceptions;
import com.intramirror.user.api.model.User;
import com.intramirror.user.api.service.UserService;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/base")
public class BaseController {

    private final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    private UserService userService;

    /**
     * Wang
     *
     * @return
     * @throws Exception
     */

    public Map baseController(HttpServletRequest httpRequest) throws Exception {
        // 返回数据初始化
        int status = StatusType.FAILURE;
        Map<String, Object> result = new HashMap<>();

        HttpSession httpSession = httpRequest.getSession(false);
        Map<String, Object> sessionUser = (Map<String, Object>) httpSession.getAttribute("user");
        if (sessionUser == null) {
            LOGGER.warn("Fail to get session due to no session or session expired.");
            throw StandardExceptions.UNAUTHORIZED;
        }

        if (sessionUser.get("userId") == null) {
            LOGGER.warn("Fail to get session due to no session or session expired.");
            throw StandardExceptions.UNAUTHORIZED;
        }

        Long userId = Long.valueOf(sessionUser.get("userId").toString());

        User user = userService.getUserById(userId, EnabledType.USED);
        if (user != null) {
            result.put("user", user);
            status = StatusType.SUCCESS;
        }
        result.put("status", status);
        return result;
    }

    public User getUserInfo(HttpServletRequest httpRequest) throws Exception {

        HttpSession httpSession = httpRequest.getSession(false);
        Map<String, Object> sessionUser = (Map<String, Object>) httpSession.getAttribute("user");
        if (sessionUser == null) {
            LOGGER.warn("Fail to get session due to no session or session expired.");
            throw StandardExceptions.UNAUTHORIZED;
        }

        if (sessionUser.get("userId") == null) {
            LOGGER.warn("Fail to get session due to no session or session expired.");
            throw StandardExceptions.UNAUTHORIZED;
        }

        Long userId = Long.valueOf(sessionUser.get("userId").toString());

        User user = userService.getUserById(userId, EnabledType.USED);

        return user;
    }

}
