package com.intramirror.web.controller.order;

import com.google.gson.Gson;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.order.api.model.CancelOrderVO;
import com.intramirror.order.api.vo.PageListVO;
import com.intramirror.product.api.service.IProductService;
import com.intramirror.product.api.service.brand.IBrandService;
import com.intramirror.user.api.model.User;
import com.intramirror.user.api.model.Vendor;
import com.intramirror.web.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@Controller
@RequestMapping("/brand")
public class BrandController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(BrandController.class);

    @Resource(name = "productBrandServiceImpl")
    private IBrandService iBrandService;

    /**
     * 查询品牌列表
     * @return
     */
    @RequestMapping(value = "/selectActiveBrands", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage selectActiveBrands() {
        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
            Integer type = null; // brand 类型 暂时不用
            /*if(StringUtils.isNotBlank(categoryType)){
                type = Integer.valueOf(categoryType);
            }*/
            List<Map<String, Object>> brands = iBrandService.queryActiveBrand(type);
            resultMessage.successStatus().putMsg("info", "SUCCESS").setData(brands);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" error message : {}", e.getMessage());
            resultMessage.errorStatus().putMsg("info", "error message : " + e.getMessage());
        }
        return resultMessage;
    }

}
