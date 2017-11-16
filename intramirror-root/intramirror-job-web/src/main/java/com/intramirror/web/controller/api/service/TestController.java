package com.intramirror.web.controller.api.service;

import com.google.gson.Gson;
import com.intramirror.web.mapping.vo.StockOption;
import java.util.Date;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by dingyifan on 2017/11/9.
 */
@Controller
@RequestMapping("/api/test")
public class TestController {

    private static final Logger logger = Logger.getLogger(TestController.class);

    @RequestMapping("/stock")
    @ResponseBody
    public Map<String,Object> testStock(@RequestBody StockOption stockOption){
        System.out.println(new Gson().toJson(stockOption));
        ApiUpdateStockSerivce apiStockSerivce = new ApiUpdateStockSerivce();
//        stockOption.setLast_check(new Date());
        long  start = System.currentTimeMillis();
        System.out.println("start:"+new Date().toString() +","+start);
        Map<String,Object> map = apiStockSerivce.updateStock(stockOption);
        long  end = System.currentTimeMillis();
        System.out.println("end:"+new Date().toString()+","+end);
        System.out.println(start-end+",map:"+new Gson().toJson(map));
        return map;
    }
}
