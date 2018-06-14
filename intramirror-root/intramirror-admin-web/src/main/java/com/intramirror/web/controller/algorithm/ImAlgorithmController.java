package com.intramirror.web.controller.algorithm;

import com.intramirror.common.help.ResultMessage;
import com.intramirror.product.api.model.ImPriceAlgorithm;
import com.intramirror.product.api.service.price.IPriceChangeRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by 123 on 2018/5/31.
 */
@Controller
@RequestMapping("/algorithm")
public class ImAlgorithmController {

    private static Logger logger = LoggerFactory.getLogger(ImAlgorithmController.class);

    @Resource(name = "productPriceChangeRule")
    private IPriceChangeRule iPriceChangeRule;


    @RequestMapping(value = "/getAlgorithmRuleList", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage getAlgorithmRuleList(@RequestBody Map<String, Object> map) {
        ResultMessage resultMessage = ResultMessage.getInstance();
        if (selectAlgorithmsByConditionsParamCheck(map)) {
            return resultMessage.errorStatus().putMsg("info", "params is null !!!");
        }

        try {
            List<ImPriceAlgorithm> imPriceAlgorithms = iPriceChangeRule.selectAlgorithmsByConditions(map);
            resultMessage.successStatus().putMsg("info", "SUCCESS").setData(imPriceAlgorithms);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" error message : {}", e.getMessage());
            resultMessage.errorStatus().putMsg("info", "error message : " + e.getMessage());
        }
        return resultMessage;
    }

    private boolean selectAlgorithmsByConditionsParamCheck(Map<String, Object> params) {
        return params == null || params.get("categoryType") == null || params.get("seasonCode") == null;
    }
}
