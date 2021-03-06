package com.intramirror.web.controller.price;

import com.google.gson.Gson;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.product.api.model.ProductGroup;
import com.intramirror.product.api.service.IProductGroupService;
import com.intramirror.web.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Wang
 */
@Controller
@RequestMapping("/productGroup")
public class ProductGroupController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(ProductGroupController.class);

    @Autowired
    private IProductGroupService productGroupService;


    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> productGroupList(@RequestBody Map<String, Object> map) throws Exception {
        logger.info("productGroupList param:" + new Gson().toJson(map));
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", StatusType.FAILURE);

        if (!checkParams(map)) {
            result.put("info", "parameter is incorrect");
            return result;
        }

        /**-------------------------------------ProductGroup-------------------------------------------------*/
        try {
            List<ProductGroup> productGroupList = productGroupService.getProductGroupListByGroupTypeAndVendorId(map.get("group_type").toString(), Long.valueOf(map.get("vendor_id").toString()));
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
    public static boolean checkParams(Map<String, Object> params) {

        if (params.get("group_type") == null || StringUtils.isBlank(params.get("group_type").toString())
                || checkIntegerNumber(params.get("group_type").toString())) {
            return false;
        }
        return true;
    }

    private static boolean checkIntegerNumber(String vue) {
        if (StringUtils.isNotBlank(vue)) {
            try {
                Integer.parseInt(vue);
            } catch (Exception e) {
                return true;
            }
        }
        return false;
    }


}
