package pk.shoplus.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson15.JSONArray;
import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;

import pk.shoplus.model.ProductEDSManagement.ProductOptions;
import pk.shoplus.model.ProductEDSManagement.SkuOptions;
import pk.shoplus.model.ProductEDSManagement.VendorOptions;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.mapping.api.IMapping;
import pk.shoplus.util.ExceptionUtils;

import static pk.shoplus.enums.ApiErrorTypeEnum.errorType.Runtime_exception;

/**
 * Atelier 调用createProduct创建商品 底层mapping
 * @author dingyifan
 *
 */
public class ProductAtelierMapping implements IMapping {
	/** logger */
	private static Logger logger = Logger.getLogger(ProductAtelierMapping.class);
	
	/** atelier create product */
	public ProductEDSManagement productEDSManagement = new ProductEDSManagement();
	
	/**
     * return MAP
     * 		status [-1 : FAILURE],[1 : SUCCESS]
     *  	info [message]
     */
	@Override
	public Map<String, Object> handleMappingAndExecute(String mqData){
		logger.info("------------------------------------------start UpdateProductAtelierMapping.handleMappingAndExecuteCreate,mqData:" + mqData);
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try {
			Map<String,Object> convertMap = this.mapping(mqData);
			if(convertMap.get("status").toString().equals(StatusType.SUCCESS+"")) {
				ProductOptions productOptions = (ProductOptions)convertMap.get("productOptions");
				VendorOptions vendorOptions = (VendorOptions)convertMap.get("vendorOptions");

				// 调用Service创建商品
				logger.info("开始调用Atelier商品创建Service by atelier,productOptions:" + new Gson().toJson(productOptions) + " , vendorOptions:" + new Gson().toJson(vendorOptions));
				resultMap = productEDSManagement.createProduct(productOptions, vendorOptions);
				logger.info("结束调用Atelier商品创建Service by atelier,resultMap:" + new Gson().toJson(resultMap)+",productOptions:" + new Gson().toJson(productOptions) + " , vendorOptions:" + new Gson().toJson(vendorOptions));
				resultMap.put("product_code",productOptions.getCode());
				resultMap.put("color_code",productOptions.getColorCode());
				resultMap.put("brand_id",productOptions.getBrandCode());
			} else {
				return convertMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("status",StatusType.FAILURE);
			resultMap.put("error_enum", Runtime_exception);
			resultMap.put("key","exception");
			resultMap.put("value",ExceptionUtils.getExceptionDetail(e));
			resultMap.put("info", "update atelier product - "+ Runtime_exception.getDesc()+" error message : " +ExceptionUtils.getExceptionDetail(e));
        	logger.error("ERROR:"+new Gson().toJson(resultMap));
		}
		logger.info("------------------------------------------end UpdateProductAtelierMapping.handleMappingAndExecuteCreate,resultMap:" + new Gson().toJson(resultMap));
		return resultMap;
	}

	public Map<String,Object> mapping(String mqData) {
		Map<String,Object> resultMap = new HashMap<>();
		resultMap.put("status", StatusType.FAILURE);
		try {
			Map<String,Object> bodyDataMap = JSONObject.parseObject(mqData);
			VendorOptions vendorOptions = new Gson().fromJson(bodyDataMap.get("vendorOptions").toString(), VendorOptions.class);
			ProductOptions productOptions = productEDSManagement.getProductOptions();
			JSONObject jsonObjectData =  JSONObject.parseObject(bodyDataMap.get("Data").toString()) ;
			productOptions.setName(jsonObjectData.getString("product_name"))
					.setCode(jsonObjectData.getString("boutique_id").trim())
					.setSeasonCode(jsonObjectData.getString("season_code"))
					.setBrandCode(jsonObjectData.getString("brand_id").trim())
					.setCarryOver(jsonObjectData.getString("carry_over"))
					.setBrandName(jsonObjectData.getString("brand").trim())
					.setColorCode(jsonObjectData.getString("color_code"))
					.setColorDesc(jsonObjectData.getString("color_description"))
					.setCategoryId(jsonObjectData.getString("category_id"))
					.setDesc(jsonObjectData.getString("product_description"))
					.setComposition(jsonObjectData.getString("composition"))
					.setMadeIn(jsonObjectData.getString("made_in"))
					.setSizeFit(jsonObjectData.getString("size_fit"))
					.setCoverImg(jsonObjectData.getString("cover_img"))
					.setDescImg(jsonObjectData.getString("description_img"))
					.setWeight(jsonObjectData.getString("weight"))
					.setLength(jsonObjectData.getString("length"))
					.setWidth(jsonObjectData.getString("width"))
					.setHeigit(jsonObjectData.getString("height"))
					.setSalePrice(jsonObjectData.getString("sale_price"));
			JSONArray skus = JSONArray.parseArray(jsonObjectData.getString("sku"));
			for (Object it : skus) {
				JSONObject item = (JSONObject) it;
				SkuOptions sku = productEDSManagement.getSkuOptions();
				sku.setBarcodes(item.getString("barcode"));
				sku.setSize(item.getString("size"));
				sku.setStock(item.getString("stock"));
				productOptions.getSkus().add(sku);
			}
			resultMap.put("status",StatusType.SUCCESS);
			resultMap.put("productOptions",productOptions);
			resultMap.put("vendorOptions",vendorOptions);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("status",StatusType.FAILURE);
			resultMap.put("error_enum", Runtime_exception);
			resultMap.put("key","exception");
			resultMap.put("value",ExceptionUtils.getExceptionDetail(e));
			resultMap.put("info", "update atelier product mapping - "+ Runtime_exception.getDesc()+" error message : " + ExceptionUtils.getExceptionDetail(e));
			logger.error("参数转换异常!errorMessage:"+e.getMessage());
		}
		return resultMap;
	}

	
}
