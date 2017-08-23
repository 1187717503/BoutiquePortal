package pk.shoplus.model;


import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;

import pk.shoplus.common.Contants;
import pk.shoplus.enums.ApiErrorTypeEnum;
import pk.shoplus.model.ProductStockEDSManagement.StockOptions;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.mapping.api.IMapping;
import pk.shoplus.service.stock.api.IStoreService;
import pk.shoplus.service.stock.impl.StoreServiceImpl;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.vo.ResultMessage;

import static pk.shoplus.enums.ApiErrorTypeEnum.errorType.Runtime_exception;

/**
 * IM调用EDS接口更新库存 底层mapping
 * @author dingyifan
 *
 */
public class ProductStockEDSMapping implements IMapping{

	private static Logger logger = Logger.getLogger(ProductStockEDSMapping.class);
	
	private ProductStockEDSManagement productStockEDSManagement = new ProductStockEDSManagement();

	private static final String handleMappingAndExecute = " ProductStockEDSMapping.handleMappingAndExecute ";

	@Override
	public Map<String, Object> handleMappingAndExecute(String mqData){
		logger.info(handleMappingAndExecute + " mqData.length : " + mqData == null ? 0 : mqData.length());

		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("status", StatusType.FAILURE);
		
		try {
			// 转换格式
			Map<String,Object> mqDataMap = JSONObject.parseObject(mqData);
			
			// 封装入参
			StockOptions stockOptions = productStockEDSManagement.getStockOptions();
			stockOptions.setProductCode(mqDataMap.get("product_id") == null ? "" : mqDataMap.get("product_id").toString());
			stockOptions.setQuantity(mqDataMap.get("quantity") == null ? "" : mqDataMap.get("quantity").toString());
			stockOptions.setSizeValue(mqDataMap.get("size") == null ? "" : mqDataMap.get("size").toString());

			if(StringUtils.isBlank(stockOptions.getQuantity())) {
				resultMap.put("info","update eds stock mapping - "+ ApiErrorTypeEnum.errorType.stock_is_null.getDesc()+"quantity:null");
				resultMap.put("error_enum", ApiErrorTypeEnum.errorType.Data_can_not_be_null);
				resultMap.put("product_code",stockOptions.getProductCode());
				resultMap.put("key","stock");
				resultMap.put("value","null");
				return resultMap;
			} else {
				IStoreService storeService = new StoreServiceImpl();
				int qty = Integer.parseInt(stockOptions.getQuantity());
				ResultMessage resultMessage = storeService.handleApiStockRule(Contants.STOCK_QTY,qty,stockOptions.getSizeValue(),stockOptions.getProductCode());
				if(resultMessage.getStatus()) {
						SkuStore skuStore = (SkuStore) resultMessage.getData();
					stockOptions.setQuantity(skuStore.getStore().toString());
					stockOptions.setReserved(skuStore.getReserved().toString());
				} else {
					resultMap.put("info",resultMessage.getMsg());
					resultMap.put("sku_size",stockOptions.getSizeValue());
					resultMap.put("error_enum", ApiErrorTypeEnum.errorType.handle_stock_rule_error);
					resultMap.put("product_code",stockOptions.getProductCode());
					return resultMap;
				}
			}
			
			logger.info("开始调用EDS库存更新Service,stockOptions:"+new Gson().toJson(stockOptions));
			Map<String, Object> serviceResultMap = productStockEDSManagement.updateStock(stockOptions);
			logger.info("结束调用EDS库存更新Service,serviceResultMap:"+new Gson().toJson(serviceResultMap)+",stockOptions:"+new Gson().toJson(stockOptions));
			serviceResultMap.put("product_code",stockOptions.getProductCode());
			serviceResultMap.put("sku_size",stockOptions.getSizeValue());
			return serviceResultMap;
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("info", "update eds stock mapping - " + Runtime_exception.getDesc() + "error message : " + ExceptionUtils.getExceptionDetail(e));
			resultMap.put("error_enum", Runtime_exception);
			resultMap.put("key","exception");
			resultMap.put("value",ExceptionUtils.getExceptionDetail(e));
        	logger.error("ERROR:"+new Gson().toJson(resultMap));
		}
		
		logger.info("------------------------------------------end ProductStockEDSMapping.handleMappingAndExecuteUpdate,resultMap:" + new Gson().toJson(resultMap));
		return resultMap;
	}
	
}
