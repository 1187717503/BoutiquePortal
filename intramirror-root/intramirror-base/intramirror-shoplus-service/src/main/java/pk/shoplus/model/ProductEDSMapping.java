package pk.shoplus.model;


import com.alibaba.fastjson15.JSON;
import com.alibaba.fastjson15.JSONArray;
import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.DateUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.CategoryService;
import pk.shoplus.service.MappingCategoryService;
import pk.shoplus.service.mapping.api.IMapping;
import pk.shoplus.service.product.api.IProductService;
import pk.shoplus.service.product.impl.ProductServiceImpl;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.MapUtils;

import java.util.*;

import static pk.shoplus.enums.ApiErrorTypeEnum.errorType.Runtime_exception;

/**
 * IM调用EDS接口,createProduct and updateProduct Mapping
 * @author dingyifan
 */
public class ProductEDSMapping implements IMapping {

	private static Logger logger = Logger.getLogger(ProductEDSMapping.class);
	
	public static ProductEDSManagement productEDSManagement = new ProductEDSManagement();

	private static IProductService productServie = new ProductServiceImpl();

	@Override
	public Map<String, Object> handleMappingAndExecute(String mqData,String queueNameEnum){
		logger.info("ProductEDSMappingHandleMappingAndExecute,mqData:"+mqData);
		MapUtils mapUtils = new MapUtils(new HashMap<>());
		try {
			Map<String,Object> mqDataMap = JSONObject.parseObject(mqData);
			JSONObject productMap = JSON.parseObject(mqDataMap.get("product").toString());

			ProductEDSManagement.ProductOptions productOptions = productEDSManagement.getProductOptions();
			ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();

			// get data
			String full_update_product = mqDataMap.get("full_update_product") == null ? "0" : mqDataMap.get("full_update_product").toString();
			String vendor_id = mqDataMap.get("vendor_id").toString();

			// get catgeory
			Connection conn = null;
			try {
				conn = DBConnector.sql2o.open();
				MappingCategoryService mappingCategoryService = new MappingCategoryService(conn);

				String gender = productMap.get("gender").toString();
				String first_category = productMap.get("first_category").toString();
				String second_category = productMap.get("second_category").toString();

				logger.info("ProductEDSMappingHandleMappingAndExecute,getCategory,vendor_id,gender,first_category,second_categoryvendor_id,"+gender+","+first_category+","+second_category);
				List<Map<String, Object>> apiCategoryMap = mappingCategoryService.getMappingCategoryInfoByCondition(vendor_id,gender,first_category,second_category);
				logger.info("ProductEDSMappingHandleMappingAndExecute,getCategory,apiCategoryMap:"+new Gson().toJson(apiCategoryMap));

				if(apiCategoryMap != null && apiCategoryMap.size() >0){
					productOptions.setCategoryId(apiCategoryMap.get(0).get("category_id").toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("ProductEDSMappingHandleMappingAndExecute,errorMessage:"+ExceptionUtils.getExceptionDetail(e));
				if(conn!=null){conn.close();}
			} finally {
				if(conn!=null){conn.close();}
			}

			// get composition
			List<TechnicalInfo> technicalInfoList = (List<TechnicalInfo>) productMap.get("technical_info");
			String technicalInfoString = "";
			for (int t = 0; t < technicalInfoList.size(); t++) {
				Map mapTechnicalInfo = (Map) technicalInfoList.get(t);
				technicalInfoString = technicalInfoString + (mapTechnicalInfo.get("name").toString() + mapTechnicalInfo.get("percentage").toString());
			}

			// get sizeFit
			List<SuiTable> suiTableList = (List<SuiTable>) productMap.get("suitable");
			StringBuffer sizeFitValue = new StringBuffer();
			if(suiTableList != null && suiTableList.size() != 0) {
				for(int i = 0,len=suiTableList.size();i<len;i++) {
					Map map = (Map)suiTableList.get(i);
					sizeFitValue.append(map.get("name").toString()+":"+map.get("value").toString()+",");
				}
			}

			// get images
			Map<String, List<String>> stringListMap;
			try {
				stringListMap = (Map<String, List<String>>) productMap.getObject("item_images", Map.class);
			} catch (Exception e) {
				e.printStackTrace();
				stringListMap = new HashMap<>();
				logger.info("ProductEDSMappingHandleMappingAndExecute,convetImagesError:"+ExceptionUtils.getExceptionDetail(e)+",mqData:"+mqData);
			}

			// get skus
			String variants = productMap.getString("variants");
			JSONArray skus = null;
			if(StringUtils.isNotBlank(variants)) {
				skus = JSONArray.parseArray(productMap.getString("variants"));
			}

			// set data
			productOptions.setName(productMap.getString("item_name"))
			.setCode(productMap.getString("product_id"))
			.setBrandCode(productMap.getString("product_reference"))
			.setSeasonCode(productMap.getString("season_year")+productMap.getString("season_reference"))
			.setBrandName(productMap.getString("brand"))
			.setColorCode(productMap.getString("color_reference"))
			.setColorDesc(productMap.getString("color"))
			.setFullUpdateProductFlag(full_update_product)
			.setDesc(productMap.getString("item_description"))
			.setComposition(technicalInfoString)
			.setMadeIn(productMap.getString("made_in"))
			.setSizeFit(sizeFitValue.toString())
			.setWeight(stringListMap.get("weight") == null ? "" : stringListMap.get("weight").toString())
			.setLength(stringListMap.get("length") == null ? "" : stringListMap.get("length").toString())
			.setWidth(stringListMap.get("width") == null ? "" : stringListMap.get("width").toString())
			.setHeigit(stringListMap.get("height") == null ? "" : stringListMap.get("height").toString())
			.setCoverImg(stringListMap.get("full") == null ? "" : stringListMap.get("full").toString())
			.setDescImg(stringListMap.get("full") == null ? "" : stringListMap.get("full").toString())
			.setSalePrice(productMap.getString("retail_price"));

			String api = "";
			if(skus != null) { // http://nugnes.edstema.it/
				for(Object sku : skus) {
					JSONObject item = (JSONObject) sku;
					ProductEDSManagement.SkuOptions skuOptions = productEDSManagement.getSkuOptions();
					List<String> barcodesList = (List<String>) item.get("barcodes");
					if(barcodesList != null && barcodesList.size() > 0) {
						skuOptions.setBarcodes(barcodesList.get(0));
					}
					skuOptions.setSize(item.getString("size"));
					skuOptions.setStock(item.getString("quantity"));
					productOptions.getSkus().add(skuOptions);
				}
				api = "http://nugnes.edstema.it/";
			} else { // http://baseblu.edstema.it/
				String size = productMap.getString("size");
				String quantity = productMap.getString("quantity");
				ProductEDSManagement.SkuOptions skuOptions = productEDSManagement.getSkuOptions();
				skuOptions.setSize(size);
				skuOptions.setStock(quantity);
				productOptions.getSkus().add(skuOptions);
				api = "http://baseblu.edstema.it/";
			}

			vendorOptions.setApiConfigurationId(Long.parseLong(mqDataMap.get("api_configuration_id").toString()));
			vendorOptions.setStoreCode(mqDataMap.get("store_code").toString());
			vendorOptions.setVendorId(Long.parseLong(vendor_id));

			// 调用Service创建商品
			logger.info("开始调用EDS商品创建Service,productOptions:"+"date:"+ DateUtils.formatDate(new Date())+",api:"+api+"," + new Gson().toJson(productOptions) + " , vendorOptions:" + new Gson().toJson(vendorOptions));
			Map<String,Object> serviceResultMap = productEDSManagement.createProduct(productOptions, vendorOptions);
			logger.info("结束调用EDS商品创建Service,serviceResultMap:"+"date:"+ DateUtils.formatDate(new Date())+",api:"+api+"," + new Gson().toJson(serviceResultMap)+",productOptions:" + new Gson().toJson(productOptions) + " , vendorOptions:" + new Gson().toJson(vendorOptions));

			if(serviceResultMap != null && serviceResultMap.get("status").equals(StatusType.PRODUCT_ALREADY_EXISTS)) {
				logger.info("调用eds商品修改ServiceByEds,productOptions:"+"date:"+ DateUtils.formatDate(new Date())+",api:"+api+"," + new Gson().toJson(productOptions) + " , vendorOptions:" + new Gson().toJson(vendorOptions));
				serviceResultMap = productServie.updateProduct(productOptions,vendorOptions);
				logger.info("调用eds商品修改ServiceByEds,serviceResultMap:"+"date:"+ DateUtils.formatDate(new Date())+",api:"+api+"," + JSON.toJSONString(serviceResultMap)+",productOptions:" + JSON.toJSONString(productOptions) + " , vendorOptions:" + JSON.toJSONString(vendorOptions));
			}

			serviceResultMap.put("product_code",productOptions.getCode());
			serviceResultMap.put("color_code",productOptions.getColorCode());
			serviceResultMap.put("brand_id",productOptions.getBrandCode());
			return serviceResultMap;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("ProductEDSMappingHandleMappingAndExecute,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
			mapUtils.putData("status",StatusType.FAILURE)
					.putData("key","exception")
					.putData("error_enum", Runtime_exception)
					.putData("value",ExceptionUtils.getExceptionDetail(e))
					.putData("info", "update eds product - " + Runtime_exception.getDesc()+"error message : " + ExceptionUtils.getExceptionDetail(e));
		}
		return mapUtils.getMap();
	}
}
