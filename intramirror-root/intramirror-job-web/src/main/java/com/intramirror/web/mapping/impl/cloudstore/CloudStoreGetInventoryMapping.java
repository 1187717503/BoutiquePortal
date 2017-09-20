package com.intramirror.web.mapping.impl.cloudstore;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.sql2o.Connection;

import com.alibaba.fastjson15.JSONArray;
import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.web.mapping.api.IProductMapping;

import pk.shoplus.DBConnector;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.model.ProductEDSManagement.ProductOptions;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.MappingCategoryService;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.MapUtils;

/**
 * Created by dingyifan on 2017/6/5.
 * IM调用CloudStore的getInventory接口处理商品信息
 */
@Service(value = "cloudStoreGetInventoryMapping")
public class CloudStoreGetInventoryMapping implements IProductMapping {

    private static Logger logger = Logger.getLogger(CloudStoreGetInventoryMapping.class);

    private ProductEDSManagement productEDSManagement = new ProductEDSManagement();

    @Override
    public ProductEDSManagement.ProductOptions mapping(Map<String,Object> bodyDataMap) {
        logger.info("CloudStoreGetInventoryMapping.handleMappingAndExecute();");
        ProductEDSManagement.ProductOptions productOptions = productEDSManagement.getProductOptions();
        try {

        	logger.info("CloudStoreGetInventoryMapping param :------->>"+new Gson().toJson(bodyDataMap));
            Map<String,Object> convertMap = this.mapping(new Gson().toJson(bodyDataMap));
            if(convertMap.get("status").toString().equals(StatusType.SUCCESS+"")) {
            	productOptions = (ProductOptions) convertMap.get("productOptions");
                return  productOptions;
            }

        } catch (Exception e) {
        	 logger.info("cloidStoreUpdateByProductMapping,errorMessage:"+ ExceptionUtils.getExceptionDetail(e)
             +",bodyDataMap:"+new Gson().toJson(bodyDataMap));
             e.printStackTrace();
         }
 	    return productOptions;
    }

    public Map<String,Object> mapping(String mqData) {
    	MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());
    	ProductEDSManagement.ProductOptions productOptions = productEDSManagement.getProductOptions();
        Connection conn = null;
        try {
        	conn = DBConnector.sql2o.open();

            Map<String,Object> bodyDataMap = JSONObject.parseObject(mqData);

            ProductEDSManagement.VendorOptions vendorOptions = new Gson().fromJson(bodyDataMap.get("vendorOption").toString(), ProductEDSManagement.VendorOptions.class);
            JSONObject jsonObjectData =  JSONObject.parseObject(bodyDataMap.get("responseBody").toString()) ;
            String full_update_product = bodyDataMap.get("full_update_product") == null ? "" : bodyDataMap.get("full_update_product").toString();
            // get brandID,colorCode
            String strSKU = jsonObjectData.get("sku").toString();
            String brandID = strSKU.substring(0,strSKU.indexOf("_"));
            String colorCode = strSKU.substring(strSKU.indexOf("_")+1,strSKU.indexOf("-"));

            // get boutique_category_id
            JSONArray jsonArray = jsonObjectData.getJSONArray("cat_ids");
            Iterator<Object> it = jsonArray.iterator();
            String boutique_category_id = "";
            String category_id = "";
            while (it.hasNext()) {
                JSONObject jsonObject = (JSONObject) it.next();
                boutique_category_id = jsonObject.getString("$id");

                MappingCategoryService mappingCategoryService = new MappingCategoryService(conn);
                List<Map<String,Object>> categoryMaps = mappingCategoryService.getMappingCategoryInfoByCondition(vendorOptions.getVendorId().toString(),boutique_category_id);
                if(categoryMaps != null && categoryMaps.size() >0){
                    category_id = categoryMaps.get(0).get("category_id").toString();
                    break;
                }
            }

            // handle product
            productOptions.setName(jsonObjectData.getString("title_en"))
                    .setCode(jsonObjectData.getString("mnf_code").trim())
                    .setSeasonCode(jsonObjectData.getString("season"))
                    .setBrandCode(brandID)
                    .setCategory_name(jsonArray.toJSONString())
                    .setBrandName(jsonObjectData.getString("brand").trim())
                    .setColorCode(colorCode)
                    .setColorDesc(jsonObjectData.getString("color_en"))
                    .setCategoryId(category_id)
                    .setDesc(jsonObjectData.getString("desc_en"))
                    .setComposition(jsonObjectData.getString("material_en"))
                    .setCoverImg(jsonObjectData.getString("images"))
                    .setDescImg(jsonObjectData.getString("images"))
                    .setSalePrice(jsonObjectData.getString("stock_price"))
                    .setFullUpdateProductFlag(full_update_product);

            // handle sku
            ProductEDSManagement.SkuOptions sku = productEDSManagement.getSkuOptions();
            sku.setBarcodes(jsonObjectData.getString("barcode"));
            sku.setSize(jsonObjectData.getString("size"));
            double douqty = Double.parseDouble(jsonObjectData.getString("qty"));
            sku.setStock((int)douqty+"");
            productOptions.getSkus().add(sku);

            if(StringUtils.isBlank(productOptions.getCategoryId())) {
                return mapUtils.putData("status",StatusType.FAILURE)
                        .putData("productOptions", productOptions).getMap();
            }

            logger.info("status : "+StatusType.SUCCESS + "productOptions :"+new Gson().toJson(productOptions) + "vendorOptions :"+new Gson().toJson(vendorOptions));
        } catch (Exception e) {
        	if(conn != null) {conn.close();}
            e.printStackTrace();
            logger.error("CloudStoreGetInventoryMapping.mapping() error message : " + ExceptionUtils.getExceptionDetail(e));
        } finally {
            if(conn != null) {conn.close();}
        }
        return mapUtils.putData("status",StatusType.SUCCESS).putData("productOptions", productOptions).getMap();
    }
}
