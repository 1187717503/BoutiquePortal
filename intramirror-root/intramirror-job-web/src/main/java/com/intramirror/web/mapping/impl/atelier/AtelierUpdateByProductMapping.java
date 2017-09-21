package com.intramirror.web.mapping.impl.atelier;

import com.alibaba.fastjson15.JSONArray;
import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.web.mapping.api.IProductMapping;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.util.ExceptionUtils;

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
                    .setSalePrice(jsonObjectData.getString("sale_price"));
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
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("AtelierUpdateByProductMapping,errorMessage:"+ExceptionUtils.getExceptionDetail(e));
        }
        logger.info("AtelierUpdateByProductMapping,outputParams,productOptions:"+JSONObject.toJSONString(productOptions));
        return productOptions;
    }
}
