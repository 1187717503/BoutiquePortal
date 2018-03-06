package com.intramirror.web.mapping.impl.atelier;

import com.alibaba.fastjson.JSONObject;
import com.intramirror.web.mapping.api.IProductMapping;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.util.ExceptionUtils;

/**
 * Created by dingyifan on 2017/9/19.
 */
@Service(value = "atelierUpdateByProductMapping")
public class AtelierUpdateByProductMapping implements IProductMapping {

    // logger
    private static final Logger logger = Logger.getLogger(AtelierUpdateByProductMapping.class);

    // productEDSManagement
    private static ProductEDSManagement productEDSManagement = new ProductEDSManagement();

    @Override
    public ProductEDSManagement.ProductOptions mapping(Map<String, Object> bodyDataMap) {
        logger.info("AtelierUpdateByProductMapping,inputParams,bodyDataMap:" + JSONObject.toJSONString(bodyDataMap));

        ProductEDSManagement.ProductOptions productOptions = productEDSManagement.getProductOptions();
        try {
            productOptions
                    .setName(getString(bodyDataMap, "product_name"))
                    .setCode(getString(bodyDataMap, "boutique_id").trim())
                    .setSeasonCode(getString(bodyDataMap, "season_code"))
                    .setBrandCode(getString(bodyDataMap, "brand_id").trim())
                    .setCarryOver(getString(bodyDataMap, "carry_over"))
                    .setBrandName(getString(bodyDataMap, "brand").trim())
                    .setColorCode(getString(bodyDataMap, "color_code"))
                    .setColorDesc(getString(bodyDataMap, "color_description"))
                    .setCategory_boutique_id(getString(bodyDataMap, "category_id"))
                    .setCategoryId(getString(bodyDataMap, "category_id"))
                    .setDesc(getString(bodyDataMap, "product_description"))
                    .setComposition(getString(bodyDataMap, "composition"))
                    .setMadeIn(getString(bodyDataMap, "made_in"))
                    .setSizeFit(getString(bodyDataMap, "size_fit"))
                    .setCoverImg(getString(bodyDataMap, "cover_img"))
                    .setDescImg(getString(bodyDataMap, "description_img"))
                    .setWeight(getString(bodyDataMap, "weight"))
                    .setLength(getString(bodyDataMap, "length"))
                    .setWidth(getString(bodyDataMap, "width"))
                    .setHeigit(getString(bodyDataMap, "height"))
                    .setSalePrice(getString(bodyDataMap, "sale_price"))
                    .setCategory1(getString(bodyDataMap, "category_l1"))
                    .setCategory2(getString(bodyDataMap, "category_l2"))
                    .setCategory3(getString(bodyDataMap, "category_l3"))
                    .setLast_check(new Date());

            List<Map<String, Object>> skus = (List<Map<String, Object>>) bodyDataMap.get("sku");
            for (Map<String, Object> item : skus) {
                ProductEDSManagement.SkuOptions sku = productEDSManagement.getSkuOptions();
                sku.setBarcodes(getString(item, "barcode"));
                sku.setSize(getString(item, "size"));
                sku.setStock(getString(item, "stock"));
                productOptions.getSkus().add(sku);
            }

            String carryOver = getString(bodyDataMap, "carry_over");
            if (StringUtils.isNotBlank(carryOver) && StringUtils.trim(carryOver).equalsIgnoreCase("CO")) {
                productOptions.setSeasonCode("CO");
            }

            /*try {
                String vendor_id = getString("vendor_id").toString();

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
            }*/

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("AtelierUpdateByProductMapping,errorMessage:" + ExceptionUtils.getExceptionDetail(e));
        }
        logger.info("AtelierUpdateByProductMapping,outputParams,productOptions:" + JSONObject.toJSONString(productOptions));
        return productOptions;
    }

    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value == null ? null : value.toString();
    }

}
