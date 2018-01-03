package com.intramirror.web.mapping.impl.atelier;

import com.alibaba.fastjson15.JSONArray;
import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.web.mapping.api.IProductMapping;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import pk.shoplus.model.ProductEDSManagement;
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
                    .setCategory_boutique_id(jsonObjectData.getString("category_id"))
                    .setCategoryId(jsonObjectData.getString("category_id"))
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
                    .setCategory1(jsonObjectData.getString("category_l1"))
                    .setCategory2(jsonObjectData.getString("category_l2"))
                    .setCategory3(jsonObjectData.getString("category_l3"))
                    .setLast_check(new Date());

            JSONArray skus = JSONArray.parseArray(jsonObjectData.getString("sku"));
            for (Object it : skus) {
                JSONObject item = (JSONObject) it;
                ProductEDSManagement.SkuOptions sku = productEDSManagement.getSkuOptions();
                sku.setBarcodes(item.getString("barcode"));
                sku.setSize(item.getString("size"));
                sku.setStock(item.getString("stock"));
                productOptions.getSkus().add(sku);
            }

            try {
                String vendor_id = bodyDataMap.get("vendor_id").toString();

                if(vendor_id.equals("26")) {
                    List<String> originList = JSONArray.parseArray(productOptions.getCoverImg(), String.class);
                    String one = originList.get(0);
                    originList.remove(0);
                    originList.add(one);
                    productOptions.setCoverImg(new Gson().toJson(originList));
                    productOptions.setDescImg(new Gson().toJson(originList));
                }

            } catch (Exception e) {
                e.printStackTrace();
                logger.info("AtelierUpdateByProductService,imagesReplace,errorMessage:"+e);
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("AtelierUpdateByProductMapping,errorMessage:"+ExceptionUtils.getExceptionDetail(e));
        }
        logger.info("AtelierUpdateByProductMapping,outputParams,productOptions:"+JSONObject.toJSONString(productOptions));
        return productOptions;
    }


}
