package com.intramirror.web.controller.api.service;

import com.alibaba.fastjson.JSONObject;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import pk.shoplus.model.Product;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.service.CategoryService;
import pk.shoplus.service.ProductService;
import pk.shoplus.util.DateUtils;

/**
 * Created on 2018/3/7.
 *
 * @author YouFeng.Zhu
 */
public abstract class AbstractService {
    private static final Logger logger = Logger.getLogger(AbstractService.class);

    protected ProductEDSManagement.ProductOptions productOptions;

    protected ProductEDSManagement.VendorOptions vendorOptions;

    protected Product uProduct;

    protected Map<String, Object> result = new HashMap<>();

    protected void printChangeLog(Connection conn) throws Exception {
        // 查询Product
        Product product = this.getProduct(conn);
        if (product == null) {
            return;
        }

        // skuLog
        CategoryService categoryService = new CategoryService(conn);
        List<Map<String, Object>> skuList = categoryService.executeSQL(
                "select s.`sku_id` ,s.`sku_code` ,s.`size` ,s.`in_price` ,s.`im_price` ,s.`price` ,ss.`store` ,ss.`reserved` ,ss.`confirmed`  from `sku`  s\n"
                        + "left join `sku_store`  ss  on(s.`sku_id` = ss.`sku_id`  and ss.`enabled`  = 1 and s.`enabled`  = 1)\n" + "where s.`product_id`  ="
                        + product.getProduct_id());

        // productLog
        Map<String, Object> productMap = new HashMap<>();
        productMap.put("vendor_id", product.getVendor_id());
        productMap.put("boutique_id", product.getProduct_code());
        productMap.put("designer_id", product.getDesigner_id());
        productMap.put("color_code", product.getColor_code());
        productMap.put("season_code", product.getSeason_code());
        productMap.put("category_id", product.getCategory_id());
        productMap.put("brand_id", product.getBrand_id());
        productMap.put("cover_img", product.getCover_img());
        productMap.put("retail_price", product.getMax_retail_price());
        productMap.put("last_check", product.getLast_check());
        productMap.put("skus", skuList);
        if (StringUtils.isNotBlank(productOptions.getRequestId())) {
            result.put("successData", productMap);
        }

        String productLog = "Prodcut Change Record,product:" + JSONObject.toJSONString(productMap) + ",skus:" + JSONObject.toJSONString(skuList) + ",update_at:"
                + DateUtils.getformatDate(new Date());
        logger.info(productLog);
    }

    protected Product getProduct(Connection conn) throws Exception {
        if (this.uProduct == null) {
            ProductService productService = new ProductService(conn);
            Map<String, Object> condition = new HashMap<>();
            condition.put("product_code", productOptions.getCode());
            condition.put("enabled", 1);
            condition.put("vendor_id", vendorOptions.getVendorId());
            Product product = productService.getProductByCondition(condition, null);
            this.uProduct = product;
        }
        return this.uProduct;
    }
}

