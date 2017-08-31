package com.intramirror.web.controller.onoff;

import com.intramirror.common.help.ResultMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by dingyifan on 2017/8/31.
 */
@Controller
@RequestMapping("/params")
public class ParamsController {

    @RequestMapping("/change")
    @ResponseBody
    public ResultMessage changeParams(){
        ResultMessage resultMessage = new ResultMessage();

        return resultMessage;
    }
}
