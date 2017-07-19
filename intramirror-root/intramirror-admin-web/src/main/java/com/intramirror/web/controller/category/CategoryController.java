package com.intramirror.web.controller.category;

import com.intramirror.common.help.ResultMessage;
import com.intramirror.product.api.model.Category;
import com.intramirror.product.api.service.category.ICategoryService;
import com.intramirror.web.controller.brand.BrandController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by dingyifan on 2017/7/19.
 */
@CrossOrigin
@Controller
@RequestMapping("/category")
public class CategoryController {

    private static Logger logger = LoggerFactory.getLogger(BrandController.class);

    @Resource(name = "productCategoryServiceImpl")
    private ICategoryService iCategoryService;

    @CrossOrigin
    @RequestMapping("/selectActiveCategorys")
    @ResponseBody
    public ResultMessage queryActiveCategorys(){
        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
            List<Category> categories = iCategoryService.queryActiveCategorys();
            resultMessage.successStatus().putMsg("info","SUCCESS").setData(categories);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" error message : {}", e.getMessage());
            resultMessage.errorStatus().putMsg("info","error message : " + e.getMessage());
        }
        return resultMessage;
    }
}
