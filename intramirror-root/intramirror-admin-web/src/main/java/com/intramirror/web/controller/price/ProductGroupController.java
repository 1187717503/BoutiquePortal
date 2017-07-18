package com.intramirror.web.controller.price;

import com.google.gson.Gson;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.product.api.model.ProductGroup;
import com.intramirror.product.api.service.IProductGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Wang
 */
@Controller
@RequestMapping("/productGroup")
public class ProductGroupController {

    private static Logger logger = LoggerFactory.getLogger(ProductGroupController.class);

    @Autowired
    private IProductGroupService productGroupService;

    @SuppressWarnings("unchecked")
    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> productGroupList(@RequestBody Map<String, Object> map) {
        logger.info("productGroupList param:" + new Gson().toJson(map));
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", StatusType.FAILURE);

        /**-------------------------------------ProductGroup-------------------------------------------------*/
        try {
            List<ProductGroup> productGroupList = productGroupService.getProductGroupListByGroupType(map.get("group_type").toString());
            result.put("productGroupList", productGroupList);
            result.put("status", StatusType.SUCCESS);
        } catch (Exception e) {
            result.put("status", StatusType.DATABASE_ERROR);
            return result;
        }

        return result;
    }


    /**
     * 检查参数
     */
    public static int checkParams(Map<String, String> params) {

//        checker.add("group_type", params.get("group_type")).required().maxLength(64).numeric();
//
//        Reports reports = checker.check();

//        return reports.firstError().toStatus();
        return 0;
    }

}
