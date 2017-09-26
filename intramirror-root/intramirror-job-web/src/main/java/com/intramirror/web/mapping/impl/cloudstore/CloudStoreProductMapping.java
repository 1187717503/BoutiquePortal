package com.intramirror.web.mapping.impl.cloudstore;

import java.util.Date;
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
import pk.shoplus.service.MappingCategoryService;
import pk.shoplus.util.ExceptionUtils;

@Service(value = "cloudStoreProductMapping")
public class CloudStoreProductMapping implements IProductMapping {

    private static Logger logger = Logger.getLogger(CloudStoreProductMapping.class);

    public static ProductEDSManagement productEDSManagement = new ProductEDSManagement();
    @Override
    public ProductEDSManagement.ProductOptions mapping(Map<String,Object> bodyDataMap) {
    	logger.info("cloudStoreUpdateByProductMapping,inputParams,bodyDataMap:"+new Gson().toJson(bodyDataMap));
        Connection conn = null;
        ProductEDSManagement.ProductOptions productOptions = productEDSManagement.getProductOptions();
    	try {
    		conn = DBConnector.sql2o.open();
    		String vendor_id = bodyDataMap.get("vendor_id").toString();
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
	            List<Map<String,Object>> categoryMaps = mappingCategoryService.getMappingCategoryInfoByCondition(vendor_id,boutique_category_id);
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
	                .setBrandName(jsonObjectData.getString("brand").trim())
	                .setColorCode(colorCode)
	                .setColorDesc(jsonObjectData.getString("color_en"))
	                .setCategoryId(category_id)
					.setCategory_name(jsonArray.toJSONString())
	                .setDesc(jsonObjectData.getString("desc_en"))
	                .setComposition(jsonObjectData.getString("material_en"))
	                .setCoverImg(jsonObjectData.getString("images"))
	                .setDescImg(jsonObjectData.getString("images"))
	                .setSalePrice(jsonObjectData.getString("stock_price"))
	                .setFullUpdateProductFlag(full_update_product)
					.setLast_check(new Date());
	
	        // handle sku
	        ProductEDSManagement.SkuOptions sku = productEDSManagement.getSkuOptions();
	        sku.setBarcodes(jsonObjectData.getString("barcode"));
	        sku.setSize(jsonObjectData.getString("size"));
	        double douqty = Double.parseDouble(jsonObjectData.getString("qty"));
	        sku.setStock((int)douqty+"");
	        productOptions.getSkus().add(sku);
	
	        if(StringUtils.isBlank(productOptions.getCategoryId())) {
	        	logger.info("cloidStoreUpdateByProductMapping,productOptions.getCategoryId is null,productOptions:"+new Gson().toJson(productOptions));
	        	return productOptions;
	        }
	        if(conn != null){conn.close();}
            logger.info("cloidStoreUpdateByProductMapping,outParams,productOptions:"+new Gson().toJson(productOptions));
	    } catch (Exception e) {
            logger.info("cloidStoreUpdateByProductMapping,errorMessage:"+ ExceptionUtils.getExceptionDetail(e)
            +",bodyDataMap:"+new Gson().toJson(bodyDataMap));
            if(conn != null){conn.close();}
            e.printStackTrace();
        } finally {
            if(conn != null){conn.close();}
        }
	    return productOptions;
    }
}
