package com.intramirror.web.controller.order;

import com.intramirror.common.help.ResultMessage;
import com.intramirror.main.api.service.AddressCityService;
import com.intramirror.main.api.service.AddressCountryService;
import com.intramirror.main.api.service.AddressDistrictService;
import com.intramirror.main.api.service.AddressProvinceService;
import com.intramirror.product.api.service.ProductPropertyService;
import com.intramirror.user.api.model.User;
import com.intramirror.web.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/order")
public class OrderGetProductController extends BaseController {


    @Autowired
    private ProductPropertyService productPropertyService;

    /**
     * Wang
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/get_product", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage orderGetProduct(@RequestBody Map<String, Object> map, HttpServletRequest httpRequest) throws Exception {
        ResultMessage result = new ResultMessage();
        Map<String, Object> results = new HashMap<>();
        result.errorStatus();

        User user = this.getUserInfo(httpRequest);
        if (user == null) {
            result.setMsg("Please log in again");
            return result;
        }

        String brandID = null;
        String colorCode = null;

        if (map.get("brandID") != null && StringUtils.isNotBlank(map.get("brandID").toString())) {
            brandID = map.get("brandID").toString();
        }

        if (map.get("colorCode") != null && StringUtils.isNotBlank(map.get("colorCode").toString())) {
            colorCode = map.get("colorCode").toString();
        }

        try {

            List<Map<String, Object>> mapList = productPropertyService.getProductPropertyValueByBrandIdAndColorCode(brandID, colorCode);
            for (Map maps : mapList) {
                Double price = Double.parseDouble(maps.get("price").toString());
                Double inPrice = Double.parseDouble(maps.get("in_price").toString());
                BigDecimal discount = new BigDecimal((inPrice * (1 + 0.22) / price) * 100);
                maps.put("discount", (100 - discount.setScale(2, BigDecimal.ROUND_HALF_UP).intValue()) + " %");
            }

            results.put("mapList", mapList);

            result.successStatus();
            result.setData(results);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
