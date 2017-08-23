package pk.shoplus.service.mapping.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson15.JSONArray;
import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.model.ApiCategoryMap;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.parameter.EnabledType;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.ApiCategoryMapService;
import pk.shoplus.service.mapping.api.IMapping;
import pk.shoplus.service.product.api.IProductService;
import pk.shoplus.service.product.impl.ProductServiceImpl;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.MapUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static pk.shoplus.enums.ApiErrorTypeEnum.errorType.Data_can_not_find_mapping;
import static pk.shoplus.enums.ApiErrorTypeEnum.errorType.Runtime_exception;

/**
 * Created by dingyifan on 2017/6/5.
 * IM调用CloudStore的getInventory接口处理商品信息
 */
public class CloudStoreGetInventoryMapping implements IMapping{

    private static Logger logger = Logger.getLogger(CloudStoreGetInventoryMapping.class);

    private ProductEDSManagement productEDSManagement = new ProductEDSManagement();

    private IProductService iProductService = new ProductServiceImpl();

    @Override
    public Map<String, Object> handleMappingAndExecute(String mqData) {
        logger.info("CloudStoreGetInventoryMapping.handleMappingAndExecute();");
        MapUtils mapUtils = new MapUtils(new HashMap<>());
        try {

            Map<String,Object> convertMap = this.mapping(mqData);
            if(convertMap.get("status").toString().equals(StatusType.SUCCESS+"")) {
                ProductEDSManagement.ProductOptions productOptions = (ProductEDSManagement.ProductOptions)convertMap.get("productOptions");
                ProductEDSManagement.VendorOptions vendorOptions = (ProductEDSManagement.VendorOptions)convertMap.get("vendorOptions");

                logger.info("CloudStore开始调用商品创建Service by atelier,productOptions:" + new Gson().toJson(productOptions) + " , vendorOptions:" + new Gson().toJson(vendorOptions));
                Map<String,Object> serviceProductMap = productEDSManagement.createProduct(productOptions,vendorOptions);
                logger.info("CloudStore结束调用商品创建Service by atelier,serviceProductMap:" + new Gson().toJson(serviceProductMap)+",productOptions:" + new Gson().toJson(productOptions) + " , vendorOptions:" + new Gson().toJson(vendorOptions));

                if(serviceProductMap != null && serviceProductMap.get("status").equals(StatusType.PRODUCT_ALREADY_EXISTS)) {

                    // 添加sku信息
                    logger.info("CloudStore开始调用商品修改Service by atelier,productOptions:" + new Gson().toJson(productOptions) + " , vendorOptions:" + new Gson().toJson(vendorOptions));
                    serviceProductMap = iProductService.updateProduct(productOptions,vendorOptions);
                    logger.info("CloudStore结束调用商品修改Service by atelier,serviceProductMap:" + JSON.toJSONString(serviceProductMap)+",productOptions:" + new Gson().toJson(productOptions) + " , vendorOptions:" + new Gson().toJson(vendorOptions));
                }

                serviceProductMap.put("product_code",productOptions.getCode());
                serviceProductMap.put("color_code",productOptions.getColorCode());
                serviceProductMap.put("brand_id",productOptions.getBrandCode());
                return  serviceProductMap;
            } else {
                return convertMap;
            }

        } catch (Exception e) {
            e.printStackTrace();
            mapUtils.putData("key","exception");
            mapUtils.putData("value",ExceptionUtils.getExceptionDetail(e));
            mapUtils.putData("error_enum", Runtime_exception);
            mapUtils.putData("status",StatusType.FAILURE).putData("info","CloudStoreGetInventoryMapping.handleMappingAndExecuteCreate() error message : " + ExceptionUtils.getExceptionDetail(e));
        }
        logger.info("end CloudStoreGetInventoryMapping.handleMappingAndExecuteCreate(); result : " + mapUtils.toJson());
        return mapUtils.getMap();
    }

    public Map<String,Object> mapping(String mqData) {
        MapUtils mapUtils = new MapUtils(new HashMap<>());
        Connection conn = null;
        try {
        	conn = DBConnector.sql2o.beginTransaction();

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
            ApiCategoryMap apiCategoryMap = new ApiCategoryMap();
            while (it.hasNext()) {
                JSONObject jsonObject = (JSONObject) it.next();
                boutique_category_id = jsonObject.getString("$id");

                // get category_id
                ApiCategoryMapService apiCategoryMapService = new ApiCategoryMapService(conn);
                Map<String,Object> apiCatgeoryMapConditions = new HashMap<>();
                apiCatgeoryMapConditions.put("api_configuration_id",vendorOptions.getApiConfigurationId());
                apiCatgeoryMapConditions.put("boutique_category_id",boutique_category_id);
                apiCatgeoryMapConditions.put("enabled", EnabledType.USED);
                List<ApiCategoryMap> apiCategoryMapList = apiCategoryMapService.queryByConditions(apiCatgeoryMapConditions);
                if(apiCategoryMapList != null && apiCategoryMapList.size() > 0) {
                    apiCategoryMap = apiCategoryMapList.get(0);
                }

                if(apiCategoryMap.getCategory_id() != null) {
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
                    .setCategoryId(apiCategoryMap.getCategory_id() == null?"":apiCategoryMap.getCategory_id().toString())
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
                        .putData("error_enum", Data_can_not_find_mapping)
                        .putData("info","CloudStoreGetInventoryMapping.mapping category:"+jsonArray.toJSONString())
                        .putData("key","category")
                        .putData("product_code",productOptions.getCode())
                        .putData("brand_id",productOptions.getBrandCode())
                        .putData("color_code",productOptions.getColorCode())
                        .putData("value",jsonArray.toJSONString()).getMap();
            }

            mapUtils.putData("status",StatusType.SUCCESS).putData("productOptions",productOptions).putData("vendorOptions",vendorOptions);
            conn.commit();
        } catch (Exception e) {
        	if(conn != null) {conn.rollback();conn.close();}
            e.printStackTrace();
            mapUtils.putData("key","exception");
            mapUtils.putData("value",ExceptionUtils.getExceptionDetail(e));
            mapUtils.putData("error_enum", Runtime_exception);
            mapUtils.putData("status",StatusType.FAILURE).putData("info","CloudStoreGetInventoryMapping.mapping() error message : " + ExceptionUtils.getExceptionDetail(e));
        } finally {
            if(conn != null) {conn.close();}
        }
        return mapUtils.getMap();
    }
}
