package pk.shoplus.model;

import java.math.BigDecimal;
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
import pk.shoplus.service.product.impl.ProductServiceImpl;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.MapUtils;
import pk.shoplus.vo.ResultMessage;

import static pk.shoplus.enums.ApiErrorTypeEnum.errorType.*;

/**
 * 更新库存接口
 * @author dingyifan
 *
 */
public class ProductStockEDSManagement {

	private static Logger logger = Logger.getLogger(ProductStockEDSManagement.class);

    private IPriceService priceService = new PriceServiceImpl();


    public Map<String, Object> updateStock(StockOptions stockOptions){
        logger.info("ProductStockEDSManagementUpdateStock,inputParams,stockOptions:"+new Gson().toJson(stockOptions));
        MapUtils mapUtils = new MapUtils(new HashMap<>());
        Connection connection = null;
		try{
            connection = DBConnector.sql2o.beginTransaction();

			SkuPropertyService skuPropertyService = new SkuPropertyService(connection);
			SkuStoreService skuStoreService = new SkuStoreService(connection);

			logger.info("ProductStockEDSManagementUpdateStock,validateParam,stockOptions:"+new Gson().toJson(stockOptions));
			ResultMessage resultMessage = this.validateParam(stockOptions,connection);
            logger.info("ProductStockEDSManagementUpdateStock,validateParam,resultMessage:"+new Gson().toJson(resultMessage));
            if(resultMessage.getStatus()) {

			    // get product
			    Product product = (Product) resultMessage.getData();

			    logger.info("ProductStockEDSManagementUpdateStock,getSkuPropertyListWithSizeAndProductCode,skuOptions:"+new Gson().toJson(stockOptions));
                List<Map<String, Object>> skuStoreIdList = skuPropertyService
                        .getSkuPropertyListWithSizeAndProductCode(stockOptions.getSizeValue(), stockOptions.getProductCode(),stockOptions.getVendor_id());
                logger.info("ProductStockEDSManagementUpdateStock,getSkuPropertyListWithSizeAndProductCode,skuStoreIdList:"+new Gson().toJson(skuStoreIdList));

                Long reserved = 0L;
                if(org.apache.commons.lang.StringUtils.isNotBlank(stockOptions.getReserved())) {
                    reserved = Long.valueOf(stockOptions.getReserved());
                }

                if(skuStoreIdList != null && skuStoreIdList.size() > 0) {
                    // update sku_store
                    Object skuStoreIdObj = skuStoreIdList.get(0).get("sku_store_id");
                    Long skuStoreId = (Long) skuStoreIdObj;
                    SkuStore skuStore = new SkuStore();
                    skuStore.sku_store_id = skuStoreId;
                    skuStore.store = Long.valueOf(stockOptions.getQuantity());
                    skuStore.confirmed = Long.valueOf(stockOptions.getConfirmed());
                    if(StringUtils.isBlank(stockOptions.getType()) || !stockOptions.getType().equals(Contants.EVENTS_TYPE_1+"")) {
                        skuStore.reserved = reserved;
                    }
                    logger.info("ProductStockEDSManagementUpdateStock,updateSkuStore,skuStore:" + new Gson().toJson(skuStore));
                    skuStoreService.updateSkuStore(skuStore);
                } else {

                    // create sku_store
                    this.createSkuInfo(product, connection, stockOptions.getQuantity(),reserved, stockOptions.getSizeValue(), skuPropertyService, skuStoreService);
                }

                // update price
                if(StringUtils.isNotBlank(stockOptions.getPrice())) {
                    logger.info("ProductStockEDSManagementUpdateStock,startUpdatePricem,stockOptions:"+new Gson().toJson(stockOptions));
                    BigDecimal price = BigDecimal.valueOf(Double.parseDouble(stockOptions.getPrice()));

                    Map<String,Object> skuConditions = new HashMap<>();
                    skuConditions.put("product_id",product.product_id);
                    skuConditions.put("enabled",EnabledType.USED);
                    SkuService skuService = new SkuService(connection);
                    List<Sku> skuList = skuService.getSkuListByCondition(skuConditions);

                    BigDecimal oldPrice = skuList.get(0).getPrice();
                    boolean flag = ProductServiceImpl.isPrice(oldPrice,price);
                    if(flag) {
                        Sku skuPrice = priceService.getPriceByRule(product,Long.parseLong(stockOptions.getVendor_id()),price,connection);
                        BigDecimal inPrice = skuPrice.getIn_price();
                        for(Sku sku : skuList){
                            logger.info("ProductStockEDSManagementUpdateStock,updateSkuPrice,originSku:"+new Gson().toJson(sku));
                            sku.price = price;
                            sku.in_price = inPrice;
                            sku.im_price = skuPrice.getIm_price();
                            sku.updated_at = new Date();
                            skuService.updateSku(sku);
                            logger.info("ProductStockEDSManagementUpdateStock,updateSkuPrice,updateSku:"+new Gson().toJson(sku));
                        }
                    } else {
                         mapUtils.putData("status",StatusType.FAILURE)
                                .putData("info","update stock - " + error_price_out_of_range.getDesc() + " price:" + price)
                                .putData("sku_size",stockOptions.getSizeValue())
                                .putData("product_code",stockOptions.getProductCode())
                                .putData("key","retail_price")
                                .putData("value",price)
                                .putData("error_enum", error_price_out_of_range);
                        logger.info("ProductStockEDSManagementUpdateStock,outputParams,mapUtils:"+new Gson().toJson(mapUtils));
                        if(connection!=null) {connection.commit();connection.close();}
                        return mapUtils.getMap();
                    }
                }
                mapUtils.putData("status",StatusType.SUCCESS).putData("info","SUCCESS");
            } else {
                mapUtils.putData("status",StatusType.FAILURE)
                        .putData("info",resultMessage.getMsg())
                        .putData("sku_size",stockOptions.getSizeValue())
                        .putData("product_code",stockOptions.getProductCode())
                        .putData("key","info")
                        .putData("value",resultMessage.getMsg())
                        .putData("error_enum",resultMessage.getData());
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
            logger.info("ProductStockEDSManagementUpdateStock,errorMessage:"+ExceptionUtils.getExceptionDetail(e));
        } finally {
           if(connection != null) {connection.close();}
        }
        logger.info("ProductStockEDSManagementUpdateStock,outputParams,mapUtils:"+new Gson().toJson(mapUtils));
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
            logger.info("ProductStockEDSManagementCreateSkuInfo,createSku,sku:"+new Gson().toJson(sku));

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
            logger.info("ProductStockEDSManagementCreateSkuInfo,createSkuStore,skuStore:"+new Gson().toJson(skuStore));

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
            logger.info("ProductStockEDSManagementCreateSkuInfo,createProductPropertyValue,pspv:"+new Gson().toJson(pspv));

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
            logger.info("ProductStockEDSManagementCreateSkuInfo,createSkuProperty,skuProperty:"+new Gson().toJson(skuProperty));

        }
    }
	
	/**
     * 检查校验参数合法性
     *
     * @return
     */
    public ResultMessage validateParam(StockOptions stockOptions, Connection conn) throws Exception {
        logger.info("ProductStockEDSManagementValidateParam,inputParams,stockOptions:"+new Gson().toJson(stockOptions));
        ResultMessage resultMessage = new ResultMessage();
        resultMessage.setStatus(true);
        String productCode = stockOptions.getProductCode();
        String sizeValue = stockOptions.getSizeValue();
        String quantity = stockOptions.getQuantity();
        Product product = null;

        if(org.apache.commons.lang.StringUtils.isNotBlank(productCode)) {

            ProductService productService = new ProductService(conn);
            Map<String, Object> condition = new HashMap<>();
            condition.put("product_code", productCode);
            condition.put("enabled", EnabledType.USED);
            condition.put("vendor_id",stockOptions.getVendor_id());
            product = productService.getProductByCondition(condition, null);
            if(product == null){
                 resultMessage.sStatus(false).sMsg("update stock - 找不到这个商品 。").setData(boutique_id_already_exists);
            }
        }

        if(StringUtils.isBlank(productCode)) {
            resultMessage.sStatus(false).sMsg("update stock - productCode is null 。").setData(boutique_id_is_null);
        } else if(org.apache.commons.lang.StringUtils.isBlank(sizeValue))
            resultMessage.sStatus(false).sMsg("update stock - sizeValue is null 。").setData(skuSize_is_null);
        else if(org.apache.commons.lang.StringUtils.isBlank(quantity)){
            resultMessage.sStatus(false).sMsg("update stock - quantity is null 。").setData(stock_is_null);
        }

        if(resultMessage.getStatus()) {
            resultMessage.sStatus(true).sMsg("SUCCESS").sData(product);
        }
        logger.info("ProductStockEDSManagementValidateParam,outputParams,resultMessage:"+new Gson().toJson(resultMessage));
        return resultMessage;
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
        public String confirmed;
		public String type;
		public String vendor_id;
		public String price;

        public String getConfirmed() {
            return confirmed;
        }

        public void setConfirmed(String confirmed) {
            this.confirmed = confirmed;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

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

        public String getVendor_id() {
            return vendor_id;
        }

        public void setVendor_id(String vendor_id) {
            this.vendor_id = vendor_id;
        }
    }
}
