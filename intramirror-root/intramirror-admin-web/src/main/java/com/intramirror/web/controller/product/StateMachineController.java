package com.intramirror.web.controller.product;

import com.intramirror.product.api.model.ProductStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2017/10/25.
 *
 * @author YouFeng.Zhu
 */
@RestController("/product/operate")
public class StateMachineController {
    private final static Logger LOGGER = LoggerFactory.getLogger(StateMachineController.class);

    @RequestMapping(value = "/{action}", method = RequestMethod.PUT)
    public Object operateProduct(@PathVariable(value = "action") String action, @RequestParam(value = "currentStatus") String currentStatus) {
        ProductStatusEnum currentStatusEnum = ProductStateMap.getStatus(currentStatus);
        ProductOperationEnum operation = ProductStateMap.getOperation(action);
        return null;
    }
}
