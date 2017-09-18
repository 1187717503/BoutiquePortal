package com.intramirror.web.controller.order;

import com.intramirror.common.help.ResultMessage;
import com.intramirror.main.api.service.AddressCityService;
import com.intramirror.main.api.service.AddressCountryService;
import com.intramirror.main.api.service.AddressDistrictService;
import com.intramirror.main.api.service.AddressProvinceService;
import com.intramirror.product.api.model.*;
import com.intramirror.product.api.service.*;
import com.intramirror.product.api.service.brand.IBrandService;
import com.intramirror.user.api.model.User;
import com.intramirror.user.api.model.Vendor;
import com.intramirror.user.api.service.VendorService;
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
public class OrderGetProductDetailController extends BaseController {


    @Autowired
    private IProductService productService;

    @Autowired
    private ShopProductService shopProductService;

    @Autowired
    private ShopProductSkuService shopProductSkuService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private ProductPropertyService productPropertyService;

    @Autowired
    private IBrandService brandService;

    @Autowired
    private VendorService vendorService;

    /**
     * Wang
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/get_product_detail", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage orderGetProductDetail(@RequestBody Map<String, Object> map, HttpServletRequest httpRequest) throws Exception {
        ResultMessage result = new ResultMessage();
        Map<String, Object> results = new HashMap<>();
        result.errorStatus();

        User user = this.getUserInfo(httpRequest);
        if (user == null) {
            result.setMsg("Please log in again");
            return result;
        }

        String productId = null;
        String shopProductId = null;
        String skuId = null;
        String shopProductSkuId = null;

        if (map.get("product_id") != null && StringUtils.isNotBlank(map.get("product_id").toString())) {
            productId = map.get("product_id").toString();
        }

        if (map.get("shop_product_id") != null && StringUtils.isNotBlank(map.get("shop_product_id").toString())) {
            shopProductId = map.get("shop_product_id").toString();
        }

        if (map.get("sku_id") != null && StringUtils.isNotBlank(map.get("sku_id").toString())) {
            skuId = map.get("sku_id").toString();
        }

        if (map.get("shop_product_sku_id") != null && StringUtils.isNotBlank(map.get("shop_product_sku_id").toString())) {
            shopProductSkuId = map.get("shop_product_sku_id").toString();
        }


        try {
            Product product = productService.selectByPrimaryKey(Long.valueOf(productId));
            ShopProduct shopProduct = shopProductService.getShopProductById(Long.valueOf(shopProductId));
            Sku sku = skuService.getSkuById(Long.valueOf(skuId));
            ShopProductSku shopProductSku = shopProductSkuService.getShopProductSkuById(Long.valueOf(shopProductSkuId));
            Map<String, Object> productPropertyMap = productPropertyService.getProductPropertyValueByProductId(Long.valueOf(productId));
            Map<String, Object> sizeMap = shopProductSkuService.getSkuBySkuId(Long.valueOf(skuId));
            Brand brand = brandService.getBrandById(product.getBrandId());
            Map<String, Object> vendorParams = new HashMap<>();
            vendorParams.put("vendor_id", product.getVendorId());
            Vendor vendor = vendorService.getVendorByVendorId(vendorParams);

            results.put("product", product);
            results.put("shopProduct", shopProduct);
            results.put("sku", sku);
            results.put("shopProductSku", shopProductSku);
            results.put("productPropertyMap", productPropertyMap);
            results.put("sizeMap", sizeMap);
            results.put("brand", brand);
            results.put("vendor", vendor);

            result.successStatus();
            result.setData(results);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
