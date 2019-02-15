package com.intramirror.web.controller.order;

import com.intramirror.common.help.ResultMessage;
import com.intramirror.order.api.util.HttpClientUtil;
import com.intramirror.order.api.vo.VendorVO;
import com.intramirror.order.core.impl.ext.ReportExtServiceImpl;
import com.intramirror.user.api.model.User;
import com.intramirror.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@Controller
@RequestMapping("/bi")
public class TableuaController extends BaseController{

    @Autowired
    private ReportExtServiceImpl reportExtService;

    @ResponseBody
    @GetMapping("/tableua/token")
    public Object getTableuaToken(HttpServletRequest httpRequest){
        ResultMessage resultMessage = ResultMessage.getInstance();
        User user = this.getUser(httpRequest);
        if(user == null){
            resultMessage.setStatus(-1);
            resultMessage.setMsg("user info error!");
            return resultMessage;
        }
        VendorVO vendorVO = reportExtService.queryVendorByUserId(user.getUserId().longValue());
        String ret = HttpClientUtil.httpPostTableuaToken();
        Map<String,Object> retMap = new HashMap<>();
        retMap.put("vendorName",vendorVO == null ? "":vendorVO.getVendorName());
        retMap.put("tableuaToken",ret);
        resultMessage.setStatus(1);
        resultMessage.setData(retMap);
        return resultMessage;
    }


}
