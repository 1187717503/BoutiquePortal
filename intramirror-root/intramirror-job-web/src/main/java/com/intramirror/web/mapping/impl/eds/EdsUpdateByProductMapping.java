package com.intramirror.web.mapping.impl.eds;

import com.alibaba.fastjson15.JSONArray;
import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.web.mapping.api.IProductMapping;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.model.SuiTable;
import pk.shoplus.model.TechnicalInfo;
import pk.shoplus.util.ExceptionUtils;

@Service(value = "edsUpdateByProductMapping")
public class EdsUpdateByProductMapping implements IProductMapping {

    private final static Logger logger = Logger.getLogger(EdsUpdateByProductMapping.class);

    public static ProductEDSManagement productEDSManagement = new ProductEDSManagement();

    @Override
    public ProductEDSManagement.ProductOptions mapping(Map<String, Object> bodyDataMap) {
        logger.info("EdsUpdateByProductMapping,inputParams,bodyDataMap:"+new Gson().toJson(bodyDataMap));
        ProductEDSManagement.ProductOptions productOptions = productEDSManagement.getProductOptions();
        try {
            JSONObject productMap = com.alibaba.fastjson15.JSON.parseObject(bodyDataMap.get("product").toString());
            String full_update_product = bodyDataMap.get("full_update_product") == null ? "0" : bodyDataMap.get("full_update_product").toString();
            String oneCategory = productMap.getString("gender");
            String twoCategory = productMap.getString("first_category");
            String threeCategory = productMap.getString("second_category");
            productOptions.setCategory1(oneCategory);
            productOptions.setCategory2(twoCategory);
            productOptions.setCategory3(threeCategory);

            // get composition
            String technicalInfoString = "";
            try {
                List<TechnicalInfo> technicalInfoList = (List<TechnicalInfo>) productMap.get("technical_info");
                for (int t = 0,len=technicalInfoList.size(); t < len; t++) {
                    Map mapTechnicalInfo = (Map) technicalInfoList.get(t);
                    technicalInfoString += mapTechnicalInfo.get("name").toString() + mapTechnicalInfo.get("percentage").toString();
                }
            } catch (Exception e) {
                logger.info("EdsUpdateByProductMapping,convertComposition,errorMessage:"+ExceptionUtils.getExceptionDetail(e)+
                ",productMap:"+new Gson().toJson(productMap));
                e.printStackTrace();
            }

            // get sizeFit
            String sizeFitValue = "";
            try {
                List<SuiTable> suiTableList = (List<SuiTable>) productMap.get("suitable");
                for(int i = 0,len=suiTableList.size();i<len;i++) {
                    Map map = (Map)suiTableList.get(i);
                    sizeFitValue += map.get("name").toString()+":"+map.get("value").toString()+",";
                }
            } catch (Exception e) {
                logger.info("EdsUpdateByProductMapping,convertSizeFit,errorMessage:"+ExceptionUtils.getExceptionDetail(e)+
                        ",productMap:"+new Gson().toJson(productMap));
                e.printStackTrace();
            }

            // get img
            Map<String, List<String>> stringListMap;
            try {
                stringListMap = (Map<String, List<String>>) productMap.getObject("item_images", Map.class);
                productOptions.setCoverImg(stringListMap.get("full").toString());
                productOptions.setDescImg(stringListMap.get("full").toString());
            } catch(Exception e) {
                stringListMap = new HashMap<>();
                logger.info("EdsUpdateByProductMapping,convertImg,errorMessage:"+ExceptionUtils.getExceptionDetail(e)+
                ",productMap:"+new Gson().toJson(productMap));
            }

            // set value
            productOptions.setName(productMap.getString("item_name"))
                    .setCode(productMap.getString("product_id"))
                    .setBrandCode(productMap.getString("product_reference"))
                    .setSeasonCode(productMap.getString("season_year")+productMap.getString("season_reference"))
                    .setBrandName(productMap.getString("brand"))
                    .setColorCode(productMap.getString("color_reference"))
                    .setColorDesc(productMap.getString("color"))
                    .setDesc(productMap.getString("item_description"))
                    .setMadeIn(productMap.getString("made_in"))
                    .setComposition(technicalInfoString)
                    .setCategory_name(oneCategory+"-"+twoCategory+"-"+threeCategory)
                    .setSizeFit(sizeFitValue)
                    .setWeight(stringListMap.get("weight") == null ? "" : stringListMap.get("weight").toString())
                    .setLength(stringListMap.get("length") == null ? "" : stringListMap.get("length").toString())
                    .setWidth(stringListMap.get("width") == null ? "" : stringListMap.get("width").toString())
                    .setHeigit(stringListMap.get("height") == null ? "" : stringListMap.get("height").toString())
                    .setSalePrice(productMap.getString("retail_price"))
                    .setFullUpdateProductFlag(full_update_product)
                    .setLast_check(new Date());

            // set get skus
            JSONArray skus = JSONArray.parseArray(productMap.getString("variants"));
            if(skus != null) {
                for (Object it : skus) {
                    JSONObject item = (JSONObject) it;
                    ProductEDSManagement.SkuOptions sku = productEDSManagement.getSkuOptions();
                    List<String> barcodesList = (List<String>) item.get("barcodes");
                    if(barcodesList != null && barcodesList.size() > 0) {
                        sku.setBarcodes(barcodesList.get(0));
                    }
                    sku.setSize(item.getString("size"));
                    sku.setStock(item.getString("quantity"));
                    productOptions.getSkus().add(sku);
                }
            } else {
                String size = productMap.getString("size");
                String quantity = productMap.getString("quantity");
                ProductEDSManagement.SkuOptions skuOptions = productEDSManagement.getSkuOptions();
                skuOptions.setSize(size);
                skuOptions.setStock(quantity);
                productOptions.getSkus().add(skuOptions);
            }

            logger.info("EdsUpdateByProductMapping,outParams,productOptions:"+new Gson().toJson(productOptions));
        } catch (Exception e) {
            logger.info("EdsUpdateByProductMapping,errorMessage:"+ ExceptionUtils.getExceptionDetail(e)
            +",bodyDataMap:"+new Gson().toJson(bodyDataMap));
            e.printStackTrace();
        }
        return productOptions;
    }
}
