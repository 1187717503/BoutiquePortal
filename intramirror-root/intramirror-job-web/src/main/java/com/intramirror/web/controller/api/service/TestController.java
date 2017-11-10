package com.intramirror.web.controller.api.service;

import com.alibaba.fastjson.JSONObject;
import com.intramirror.web.mapping.vo.StockOption;
import java.util.Date;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pk.shoplus.common.Contants;

/**
 * Created by dingyifan on 2017/11/9.
 */
@Controller
@RequestMapping("/api/test")
public class TestController {

    private static final Logger logger = Logger.getLogger(TestController.class);

    @RequestMapping("/stock")
    @ResponseBody
    public void testStock(){
        StockOption stockOption = new StockOption();
        ApiStockSerivce apiStockSerivce = new ApiStockSerivce(stockOption);
        stockOption.setSizeValue("AA");
        stockOption.setVendor_id("9");
        stockOption.setProductCode("587e3'4dafd7955c'0bff4515d");
        stockOption.setSku_code("Ding_s'ku_code");
        stockOption.setQuantity("1");
        stockOption.setType(Contants.STOCK_QTY+"");
        System.out.println("start:"+new Date().toString());
        Map<String,Object> map = apiStockSerivce.executeUpdateStock();
        System.out.println("end:"+new Date().toString());
        System.out.println("result:"+ JSONObject.toJSONString(map));


    }
}
