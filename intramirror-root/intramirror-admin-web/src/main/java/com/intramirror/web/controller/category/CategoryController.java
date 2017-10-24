package com.intramirror.web.controller.category;

import com.intramirror.common.help.ResultMessage;
import com.intramirror.product.api.model.Category;
import com.intramirror.product.api.service.category.ICategoryService;
import com.intramirror.web.controller.brand.BrandController;
import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dingyifan on 2017/7/19.
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    private static Logger logger = LoggerFactory.getLogger(BrandController.class);

    @Resource(name = "productCategoryServiceImpl")
    private ICategoryService iCategoryService;

    @RequestMapping("/selectActiveCategorys")
    @ResponseBody
    public ResultMessage queryActiveCategorys() {
        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
            List<Category> categories = iCategoryService.queryActiveCategorys();
            resultMessage.successStatus().putMsg("info", "SUCCESS").setData(categories);
        } catch (Exception e) {
            logger.error(" error message : {}", e.getMessage(), e);
            resultMessage.errorStatus().putMsg("info", "error message : " + e.getMessage());
        }
        return resultMessage;
    }
}
