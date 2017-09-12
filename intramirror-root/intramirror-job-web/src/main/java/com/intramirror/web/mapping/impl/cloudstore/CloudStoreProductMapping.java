package com.intramirror.web.mapping.impl.cloudstore;

import com.alibaba.fastjson15.JSONArray;
import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.web.mapping.api.IDataMapping;
import com.intramirror.web.mq.producer.ProducerUtils;
import com.intramirror.web.mq.vo.Region;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.service.MappingCategoryService;
import pk.shoplus.util.ExceptionUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/9/11.
 */
@Service(value = "cloudStoreProductMapping")
public class CloudStoreProductMapping implements IDataMapping {

    private static Logger logger = Logger.getLogger(CloudStoreProductMapping.class);

    @Autowired
    private ProducerUtils producerUtils;

    @Autowired
    private Region region;

    @Override
    public ProductEDSManagement.ProductOptions mapping(Map<String,Object> bodyDataMap) {
        Connection conn = null;
        try {
            conn = DBConnector.sql2o.open();
            ProductEDSManagement productEDSManagement = new ProductEDSManagement();

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
            ProductEDSManagement.ProductOptions productOptions = productEDSManagement.getProductOptions();
            productOptions.setName(jsonObjectData.getString("title_en"))
                    .setCode(jsonObjectData.getString("mnf_code").trim())
                    .setSeasonCode(jsonObjectData.getString("season"))
                    .setBrandCode(brandID)
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

            if(conn != null) {conn.close();}
            return productOptions;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("CloudStoreProductMappingMapping,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
            if(conn != null) {conn.close();}
        } finally {
            if(conn != null) {conn.close();}
        }
        return null;
    }
}
