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

import static pk.shoplus.enums.ApiErrorTypeEnum.errorType.handle_stock_rule_error;
import static pk.shoplus.enums.ApiErrorTypeEnum.errorType.Runtime_exception;

/** 
 * 提供给Atelier用的Service,通过此service映射Atelier的数据并并调用更新库存Service
 * @author dingyifan
 * 调用方：处理指定MQ的pending队列的接口
 * 被调用方：更新商品库存的serivce
 */
public class ProductStockAtelierMapping implements IMapping {
	
	/** logger */
	public static Logger logger = Logger.getLogger(ProductStockAtelierMapping.class);
	
	private ProductStockEDSManagement productStockEDSManagement = new ProductStockEDSManagement();
	
	@Override
	public Map<String, Object> handleMappingAndExecute(String mqData){
		logger.info("------------------------------------------start ProductStockAtelierMapping.handleMappingAndExecuteUpdate,mqData:" + mqData);
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try {
			Map<String,Object> bodyDataMap = JSONObject.parseObject(mqData);
			StockOptions stockOptions = productStockEDSManagement.getStockOptions();
			
			String sku = bodyDataMap.get("sku")==null?"":bodyDataMap.get("sku").toString();
			
			if(StringUtils.isNotBlank(sku)) {
				IStoreService storeService = new StoreServiceImpl();
				JSONObject jsonSku = JSONObject.parseObject(sku);
				stockOptions.setProductCode(jsonSku.getString("boutique_id"));
				stockOptions.setSizeValue(jsonSku.getString("size"));
//				stockOptions.setQuantity(jsonSku.getString("stock"));
				String stock = jsonSku.getString("stock");
				if(StringUtils.isBlank(stock)) {
					resultMap.put("status",StatusType.FAILURE);
					resultMap.put("key","stock");
					resultMap.put("value","null");
					resultMap.put("info","update atelier stock mapping - "+ ApiErrorTypeEnum.errorType.stock_is_null.getDesc()+"quantity:null");
					resultMap.put("error_enum", ApiErrorTypeEnum.errorType.Data_can_not_be_null);
					resultMap.put("product_code",stockOptions.getProductCode());
					return resultMap;
				}
				int qty = Integer.parseInt(stock);
				ResultMessage resultMessage = storeService.handleApiStockRule(Contants.STOCK_QTY,qty,stockOptions.getSizeValue(),stockOptions.getProductCode());
				if(resultMessage.getStatus()) {
					SkuStore skuStore = (SkuStore) resultMessage.getData();
					stockOptions.setQuantity(skuStore.getStore().toString());
					stockOptions.setReserved(skuStore.getReserved().toString());
				} else {
					resultMap.put("status", StatusType.FAILURE);
					resultMap.put("info",resultMessage.getMsg());
					resultMap.put("error_enum",handle_stock_rule_error);
					resultMap.put("product_code",stockOptions.getProductCode());
					return resultMap;
				}
			}
			
			logger.info("开始调用Atelier库存更新Service,stockOptions:"+new Gson().toJson(stockOptions));
			Map<String, Object> serviceResultMap = productStockEDSManagement.updateStock(stockOptions);
			logger.info("结束调用Atelier库存更新Service,serviceResultMap:"+new Gson().toJson(serviceResultMap)+",stockOptions:"+new Gson().toJson(stockOptions));
			serviceResultMap.put("product_code",stockOptions.getProductCode());
			serviceResultMap.put("sku_size",stockOptions.getSizeValue());
			return serviceResultMap;
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("status",StatusType.FAILURE);
			resultMap.put("error_enum", Runtime_exception);
			resultMap.put("key","exception");
			resultMap.put("value",ExceptionUtils.getExceptionDetail(e));
			resultMap.put("info", "update atelier stock mapping - "+ Runtime_exception.getDesc()+" error message : " + ExceptionUtils.getExceptionDetail(e));
//			resultMap.put("data", mqData);
			logger.error("ERROR:"+new Gson().toJson(resultMap));
		}
		logger.info("------------------------------------------start ProductStockAtelierMapping.handleMappingAndExecuteUpdate,resultMap:" + new Gson().toJson(resultMap));
		return resultMap;
	}
}
