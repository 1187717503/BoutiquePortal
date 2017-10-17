package com.intramirror.web.controller.api.product;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.intramirror.common.help.ExceptionUtils;
import com.intramirror.common.parameter.StatusType;
import org.apache.commons.io.IOUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pk.shoplus.model.ProductEDSManagement;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dingyifan on 2017/10/12.
 */
@Controller
@RequestMapping("/product/management")
public class CreateProductController {

    private static Logger logger = Logger.getLogger(CreateProductController.class);

    @ResponseBody
    @RequestMapping(value = "/createproduct", method = RequestMethod.POST)
    public Map<String,Object> createProduct(HttpServletRequest request){
        Map<String,Object> resultMap = new HashMap<>();
        try {
            InputStream is = request.getInputStream();
            String body = IOUtils.toString(is, "utf-8");
            JSONObject data = JSON.parseObject(body);

            ProductEDSManagement productEDSManagement = new ProductEDSManagement();
            ProductEDSManagement.ProductOptions productOptions = data.getObject("productOptions",ProductEDSManagement.ProductOptions.class);
            ProductEDSManagement.VendorOptions vendorOptions =  data.getObject("vendorOptions",ProductEDSManagement.VendorOptions.class);

            logger.info("CreateProductController,createProduct,productOptions:"+JSONObject.toJSONString(productOptions)+",vendorOptions:"+vendorOptions);
            resultMap = productEDSManagement.createProduct(productOptions,vendorOptions);
            logger.info("CreateProductController,createProduct,resultMap:"+JSONObject.toJSONString(resultMap)+",productOptions:"+JSONObject.toJSONString(productOptions)+",vendorOptions:"+vendorOptions);

        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", StatusType.FAILURE);
            resultMap.put("info", ExceptionUtils.getExceptionDetail(e));
            logger.info("CreateProductController,errorMessage:"+ExceptionUtils.getExceptionDetail(e));
        }

        logger.info("CreateProductController,outputParams,resultMap:"+JSONObject.toJSONString(resultMap));
        return resultMap;
    }

    public static void main(String[] args) {
        ProductEDSManagement productEDSManagement = new ProductEDSManagement();

        ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
        vendorOptions.setVendorId(10L);

        ProductEDSManagement.ProductOptions productOptions = productEDSManagement.getProductOptions();
        productOptions.setCode("123");

        Map<String,Object> paramsMap = new HashMap<>();
        paramsMap.put("vendorOptions",vendorOptions);
        paramsMap.put("productOptions",productOptions);
        System.out.println(JSONObject.toJSON(paramsMap));

        ProductEDSManagement.ProductOptions productOptions2 = (ProductEDSManagement.ProductOptions) paramsMap.get("productOptions");
        System.out.println(JSONObject.toJSON(productOptions2));
    }
}
