package com.intramirror.web.mapping.impl.atelier;

import com.alibaba.fastjson15.JSONArray;
import com.alibaba.fastjson15.JSONObject;
import com.intramirror.product.api.service.category.ICategoryService;
import com.intramirror.web.mapping.api.IProductMapping;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.service.MappingCategoryService;
import pk.shoplus.util.ExceptionUtils;

/**
 * Created by dingyifan on 2017/9/19.
 */
@Service(value = "atelierUpdateByProductMapping")
public class AtelierUpdateByProductMapping implements IProductMapping{

    // logger
    private static final Logger logger = Logger.getLogger(AtelierUpdateByProductMapping.class);

    // productEDSManagement
    private static ProductEDSManagement productEDSManagement = new ProductEDSManagement();

    @Autowired
    private ICategoryService categoryService;

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

            conn = DBConnector.sql2o.open();
            MappingCategoryService mappingCategoryService = new MappingCategoryService(conn);
            String vendor_id = bodyDataMap.get("vendor_id").toString();
            logger.info("AtelierUpdateByProductMapping,getMappingCategoryInfoByCondition,start,vendor_id:"+vendor_id+",productOptions:"+JSONObject.toJSONString(productOptions));
            List<Map<String,Object>> categoryMaps = mappingCategoryService.getMappingCategoryInfoByCondition(vendor_id,productOptions.getCategoryId());
            logger.info("AtelierUpdateByProductMapping,getMappingCategoryInfoByCondition,end,vendor_id:"+vendor_id+",productOptions:"+JSONObject.toJSONString(productOptions)+",categoryMaps:"+JSONObject.toJSONString(categoryMaps));
            if(categoryMaps != null && categoryMaps.size() >0){
                productOptions.setCategoryId(categoryMaps.get(0).get("category_id").toString());
            }
            if(conn != null) {conn.close();}

            if(StringUtils.isBlank(productOptions.getCategoryId()) && StringUtils.isBlank(jsonObjectData.getString("category_id"))) {
                String category_l1 = jsonObjectData.getString("category_l1")==null?"":jsonObjectData.getString("category_l1");
                String category_l2 = jsonObjectData.getString("category_l2")==null?"":jsonObjectData.getString("category_l2");
                String category_l3 = jsonObjectData.getString("category_l3")==null?"":jsonObjectData.getString("category_l3");

                Map<String, Object> categoryMap = new HashMap<String, Object>();
                categoryMap.put("vendor_id", bodyDataMap.get("vendor_id"));
                categoryMap.put("boutique_second_category", category_l2);
                categoryMap.put("boutique_third_category", category_l3);
                categoryMap.put("boutique_first_category", category_l1);
                productOptions.setCategory_name(JSONObject.toJSONString(categoryMap));
                productOptions.setCategory1(category_l1);
                productOptions.setCategory2(category_l2);
                productOptions.setCategory3(category_l3);
                logger.info("AtelierUpdateByProductMapping,getCategoryByCondition,categoryMap:"+JSONObject.toJSONString(categoryMap));
                List<Map<String, Object>> apiCategoryMap = categoryService.getCategoryByCondition(categoryMap);
                logger.info("AtelierUpdateByProductMapping,getCategoryByCondition,apiCategoryMap:"+JSONObject.toJSONString(apiCategoryMap)+",categoryMap:"+JSONObject.toJSONString(categoryMap));
                if(null != apiCategoryMap && 0 < apiCategoryMap.size()) {
                    productOptions.setCategoryId(apiCategoryMap.get(0).get("category_id").toString());
                }
            }
            if(conn != null) {conn.close();}
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("AtelierUpdateByProductMapping,errorMessage:"+ExceptionUtils.getExceptionDetail(e));
            if(conn != null) {conn.close();}
        }
        logger.info("AtelierUpdateByProductMapping,outputParams,productOptions:"+JSONObject.toJSONString(productOptions));
        return productOptions;
    }
}
