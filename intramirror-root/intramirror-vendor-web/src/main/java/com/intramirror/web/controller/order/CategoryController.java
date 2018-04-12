package com.intramirror.web.controller.order;

import com.intramirror.common.help.ResultMessage;
import com.intramirror.product.api.model.Category;
import com.intramirror.product.api.service.category.ICategoryService;
import com.intramirror.web.controller.BaseController;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@CrossOrigin
@Controller
@RequestMapping("/category")
public class CategoryController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Resource(name = "productCategoryServiceImpl")
    private ICategoryService iCategoryService;

    /**
     * 查询类目树列表
     * @return
     */
    @RequestMapping(value = "/selectActiveCategorys", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage selectActiveCategorys() {
        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
            List<Category> categories = iCategoryService.queryActiveCategorys();
            // category 顺序特殊处理
            if(CollectionUtils.isNotEmpty(categories)){
                for(Category category : categories){
                    if(category.getCategoryId().equals(Long.valueOf(1499))){ // Men
                        List<Category> childs = category.getChildren();
                        Category tmpB = null;
                        Category tmpS = null;
                        for(Category cate : childs){
                            if(cate.getCategoryId().equals(Long.valueOf(1505))){ // Bags
                                tmpB = cate;
                            }
                            if(cate.getCategoryId().equals(Long.valueOf(1506))){ // Shoes
                                tmpS = cate;
                            }
                        }
                        if(tmpB!=null && tmpS!=null){
                            childs.add(1,tmpS);
                            childs.add(2,tmpB);
                            childs.remove(3);
                            childs.remove(3);
                        }
                        break;
                    }
                }
            }

            resultMessage.successStatus().putMsg("info", "SUCCESS").setData(categories);
        } catch (Exception e) {
            logger.error(" error message : {}", e.getMessage(), e);
            resultMessage.errorStatus().putMsg("info", "error message : " + e.getMessage());
        }
        return resultMessage;
    }

}
