package com.intramirror.web.controller.async;

import com.intramirror.core.common.response.Response;
import com.intramirror.web.distributed.consumer.ProductConsumerService;
import com.intramirror.web.distributed.consumer.StockConsumerService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2018/3/5.
 *
 * @author YouFeng.Zhu
 */
@RestController
@RequestMapping("/async")
public class AsyncController {
    @Autowired
    private ProductConsumerService productConsumerService;

    @Autowired
    private StockConsumerService stockConsumerService;

    @PutMapping("/product/start")
    public Response startProductConsumer(@RequestBody Map<String, Object> body) {
        productConsumerService.startConsumeProduct(Integer.valueOf(body.get("concurrency").toString()));
        return Response.success();
    }

    @PutMapping("/product/stop")
    public Response stopProductConsumer() {
        productConsumerService.stopConsumeProduct();
        return Response.success();
    }

    @PutMapping("/stock/start")
    public Response startStockConsumer(@RequestBody Map<String, Object> body) {
        stockConsumerService.startConsumeStock(Integer.valueOf(body.get("concurrency").toString()));
        return Response.success();
    }

    @PutMapping("/stock/stop")
    public Response stopStockConsumer() {
        stockConsumerService.stopConsumeStock();
        return Response.success();
    }

}
