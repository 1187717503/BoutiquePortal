package com.intramirror.web.controller.order;

import com.intramirror.common.help.ResultMessage;
import com.intramirror.order.api.util.HttpClientUtil;
import com.intramirror.user.api.model.User;
import com.intramirror.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin
@Controller
@RequestMapping("/table")
public class TableuaController extends BaseController{

    public Object getTableView1Url(HttpServletRequest httpRequest){
        ResultMessage resultMessage = ResultMessage.getInstance();
        User user = this.getUser(httpRequest);
        //Long vendorId = reportExtService.queryVendorIdByUserId(user.getUserId());
        return null;
    }
}
