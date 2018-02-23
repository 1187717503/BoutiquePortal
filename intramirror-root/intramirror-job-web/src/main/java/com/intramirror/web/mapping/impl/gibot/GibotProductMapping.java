package com.intramirror.web.mapping.impl.gibot;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intramirror.utils.transform.JsonTransformUtil;
import com.intramirror.web.mapping.api.IProductMapping;
import java.util.Date;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.util.ExceptionUtils;

/**
 * Created on 2018/2/9.
 *
 * @author YouFeng.Zhu
 */
@Service(value = "gibotProductMapping")
public class GibotProductMapping implements IProductMapping {
    // logger
    private static final Logger LOGGER = Logger.getLogger(GibotProductMapping.class);

    @Override
    public ProductEDSManagement.ProductOptions mapping(Map<String, Object> bodyDataMap) {
        LOGGER.info("AtelierUpdateByProductMapping,inputParams,bodyDataMap:" + JSONObject.toJSONString(bodyDataMap));
        ProductEDSManagement.ProductOptions productOptions = new ProductEDSManagement.ProductOptions();
        try {
            JSONObject jsonObjectData = JSONObject.parseObject(JsonTransformUtil.toJson(bodyDataMap.get("product")));
            productOptions
                    .setName(jsonObjectData.getString("name"))
                    .setCode(jsonObjectData.getString("product_id").trim())
                    .setSeasonCode(jsonObjectData.getString("collection_name"))
                    .setSeasonCode("18SS")
                    .setBrandCode(jsonObjectData.getString("modelcode").trim())
                    .setCarryOver("")
                    .setBrandName(jsonObjectData.getString("brand").trim())
                    .setColorCode(jsonObjectData.getString("variantcode"))
                    .setColorDesc("")
                    .setCategory_boutique_id("")
                    .setCategoryId("")
                    .setDesc(jsonObjectData.getString("text"))
                    .setComposition("")
                    .setMadeIn("")
                    .setSizeFit("")
                    .setCoverImg(jsonObjectData.getString("images"))
                    .setDescImg(jsonObjectData.getString("images"))
                    .setWeight("")
                    .setLength("")
                    .setWidth("")
                    .setHeigit("")
                    .setSalePrice(jsonObjectData.getString("pricewas"))
                    .setCategory1(jsonObjectData.getString("gender")) //todo
                    .setCategory2(jsonObjectData.getString("group_name"))//todo
                    .setCategory3(jsonObjectData.getString("category_name"))//todo
                    .setLast_check(new Date());

            JSONArray skus = jsonObjectData.getJSONArray("skus");
            for (Object it : skus) {
                JSONObject item = (JSONObject) it;
                ProductEDSManagement.SkuOptions sku = new ProductEDSManagement.SkuOptions();
                sku.setBarcodes(item.getString("barcode"));
                sku.setSize(item.getString("label"));
                sku.setStock(item.getString("stock"));
                sku.setBoutique_sku_id(item.getString("sku_id"));
                productOptions.getSkus().add(sku);
            }

            String carryOver = jsonObjectData.getString("carry_over");
            if (StringUtils.isNotBlank(carryOver) && StringUtils.trim(carryOver).equalsIgnoreCase("CO")) {
                productOptions.setSeasonCode("CO");
            }

            /*try {
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
            }*/

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("AtelierUpdateByProductMapping,errorMessage:" + ExceptionUtils.getExceptionDetail(e));
        }
        LOGGER.info("AtelierUpdateByProductMapping,outputParams,productOptions:" + JSONObject.toJSONString(productOptions));
        return productOptions;
    }
}
