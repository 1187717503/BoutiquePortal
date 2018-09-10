package com.intramirror.web.controller.apimq;

import com.intramirror.common.parameter.StatusType;
import com.intramirror.user.api.model.User;
import com.intramirror.user.api.model.Vendor;
import com.intramirror.user.api.service.VendorService;
import com.intramirror.web.controller.BaseController;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

}