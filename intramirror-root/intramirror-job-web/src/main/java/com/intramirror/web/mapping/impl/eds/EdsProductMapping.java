package com.intramirror.web.mapping.impl.eds;

import com.alibaba.fastjson15.JSONArray;
import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.web.mapping.api.IDataMapping;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.model.Category;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.model.SuiTable;
import pk.shoplus.model.TechnicalInfo;
import pk.shoplus.service.CategoryService;
import pk.shoplus.service.MappingCategoryService;
import pk.shoplus.util.ExceptionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(value = "edsProductMapping")
public class EdsProductMapping implements IDataMapping{

    private final static Logger logger = Logger.getLogger(EdsProductMapping.class);

    public static ProductEDSManagement productEDSManagement = new ProductEDSManagement();

    @Override
    public ProductEDSManagement.ProductOptions mapping(Map<String, Object> bodyDataMap) {
        logger.info("EdsProductMapping,inputParams,bodyDataMap:"+new Gson().toJson(bodyDataMap));
        Connection conn = null;
        try {
            ProductEDSManagement.ProductOptions productOptions = productEDSManagement.getProductOptions();

            JSONObject productMap = com.alibaba.fastjson15.JSON.parseObject(bodyDataMap.get("product").toString());
            String full_update_product = bodyDataMap.get("full_update_product") == null ? "0" : bodyDataMap.get("full_update_product").toString();
            String vendor_id = bodyDataMap.get("vendor_id").toString();
            String oneCategory = productMap.getString("gender");
            String twoCategory = productMap.getString("first_category");
            String threeCategory = productMap.getString("second_category");

            // get category
            String category_id = "";
            try {
                conn = DBConnector.sql2o.open();
                MappingCategoryService mappingCategoryService = new MappingCategoryService(conn);
                List<Map<String, Object>> apiCategoryMap = mappingCategoryService.getMappingCategoryInfoByCondition(vendor_id,oneCategory,twoCategory,threeCategory);
                if (null != apiCategoryMap && apiCategoryMap.size() > 0) {
                    category_id = apiCategoryMap.get(0).get("category_id").toString();
                }
                if(conn != null) {conn.close();}
            } catch (Exception e) {
                logger.info("EdsProductMapping,convertCategory,errorMessage:"+ExceptionUtils.getExceptionDetail(e)+
                        ",productMap:"+new Gson().toJson(productMap));
                e.printStackTrace();
            }

            // get composition
            String technicalInfoString = "";
            try {
                List<TechnicalInfo> technicalInfoList = (List<TechnicalInfo>) productMap.get("technical_info");
                for (int t = 0,len=technicalInfoList.size(); t < len; t++) {
                    Map mapTechnicalInfo = (Map) technicalInfoList.get(t);
                    technicalInfoString += mapTechnicalInfo.get("name").toString() + mapTechnicalInfo.get("percentage").toString();
                }
            } catch (Exception e) {
                logger.info("EdsProductMapping,convertComposition,errorMessage:"+ExceptionUtils.getExceptionDetail(e)+
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
                logger.info("EdsProductMapping,convertSizeFit,errorMessage:"+ExceptionUtils.getExceptionDetail(e)+
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
                logger.info("EdsProductMapping,convertImg,errorMessage:"+ExceptionUtils.getExceptionDetail(e)+
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
                    .setCategoryId(category_id)
                    .setMadeIn(productMap.getString("made_in"))
                    .setComposition(technicalInfoString)
                    .setSizeFit(sizeFitValue)
                    .setWeight(stringListMap.get("weight") == null ? "" : stringListMap.get("weight").toString())
                    .setLength(stringListMap.get("length") == null ? "" : stringListMap.get("length").toString())
                    .setWidth(stringListMap.get("width") == null ? "" : stringListMap.get("width").toString())
                    .setHeigit(stringListMap.get("height") == null ? "" : stringListMap.get("height").toString())
                    .setSalePrice(productMap.getString("retail_price"))
                    .setFullUpdateProductFlag(full_update_product);

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
            }

            if(conn != null){conn.close();}
            logger.info("EdsProductMapping,outParams,productOptions:"+new Gson().toJson(productOptions));
            return productOptions;
        } catch (Exception e) {
            logger.info("EdsProductMapping,errorMessage:"+ ExceptionUtils.getExceptionDetail(e)
            +",bodyDataMap:"+new Gson().toJson(bodyDataMap));
            if(conn != null){conn.close();}
            e.printStackTrace();
        } finally {
            if(conn != null){conn.close();}
        }
        return null;
    }
}
