package com.intramirror.web.controller.user;

import com.intramirror.common.Helper;
import com.intramirror.common.parameter.EnabledType;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.user.api.model.Role;
import com.intramirror.user.api.model.User;
import com.intramirror.user.api.model.UserRole;
import com.intramirror.user.api.service.RoleService;
import com.intramirror.user.api.service.UserRoleService;
import com.intramirror.user.api.service.UserService;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/login")
public class LoginController {

    //@Resource(name = "userServiceImpl")
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleService userRoleService;

    /**
     * @param map
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/do_login", method = RequestMethod.POST)
    @ResponseBody
    public Map do_login(HttpServletRequest request, @RequestBody Map<String, Object> map) throws Exception {
        // 返回数据初始化
        int status = StatusType.FAILURE;
        Map<String, Object> stringObjectMap = new HashMap<String, Object>();

        // check param
        if (Helper.isNullOrEmpty(map.get("email").toString())) {
            stringObjectMap.put("status", StatusType.PARAM_EMPTY_OR_NULL);
            return stringObjectMap;
        }
        if (!Helper.checkEmail(map.get("email").toString())) {
            stringObjectMap.put("status", StatusType.EMAIL_ADDRESS_ERROR);
            return stringObjectMap;
        }
        if (map.get("userPwd").toString().length() > 64) {
            stringObjectMap.put("status", StatusType.PASSWORD_LENGTH_ERROR);
            return stringObjectMap;
        }

        // 根据user_id获取用户数据，如没有，返回状态
        User user = userService.getUserByEmail(map.get("email").toString(), EnabledType.USED);
        if (user == null) {
            status = StatusType.EMAIL_ADDRESS_ERROR;
            stringObjectMap.put("status", status);
            return stringObjectMap;
        }
        // 权限验证
        UserRole userRole = userRoleService.getUserRoleByUserId(user.getUserId(), EnabledType.USED);
        if (userRole == null) {
            stringObjectMap.put("status", StatusType.ADMIN_USER_ROLE_NOT_EXIST);
            return stringObjectMap;
        } else if (user.getPassword().equals(map.get("userPwd").toString())) {
            Role role = roleService.getRoleById(userRole.getRoleId());
            if (role != null) {
                //计算过期时间生成token
                Map<String, Object> sessionUser = new HashMap<>();
                sessionUser.put("userId", user.getUserId());
                sessionUser.put("email", user.getEmail());
                sessionUser.put("roleId", role.getRoleId());
                request.getSession().setAttribute("user", sessionUser);

                stringObjectMap.put("token", request.getSession().getId());
                status = StatusType.SUCCESS;
            } else {
                status = StatusType.ADMIN_ROLE_NOT_EXIST;
            }
        } else {
            // 用户名存在但密码错误
            status = StatusType.INCORRECT_PASSWORD;
        }

        stringObjectMap.put("status", status);
        return stringObjectMap;
    }

}
