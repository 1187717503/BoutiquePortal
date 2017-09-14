package com.intramirror.web.controller.api.cloudstore;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/cloudStore")
public class CloudStoreAllUpdateBySkuController {

    private final Logger logger = Logger.getLogger(CloudStoreAllUpdateBySkuController.class);

    @RequestMapping("/syn_sku_producer")
    @ResponseBody
    public Map<String,Object> execute(){
        return null;
    }

}
