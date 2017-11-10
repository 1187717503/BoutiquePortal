package com.intramirror.web.controller.api.service;

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
        stockOption.setProductCode("587e34dafd7955c0bff4515d");
        stockOption.setSku_code("Ding_s'ku_code");
        stockOption.setQuantity("1000");
        stockOption.setType(Contants.STOCK_QTY+"");
        stockOption.setLast_check(new Date());
        long  start = System.currentTimeMillis();
        System.out.println("start:"+new Date().toString() +","+start);
        Map<String,Object> map = apiStockSerivce.executeUpdateStock();
//        ApiUpdateStockService apiUpdateStockService = new ApiUpdateStockService();

//        apiUpdateStockService.updateStock(stockOption);
        long  end = System.currentTimeMillis();
        System.out.println("end:"+new Date().toString()+","+end);
        System.out.println(start-end);
//        System.out.println("result:"+ JSONObject.toJSONString(map));


    }
}
