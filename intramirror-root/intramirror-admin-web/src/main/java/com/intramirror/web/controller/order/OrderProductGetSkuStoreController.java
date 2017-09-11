package com.intramirror.web.controller.order;

import com.intramirror.common.help.ResultMessage;
import com.intramirror.product.api.model.ShopProduct;
import com.intramirror.product.api.service.ProductPropertyService;
import com.intramirror.product.api.service.ShopProductService;
import com.intramirror.product.api.service.ShopProductSkuService;
import com.intramirror.user.api.model.User;
import com.intramirror.web.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@Controller
@RequestMapping("/order")
public class OrderProductGetSkuStoreController extends BaseController {


    @Autowired
    private ShopProductSkuService shopProductSkuService;

    @Autowired
    private ShopProductService shopProductService;

    /**
     * Wang
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/product_get_skuStore", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage orderProductGetSkuStore(@RequestBody Map<String, Object> map, HttpServletRequest httpRequest) throws Exception {
        ResultMessage result = new ResultMessage();
        Map<String, Object> results = new HashMap<>();
        result.errorStatus();

        User user = this.getUserInfo(httpRequest);
        if (user == null) {
            result.setMsg("Please log in again");
            return result;
        }

        String shopProductId = null;

        if (map.get("shop_product_id") != null && StringUtils.isNotBlank(map.get("shop_product_id").toString())) {
            shopProductId = map.get("shop_product_id").toString();
        }

        try {
            List<Map<String, Object>> mapList = shopProductSkuService.getSkuByShopProductId(Long.valueOf(shopProductId));
            ShopProduct shopProduct = shopProductService.getShopProductById(Long.valueOf(shopProductId));
            results.put("mapList", mapList);
            results.put("shopProduct", shopProduct);
            result.successStatus();
            result.setData(results);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
