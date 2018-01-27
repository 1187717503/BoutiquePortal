package com.intramirror.web.controller.api.atelier;

import com.alibaba.fastjson.JSONObject;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.model.Product;
import pk.shoplus.service.ProductService;
import pk.shoplus.service.price.api.IPriceService;
import pk.shoplus.service.price.impl.PriceServiceImpl;

/**
 * Created by dingyifan on 2018/1/27.
 */
@Controller
@RequestMapping("/rollback")
public class DataRollbackController {

    private static final Logger logger = Logger.getLogger(DataRollbackController.class);

    @RequestMapping(value = "/category",method = RequestMethod.GET)
    @ResponseBody
    public String category(){
        IPriceService iPriceService = new PriceServiceImpl();
        Connection conn = null;
        try {
            conn = DBConnector.sql2o.open();

            // category
            String categorySQL = "select p.`product_id` ,p.`category_id` ,pb.category_id as pbCategory_id from `product`  p \n"
                    + "inner join product_bak_prd pb on(p.`product_id` = pb.product_id)\n" + "where p.`category_id`  != pb.category_id";
            ProductService productService = new ProductService(conn);

            List<Map<String,Object>> categoryMap = productService.executeSQL(categorySQL);
            for(Map<String,Object> category : categoryMap) {

                Long product_id = Long.parseLong(category.get("product_id").toString());
                Long category_id = Long.parseLong(category.get("category_id").toString());
                Long pbCategory_id = Long.parseLong(category.get("pbCategory_id").toString());

                logger.info("DataRollbackController,start,category:"+ JSONObject.toJSONString(category));
                Product product = productService.getProductById(product_id);
                product.category_id = pbCategory_id;
                iPriceService.synProductPriceRule(product,product.getMin_retail_price(),conn);
                productService.updateProduct(product);
                logger.info("DataRollbackController,end,category:"+ JSONObject.toJSONString(category));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("DataRollbackController,categoryErrorMsg:"+e);
        } finally {
            if(conn != null) {conn.close();}
        }

        return null;
    }

    @RequestMapping(value = "/brand",method = RequestMethod.GET)
    @ResponseBody
    public String brand(){
        IPriceService iPriceService = new PriceServiceImpl();
        Connection conn = null;
        try {
            conn = DBConnector.sql2o.open();

            // brand
            String brandSQL = "select p.`product_id` ,p.`brand_id` ,pb.brand_id as pbBrand_id from `product`  p \n"
                    + "inner join product_bak_prd pb on(p.`product_id` = pb.product_id)\n" + "where p.`brand_id`  != pb.brand_id; ";
            ProductService productService = new ProductService(conn);

            List<Map<String,Object>> brandMap = productService.executeSQL(brandSQL);
            for(Map<String,Object> brand : brandMap) {

                Long product_id = Long.parseLong(brand.get("product_id").toString());
                Long brand_id = Long.parseLong(brand.get("brand_id").toString());
                Long pbBrand_id = Long.parseLong(brand.get("pbBrand_id").toString());

                logger.info("DataRollbackController,start,brand:"+ JSONObject.toJSONString(brand));
                Product product = productService.getProductById(product_id);
                product.brand_id = pbBrand_id;
                iPriceService.synProductPriceRule(product,product.getMin_retail_price(),conn);
                productService.updateProduct(product);
                logger.info("DataRollbackController,end,brand:"+ JSONObject.toJSONString(brand));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("DataRollbackController,brandErrorMsg:"+e);
        } finally {
            if(conn != null) {conn.close();}
        }

        return null;
    }

}
