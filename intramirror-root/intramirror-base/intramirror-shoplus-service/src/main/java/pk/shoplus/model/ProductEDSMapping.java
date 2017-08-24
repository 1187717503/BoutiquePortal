package pk.shoplus.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;
import org.sql2o.Connection;

import com.alibaba.fastjson15.JSONArray;
import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;

import pk.shoplus.DBConnector;
import pk.shoplus.model.ProductEDSManagement.ProductOptions;
import pk.shoplus.model.ProductEDSManagement.SkuOptions;
import pk.shoplus.model.ProductEDSManagement.VendorOptions;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.CategoryService;
import pk.shoplus.service.MappingCategoryService;
import pk.shoplus.service.mapping.api.IMapping;
import pk.shoplus.service.product.api.IProductService;
import pk.shoplus.service.product.impl.ProductServiceImpl;
import pk.shoplus.util.ExceptionUtils;

import static pk.shoplus.enums.ApiErrorTypeEnum.errorType.category_not_exist;
import static pk.shoplus.enums.ApiErrorTypeEnum.errorType.Data_can_not_find_mapping;
import static pk.shoplus.enums.ApiErrorTypeEnum.errorType.Runtime_exception;

/**
 * im调用eds接口创建商品 底层mapping
 * @author dingyifan
 *
 */
public class ProductEDSMapping implements IMapping{
	
	private static Logger logger = Logger.getLogger(ProductEDSMapping.class);
	
	public ProductEDSManagement productEDSManagement = new ProductEDSManagement();

	public static final String method_handleMappingAndExecute = "ProductEDSMapping.handleMappingAndExecute()";

	private static IProductService productServie = new ProductServiceImpl();

	@Override
	public Map<String, Object> handleMappingAndExecute(String mqData){
		logger.info("start product eds mapping mqData : "+mqData);
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try {
			// init mqData
			Map<String,Object> mqDataMap = JSONObject.parseObject(mqData);
			JSONObject productMap = com.alibaba.fastjson15.JSON.parseObject(mqDataMap.get("product").toString());
			String storeCode = mqDataMap.get("store_code").toString();
			String full_update_product = mqDataMap.get("full_update_product") == null ? "0" : mqDataMap.get("full_update_product").toString();

			// init 创建商品入参
			ProductOptions productOptions = productEDSManagement.getProductOptions();
			VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
			
			// 设置创建商品入参的值
			productOptions.setName(productMap.getString("item_name"));
			
			productOptions.setCode(productMap.getString("product_id"));
			
			productOptions.setBrandCode(productMap.getString("product_reference"));
			
			productOptions.setSeasonCode(productMap.getString("season_year")+productMap.getString("season_reference"));
			
			productOptions.setBrandName(productMap.getString("brand")); 
			
			productOptions.setColorCode(productMap.getString("color_reference"));
			
			productOptions.setColorDesc(productMap.getString("color"));

			productOptions.setFullUpdateProductFlag(full_update_product);
			
			// category_id for gender,first_category,second_category
			Connection conn = null;
			try{
				conn = DBConnector.sql2o.open();
				MappingCategoryService mappingCategoryService = new MappingCategoryService(conn);
				CategoryService categoryService = new CategoryService(conn);
				Category category = null;

		        Object [] object = new Object[]{storeCode, productMap.get("gender").toString(), productMap.get("first_category").toString(), productMap.get("second_category").toString()};

		        List<Map<String, Object>> apiCategoryMap = null;
		        apiCategoryMap = mappingCategoryService.getMappingCategoryInfoByCondition(mqDataMap.get("vendor_id").toString(),productMap.get("gender").toString(),productMap.get("first_category").toString(),productMap.get("second_category").toString());
		        if (null != apiCategoryMap && apiCategoryMap.size() > 0) {
					category = categoryService.convertMapToCategory(apiCategoryMap.get(0));
		        } else {
					resultMap.put("error_enum", Data_can_not_find_mapping);
		        	resultMap.put("info", "update eds product mapping - "+category_not_exist.getDesc()+"object:" + new Gson().toJson(object));
					resultMap.put("key","category");
					resultMap.put("value",new Gson().toJson(object));
					resultMap.put("product_code",productOptions.getCode());
					resultMap.put("color_code",productOptions.getColorCode());
					resultMap.put("brand_id",productOptions.getBrandCode());
		        	resultMap.put("status", StatusType.FAILURE);
					return resultMap;
		        }
		        productOptions.setCategoryId(category.getCategory_id()+"");
			}catch (Exception e) {
				e.printStackTrace();
				if(conn != null) {conn.close();}
				resultMap.put("status",StatusType.FAILURE);
				resultMap.put("error_enum", Runtime_exception);
				resultMap.put("key","exception");
				resultMap.put("value",ExceptionUtils.getExceptionDetail(e));
				resultMap.put("product_code",productOptions.getCode());
				resultMap.put("color_code",productOptions.getColorCode());
				resultMap.put("brand_id",productOptions.getBrandCode());
				resultMap.put("info", " update eds product - " + Runtime_exception.getDesc()+"error message : " + ExceptionUtils.getExceptionDetail(e));
				return resultMap;
			} finally {
				if(conn != null) {conn.close();}
			}
			
			productOptions.setDesc(productMap.getString("item_description"));
			
			List<TechnicalInfo> technicalInfoList = (List<TechnicalInfo>) productMap.get("technical_info");
            List<String> list = new ArrayList<>();
            for (int t = 0; t < technicalInfoList.size(); t++) {
                Map mapTechnicalInfo = (Map) technicalInfoList.get(t);
                list.add(mapTechnicalInfo.get("name").toString() + mapTechnicalInfo.get("percentage").toString());
            }
            String technicalInfoString = "";
            for (int a = 0; a < list.size(); a++) {
                technicalInfoString += list.get(a);

            }
			productOptions.setComposition(technicalInfoString);
			
			productOptions.setMadeIn(productMap.getString("made_in"));
			
			List<SuiTable> suiTableList = (List<SuiTable>) productMap.get("suitable");
			StringBuffer sizeFitValue = new StringBuffer();
			if(suiTableList != null && suiTableList.size() != 0) {
				for(int i = 0,len=suiTableList.size();i<len;i++) {
					Map map = (Map)suiTableList.get(i);
					sizeFitValue.append(map.get("name").toString()+":"+map.get("value").toString()+",");
				}
			}
			productOptions.setSizeFit(sizeFitValue.toString());

			Map<String, List<String>> stringListMap;
	        try {
	            stringListMap = (Map<String, List<String>>) productMap.getObject("item_images", Map.class);
	            productOptions.setCoverImg(stringListMap.get("full").toString());
				productOptions.setDescImg(stringListMap.get("full").toString());
	        } catch(Exception e) {
	            stringListMap = new HashMap<>();
	        }
	        
			productOptions.setWeight(stringListMap.get("weight") == null ? "" : stringListMap.get("weight").toString());
			
			productOptions.setLength(stringListMap.get("length") == null ? "" : stringListMap.get("length").toString());
			
			productOptions.setWidth(stringListMap.get("width") == null ? "" : stringListMap.get("width").toString());
			
			productOptions.setHeigit(stringListMap.get("height") == null ? "" : stringListMap.get("height").toString());
			
			productOptions.setSalePrice(productMap.getString("retail_price"));
			
			JSONArray skus = JSONArray.parseArray(productMap.getString("variants"));
			if(skus != null) {
				for (Object it : skus) {
		            JSONObject item = (JSONObject) it;
		            SkuOptions sku = productEDSManagement.getSkuOptions();
		            List<String> barcodesList = (List<String>) item.get("barcodes");
		            if(barcodesList != null && barcodesList.size() > 0) {
						sku.setBarcodes(barcodesList.get(0));
					}
		            sku.setSize(item.getString("size"));
		            sku.setStock(item.getString("quantity"));
		            productOptions.getSkus().add(sku);
		        }
			}
			
			// vendorOptions
			vendorOptions.setApiConfigurationId(Long.parseLong(mqDataMap.get("api_configuration_id").toString()));
			vendorOptions.setStoreCode(mqDataMap.get("store_code").toString());
			vendorOptions.setVendorId(Long.parseLong(mqDataMap.get("vendor_id").toString()));

			// 调用Service创建商品
			logger.info("开始调用EDS商品创建Service,productOptions:" + new Gson().toJson(productOptions) + " , vendorOptions:" + new Gson().toJson(vendorOptions));
			Map<String,Object> serviceResultMap = productEDSManagement.createProduct(productOptions, vendorOptions);
			logger.info("结束调用EDS商品创建Service,serviceResultMap:" + new Gson().toJson(serviceResultMap)+",productOptions:" + new Gson().toJson(productOptions) + " , vendorOptions:" + new Gson().toJson(vendorOptions));

			if(serviceResultMap != null && serviceResultMap.get("status").equals(StatusType.PRODUCT_ALREADY_EXISTS)) {
				logger.info("调用eds商品修改Service by eds,productOptions:" + new Gson().toJson(productOptions) + " , vendorOptions:" + new Gson().toJson(vendorOptions));
				serviceResultMap = productServie.updateProduct(productOptions,vendorOptions);
				logger.info("调用eds商品修改Service by eds,serviceResultMap:" + JSON.toJSONString(serviceResultMap)+",productOptions:" + JSON.toJSONString(productOptions) + " , vendorOptions:" + JSON.toJSONString(vendorOptions));
			}

			serviceResultMap.put("product_code",productOptions.getCode());
			serviceResultMap.put("color_code",productOptions.getColorCode());
			serviceResultMap.put("brand_id",productOptions.getBrandCode());

			return serviceResultMap;
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("status",StatusType.FAILURE);
			resultMap.put("error_enum", Runtime_exception);
			resultMap.put("key","exception");
			resultMap.put("value",ExceptionUtils.getExceptionDetail(e));
			resultMap.put("info", "update eds product - " + Runtime_exception.getDesc()+"error message : " + ExceptionUtils.getExceptionDetail(e));
        	logger.error("ERROR:"+new Gson().toJson(resultMap));
		}
		logger.info("start product eds mapping resultMap : " + new Gson().toJson(resultMap));
		return resultMap;
	}
	
}
