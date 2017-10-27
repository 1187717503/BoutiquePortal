package com.intramirror.web.controller.brand;

import com.intramirror.common.help.ResultMessage;
import com.intramirror.product.api.service.brand.IBrandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/7/19.
 */

@Controller
@RequestMapping("/brand")
public class BrandController {

    private static Logger logger = LoggerFactory.getLogger(BrandController.class);

    @Resource(name = "productBrandServiceImpl")
    private IBrandService iBrandService;


    @RequestMapping("/selectActiveBrands")
    @ResponseBody
    public ResultMessage queryActiveBrands() {
        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
            List<Map<String, Object>> brands = iBrandService.queryActiveBrand();
            resultMessage.successStatus().putMsg("info", "SUCCESS").setData(brands);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" error message : {}", e.getMessage());
            resultMessage.errorStatus().putMsg("info", "error message : " + e.getMessage());
        }
        return resultMessage;
    }
}
