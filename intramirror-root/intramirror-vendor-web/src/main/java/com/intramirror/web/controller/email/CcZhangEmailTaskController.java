package com.intramirror.web.controller.email;

import com.intramirror.core.common.response.Response;
import com.intramirror.order.api.service.ICcZhangOrderEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created on 2019/1/20.
 *
 * @author yfding
 */
@Controller
@RequestMapping("/cczhang/task")
public class CcZhangEmailTaskController {

    @Autowired
    private ICcZhangOrderEmailService iCcZhangOrderEmailService;

    @Scheduled(cron = "0 0 08 * * ?")
    @RequestMapping(value = "/confirmed", method = RequestMethod.GET)
    @ResponseBody
    public Response confirmedNotify() {
        iCcZhangOrderEmailService.confirmedOrderToEmail();
        return Response.success();
    }

    @Scheduled(cron = "0 0 08 * * ?")
    @RequestMapping(value = "/shipped", method = RequestMethod.GET)
    @ResponseBody
    public Response shippedNotify() {
        iCcZhangOrderEmailService.shippedOrderToEmail();
        return Response.success();
    }

}
