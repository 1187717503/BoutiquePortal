package com.intramirror.web.controller.async;

import com.intramirror.core.common.response.Response;
import com.intramirror.web.distributed.consumer.ConsumerService;
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
    private ConsumerService consumerService;

    @PutMapping("/product/start")
    public Response startProductConsumer(@RequestBody Map<String, Object> body) {
        consumerService.startConsumerProduct(Integer.valueOf(body.get("concurrency").toString()));
        return Response.success();
    }

    @PutMapping("/product/stop")
    public Response stopProductConsumer() {
        consumerService.stopConsumerProduct();
        return Response.success();
    }

}
