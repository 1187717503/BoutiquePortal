package pk.shoplus.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;

import pk.shoplus.DBConnector;
import pk.shoplus.common.Contants;
import pk.shoplus.parameter.EnabledType;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.ApiParameterService;
import pk.shoplus.service.ProductPropertyKeyService;
import pk.shoplus.service.ProductPropertyValueService;
import pk.shoplus.service.ProductService;
import pk.shoplus.service.SkuPropertyService;
import pk.shoplus.service.SkuService;
import pk.shoplus.service.SkuStoreService;
import pk.shoplus.service.price.api.IPriceService;
import pk.shoplus.service.price.impl.PriceServiceImpl;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.MapUtils;
import pk.shoplus.vo.ResultMessage;

import static pk.shoplus.enums.ApiErrorTypeEnum.errorType.Runtime_exception;
import static pk.shoplus.enums.ApiErrorTypeEnum.errorType.updateStock_params_is_error;

/**
 * 更新库存接口
 * @author dingyifan
 *
 */
public class ProductStockEDSManagement {

	private static Logger logger = Logger.getLogger(ProductStockEDSManagement.class);

	private final String method_updateStock = "ProductStockEDSManagement.updateStock()";

	public Map<String, Object> updateStock(StockOptions stockOptions){
	    logger.info("start updateStock service stockOptions : " + new Gson().toJson(stockOptions));

        MapUtils mapUtils = new MapUtils(new HashMap<>());
        Connection connection = null;
		try{
            connection = DBConnector.sql2o.beginTransaction();

			SkuPropertyService skuPropertyService = new SkuPropertyService(connection);
			SkuStoreService skuStoreService = new SkuStoreService(connection);

			ResultMessage resultMessage = this.validateParam(stockOptions,connection);
			if(resultMessage.getStatus()) {

			    // get product
			    Product product = (Product) resultMessage.getData();

                List<Map<String, Object>> skuStoreIdList = skuPropertyService
                        .getSkuPropertyListWithSizeAndProductCode(stockOptions.getSizeValue(), stockOptions.getProductCode());

                Long reserved = 0L;
                if(org.apache.commons.lang.StringUtils.isNotBlank(stockOptions.getReserved())) {
                    reserved = Long.valueOf(stockOptions.getReserved());
                }

                if(skuStoreIdList != null && skuStoreIdList.size() > 0) {
                    // update sku_store
                    Object skuStoreIdObj = skuStoreIdList.get(0).get("sku_store_id");
                    if (skuStoreIdObj != null) {
                        Long skuStoreId = (Long) skuStoreIdObj;
                        SkuStore skuStore = new SkuStore();
                        skuStore.sku_store_id = skuStoreId;
                        skuStore.store = Long.valueOf(stockOptions.getQuantity());
                        if(StringUtils.isBlank(stockOptions.getType()) || !stockOptions.getType().equals(Contants.EVENTS_TYPE_1+"")) {
                            skuStore.reserved = reserved;
                        }
                        logger.info("updateSkuStore skuStore : " + new Gson().toJson(skuStore));
                        skuStoreService.updateSkuStore(skuStore);
                    }
                } else {

                    // create sku_store
                    this.createSkuInfo(product, connection, stockOptions.getQuantity(),reserved, stockOptions.getSizeValue(), skuPropertyService, skuStoreService);
                }
                mapUtils.putData("status",StatusType.SUCCESS).putData("info","SUCCESS");
            } else {
                mapUtils.putData("status",StatusType.FAILURE)
                        .putData("info",resultMessage.getMsg())
                        .putData("sku_size",stockOptions.getSizeValue())
                        .putData("product_code",stockOptions.getProductCode())
                        .putData("key","info")
                        .putData("value",resultMessage.getMsg())
                        .putData("error_enum",updateStock_params_is_error);
            }
            connection.commit();
		}catch (Exception e) {
            if(connection != null) {connection.rollback();connection.close();}
			e.printStackTrace();
            mapUtils.putData("status",StatusType.FAILURE)
                    .putData("info","update stock - " + Runtime_exception.getDesc()+"error message : " + ExceptionUtils.getExceptionDetail(e))
                    .putData("key","exception")
                    .putData("value",ExceptionUtils.getExceptionDetail(e))
            .putData("error_enum", Runtime_exception);
		} finally {
           if(connection != null) {connection.close();}
        }
		return mapUtils.getMap();
	}
	
	/**
     * 创建sku相关信息
     * @param product
     * @param connection
     * @param quantity
     * @param size
     * @param skuPropertyService
     * @param skuStoreService
     * @throws Exception
     */
    public void createSkuInfo (Product product, Connection connection, String quantity, Long reserved, String size, SkuPropertyService skuPropertyService, SkuStoreService skuStoreService) throws Exception {
        if (null != product) {
            SkuService skuService = new SkuService(connection);
            Sku sku = new Sku();
            sku.product_id = product.getProduct_id();
            sku.sku_code = "#";
            sku.name = product.getName();
            sku.coverpic = product.cover_img;
            sku.introduction = product.getDescription();



            Map condition = new HashMap<>();
            condition.put("product_id", product.getProduct_id());
            condition.put("enabled", 1);
            List<Sku> list = skuService.getSkuListByCondition(condition);
            if (null != list && list.size() > 0) {
                Sku skuTemp = list.get(0);
                IPriceService iPriceService = new PriceServiceImpl();
                Sku sku1 = iPriceService.getPriceByRule(product,product.getVendor_id(),skuTemp.getPrice(),connection);
                sku.im_price = sku1.getIm_price();
                sku.in_price = sku1.getIn_price();
                sku.price = skuTemp.getPrice();
            }
            sku.created_at = new Date();
            sku.updated_at = new Date();
            sku.enabled = EnabledType.USED;
            Sku newSku = skuService.createSku(sku);

            //创建sku_store
            SkuStore skuStore = new SkuStore();
            skuStore.product_id = product.getProduct_id();
            skuStore.sku_id = newSku.sku_id;
            skuStore.store  = Long.valueOf(quantity);
            skuStore.reserved = reserved;
            skuStore.remind = 0;
            skuStore.ordered = 0;
            skuStore.confirm = 0;
            skuStore.ship = 0;
            skuStore.finished = 0;
            skuStore.clear = 0;
            skuStore.changed = 0;
            skuStore.returned = 0;
            skuStore.created_at = new Date();
            skuStore.updated_at = new Date();
            skuStore.enabled = EnabledType.USED;
            skuStoreService.createSkuStore(skuStore);

            //创建product_sku_property_key
            ProductPropertyKeyService productPropertyKeyService = new ProductPropertyKeyService(connection);
            ProductSkuPropertyKey pspk = null;
            Map<String, Object> param = new HashMap<>();
            param.put("product_id", product.getProduct_id());
            param.put("name", "Size");
            param.put("enabled", true);
            List<ProductSkuPropertyKey> keyList = productPropertyKeyService.getProductPropertyKeyListByCondition(param);
            if (null != keyList && keyList.size() > 0) {
                pspk = keyList.get(0);
            }

            //创建product_sku_property_value
            ProductPropertyValueService productPropertyValueService = new ProductPropertyValueService(connection);
            ProductSkuPropertyValue productSkuPropertyValue = new ProductSkuPropertyValue();
            productSkuPropertyValue.product_sku_property_key_id = pspk.getProduct_sku_property_key_id();
            productSkuPropertyValue.value = size;
            productSkuPropertyValue.remark = "";
            productSkuPropertyValue.created_at = new Date();
            productSkuPropertyValue.updated_at = new Date();
            productSkuPropertyValue.enabled = EnabledType.USED;
            ProductSkuPropertyValue pspv = productPropertyValueService.createProductPropertyValue(productSkuPropertyValue);

            //创建sku_property
            SkuProperty skuProperty = new SkuProperty();
            skuProperty.sku_id = newSku.getSku_id();
            skuProperty.product_sku_property_key_id = pspk.getProduct_sku_property_key_id();
            skuProperty.product_sku_property_value_id = pspv.getProduct_sku_property_value_id();
            skuProperty.name = product.getName();
            skuProperty.remark = "";
            skuProperty.created_at = new Date();
            skuProperty.updated_at = new Date();
            skuProperty.enabled = EnabledType.USED;
            skuPropertyService.createSkuProperty(skuProperty);

        }
    }
	
	/**
     * 检查校验参数合法性
     *
     * @return
     */
    public ResultMessage validateParam(StockOptions stockOptions, Connection conn) throws Exception {
        // init resultMap
        ResultMessage resultMessage = new ResultMessage();

        // get params
        String productCode = stockOptions.getProductCode();
        String sizeValue = stockOptions.getSizeValue();
        String quantity = stockOptions.getQuantity();
//        String reserved = stockOptions.getReserved();
        Product product = null;

        // checked productCode
        if(org.apache.commons.lang.StringUtils.isNotBlank(productCode)) {

            ProductService productService = new ProductService(conn);
            Map<String, Object> condition = new HashMap<>();
            condition.put("product_code", productCode);
            condition.put("enabled", EnabledType.USED);
            product = productService.getProductByCondition(condition, null);
            if(product == null){
                return resultMessage.sStatus(false).sMsg("update stock - 找不到这个商品 。");
            }
        } else {
            return resultMessage.sStatus(false).sMsg("update stock - productCode is null 。");
        }

        // checked sizeValue
        if(org.apache.commons.lang.StringUtils.isBlank(sizeValue))
            return resultMessage.sStatus(false).sMsg("update stock - sizeValue is null 。");

        // checked quantity
        if(org.apache.commons.lang.StringUtils.isBlank(quantity))
            return resultMessage.sStatus(false).sMsg("update stock - quantity is null 。");

        return resultMessage.sStatus(true).sMsg("SUCCESS").sData(product);
    }

    /**
     * 获取请求URL
     * @param apiEndpoint
     * @param conn
     * @return
     * @throws Exception
     */
    public Map<String, Object> getUrl (Map<String, Object> apiEndpoint, Connection conn) throws Exception {
        ApiParameterService apiParameterService = new ApiParameterService(conn);
        StringBuffer urlBuffer = new StringBuffer();
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> apiParameterList = null;
        String apiEndPointId = "";

        if (null != apiEndpoint) {
            urlBuffer.append(apiEndpoint.get("url"));
            apiEndPointId = apiEndpoint.get("api_end_point_id").toString();
        }

        try {
            apiParameterList = apiParameterService.getapiParameterByCondition(apiEndPointId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String limit = "";
        String offset = "";
        if(null != apiParameterList && apiParameterList.size() > 0) {
            String paramKey = "";
            String paramValue = "";
            urlBuffer.append("?");

            for (Map<String, Object> apiParameter : apiParameterList) {
                paramKey = apiParameter.get("param_key").toString();
                paramValue = apiParameter.get("param_value").toString();

                if ("limit".equals(paramKey)) {
                    limit = paramValue;
                }
                if ("offset".equals(paramKey)) {
                    offset = paramValue;
                }

                urlBuffer.append(paramKey+"="+paramValue+"&");
            }
        }
        String url =  urlBuffer.toString();

        map.put("url", url);
        map.put("storeCode", apiEndpoint.get("store_code"));
        map.put("limit", limit);
        map.put("offset", offset);

        return map;
    }
	
	// 转换参数
	public Map<String,Object> convertParam(StockOptions stockOptions){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("product_id", stockOptions.getProductCode());
		map.put("size", stockOptions.getSizeValue());
		map.put("quantity", stockOptions.getQuantity());
		return map;
	}
	
	public StockOptions getStockOptions(){
		return new StockOptions();
	}
	
	public class StockOptions{
		public String productCode;
		public String sizeValue;
		public String quantity;
		public String reserved;
		public String type;
		
		public String getProductCode() {
			return productCode;
		}
		public void setProductCode(String productCode) {
			this.productCode = productCode;
		}
		public String getSizeValue() {
			return sizeValue;
		}
		public void setSizeValue(String sizeValue) {
			this.sizeValue = sizeValue;
		}
		public String getQuantity() {
			return quantity;
		}
		public void setQuantity(String quantity) {
			this.quantity = quantity;
		}

        public String getReserved() {
            return reserved;
        }

        public void setReserved(String reserved) {
            this.reserved = reserved;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
