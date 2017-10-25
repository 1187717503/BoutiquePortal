package com.intramirror.web.mapping.impl.atelier;

import com.alibaba.fastjson15.JSONArray;
import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.web.mapping.api.IProductMapping;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.MappingCategoryService;
import pk.shoplus.util.ExceptionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static pk.shoplus.enums.ApiErrorTypeEnum.errorType.Runtime_exception;

/**
 * Created by dingyifan on 2017/9/19.
 */
@Service(value = "atelierUpdateByProductMapping")
public class AtelierUpdateByProductMapping implements IProductMapping{

    // logger
    private static final Logger logger = Logger.getLogger(AtelierUpdateByProductMapping.class);

    // productEDSManagement
    private static ProductEDSManagement productEDSManagement = new ProductEDSManagement();

    @Override
    public ProductEDSManagement.ProductOptions mapping(Map<String, Object> bodyDataMap) {
        logger.info("AtelierUpdateByProductMapping,inputParams,bodyDataMap:"+JSONObject.toJSONString(bodyDataMap));
        ProductEDSManagement.ProductOptions productOptions = productEDSManagement.getProductOptions();
        Connection conn = null;
        try {
            JSONObject jsonObjectData =  JSONObject.parseObject(bodyDataMap.get("Data").toString()) ;
            productOptions.setName(jsonObjectData.getString("product_name"))
                    .setCode(jsonObjectData.getString("boutique_id").trim())
                    .setSeasonCode(jsonObjectData.getString("season_code"))
                    .setBrandCode(jsonObjectData.getString("brand_id").trim())
                    .setCarryOver(jsonObjectData.getString("carry_over"))
                    .setBrandName(jsonObjectData.getString("brand").trim())
                    .setColorCode(jsonObjectData.getString("color_code"))
                    .setColorDesc(jsonObjectData.getString("color_description"))
                    .setCategoryId(jsonObjectData.getString("category_id"))
                    .setCategory_name(jsonObjectData.getString("category_id"))
                    .setDesc(jsonObjectData.getString("product_description"))
                    .setComposition(jsonObjectData.getString("composition"))
                    .setMadeIn(jsonObjectData.getString("made_in"))
                    .setSizeFit(jsonObjectData.getString("size_fit"))
                    .setCoverImg(jsonObjectData.getString("cover_img"))
                    .setDescImg(jsonObjectData.getString("description_img"))
                    .setWeight(jsonObjectData.getString("weight"))
                    .setLength(jsonObjectData.getString("length"))
                    .setWidth(jsonObjectData.getString("width"))
                    .setHeigit(jsonObjectData.getString("height"))
                    .setSalePrice(jsonObjectData.getString("sale_price"))
                    .setLast_check(new Date());
            String full_update_product = bodyDataMap.get("full_update_product") == null ? "0" : bodyDataMap.get("full_update_product").toString();
            productOptions.setFullUpdateProductFlag(full_update_product);

            JSONArray skus = JSONArray.parseArray(jsonObjectData.getString("sku"));
            for (Object it : skus) {
                JSONObject item = (JSONObject) it;
                ProductEDSManagement.SkuOptions sku = productEDSManagement.getSkuOptions();
                sku.setBarcodes(item.getString("barcode"));
                sku.setSize(item.getString("size"));
                sku.setStock(item.getString("stock"));
                productOptions.getSkus().add(sku);
            }

            conn = DBConnector.sql2o.beginTransaction();
            MappingCategoryService mappingCategoryService = new MappingCategoryService(conn);
            String vendor_id = bodyDataMap.get("vendor_id").toString();
            logger.info("AtelierUpdateByProductMapping,getMappingCategoryInfoByCondition,start,vendor_id:"+vendor_id+",productOptions:"+JSONObject.toJSONString(productOptions));
            List<Map<String,Object>> categoryMaps = mappingCategoryService.getMappingCategoryInfoByCondition(vendor_id,productOptions.getCategoryId());
            logger.info("AtelierUpdateByProductMapping,getMappingCategoryInfoByCondition,end,vendor_id:"+vendor_id+",productOptions:"+JSONObject.toJSONString(productOptions)+",categoryMaps:"+JSONObject.toJSONString(categoryMaps));
            if(categoryMaps != null && categoryMaps.size() >0){
                productOptions.setCategoryId(categoryMaps.get(0).get("category_id").toString());
            }
            if(conn != null) {conn.commit();conn.close();}
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("AtelierUpdateByProductMapping,errorMessage:"+ExceptionUtils.getExceptionDetail(e));
            if(conn != null) {conn.rollback();conn.close();}
        }
        logger.info("AtelierUpdateByProductMapping,outputParams,productOptions:"+JSONObject.toJSONString(productOptions));
        return productOptions;
    }
}
