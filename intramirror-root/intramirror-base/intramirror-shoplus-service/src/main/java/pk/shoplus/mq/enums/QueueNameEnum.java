package pk.shoplus.mq.enums;

import org.redisson.Redisson;
import pk.shoplus.DBConnector;
import pk.shoplus.model.*;
import pk.shoplus.service.ApiMqService;
import pk.shoplus.service.RedisService;
import pk.shoplus.service.mapping.api.IMapping;
import pk.shoplus.service.mapping.impl.*;

import java.util.Map;
import java.util.Queue;

import org.sql2o.Connection;

public enum QueueNameEnum {
	/*EdsCreateProduct_1("EdsCreateProduct1",new ProductEDSMapping(),"调用EDS接口创建商品"),
	EdsUpdateStock_11("EdsUpdateStock11",new ProductStockEDSMapping(),"调用EDS接口修改库存"),
	EdsCreateOrder_12("EdsCreateOrder12",new OrderEDSMapping(),"调用EDS接口创建订单"),
	EdsGetOrderStatus_14("EdsGetOrderStatus14",new OrderEdsUpdateStatusMapping(),"调用EDS接口获取订单状态"),
	AtelierCreateProduct_13("AtelierCreateProduct13",new ProductAtelierMapping(),"Atelier调用IM接口创建商品"),
	AtelierUpdateSkuStock_15("AtelierUpdateSkuStock15",new ProductStockAtelierMapping(),"Atelier调用IM修改库存"),
	AtelierUpdateOrderStatus_16("AtelierUpdateOrderStatus16",new OrderAtelierUpdateStatusMapping(),"Atelier调用IM接口修改订单状态"),
	AtelierUpdateProduct_29("AtelierUpdateProduct",new UpdateProductMapping(),"Atelier调用IM接口修改商品信息"),
	CloudStoreCreateOrder_31("CloudStoreCreateOrder31",new OrderCloudStoreMapping(),"调用CloudStore接口创建订单"),
	CloudStoreUpdateOrderStatus_32("CloudStoreUpdateOrderStatus32",new OrderCloudStoreUpdateStatusMapping(),"调用CloudStoreUpdateOrderStatus接口更新订单状态"),
	CloudStoreSynProduct_17("CloudStoreSynProduct17",new CloudStoreGetInventoryMapping(),"IM调用cloudstore getInventory更新商品信息"),
	CloudStoreSynStock_18("CloudStoreSynStock18",new CloudStoreGetEventsMapping(),"IM调用cloudstore getEvents更新库存信息"),

	EdsCreateProduct("EdsCreateProduct","调用EDS接口创建商品"),
	EdsUpdateStock("EdsUpdateStock","调用EDS接口修改库存"),
	EdsCreateOrder("EdsCreateOrder","调用EDS接口创建订单"),
	EdsGetOrderStatus("EdsGetOrderStatus","调用EDS接口获取订单状态"),
	AtelierCreateProduct("AtelierCreateProduct","Atelier调用IM接口创建商品"),
	AtelierUpdateSkuStock("AtelierUpdateSkuStock","Atelier调用IM修改库存"),
	AtelierUpdateOrderStatus("AtelierUpdateOrderStatus","Atelier调用IM接口修改订单状态"),
	AtelierUpdateProduct("AtelierUpdateProduct","Atelier调用IM修改商品信息"),
	CloudStoreCreateOrder("CloudStoreCreateOrder","调用CloudStore接口创建订单"),
	CloudStoreUpdateOrderStatus("CloudStoreUpdateOrderStatus","调用CloudStoreUpdateOrderStatus接口 更新订单状态"),
	CloudStoreSynProduct("CloudStoreSynProduct","IM调用cloudstore getInventory更新商品信息"),
	CloudStoreSynStock("CloudStoreSynStock","IM调用cloudstore getEvents更新库存信息");*/

	EdsCreateProduct("EdsCreateProduct",new ProductEDSMapping(),"调用EDS接口创建商品"),
	EdsUpdateStock("EdsUpdateStock",new ProductStockEDSMapping(),"调用EDS接口修改库存"),
	EdsCreateOrder("EdsCreateOrder",new OrderEDSMapping(),"调用EDS接口创建订单"),
	EdsGetOrderStatus("EdsGetOrderStatus",new OrderEdsUpdateStatusMapping(),"调用EDS接口获取订单状态"),
	AtelierCreateProduct("AtelierCreateProduct",new ProductAtelierMapping(),"Atelier调用IM接口创建商品"),
	AtelierUpdateSkuStock("AtelierUpdateSkuStock",new ProductStockAtelierMapping(),"Atelier调用IM修改库存"),
	AtelierUpdateOrderStatus("AtelierUpdateOrderStatus",new OrderAtelierUpdateStatusMapping(),"Atelier调用IM接口修改订单状态"),
	AtelierUpdateProduct("AtelierUpdateProduct",new UpdateProductMapping(),"Atelier调用IM接口修改商品信息"),
	CloudStoreCreateOrder("CloudStoreCreateOrder",new OrderCloudStoreMapping(),"调用CloudStore接口创建订单"),
	CloudStoreUpdateOrderStatus("CloudStoreUpdateOrderStatus",new OrderCloudStoreUpdateStatusMapping(),"调用CloudStoreUpdateOrderStatus接口更新订单状态"),
	CloudStoreSynProduct("CloudStoreSynProduct",new CloudStoreGetInventoryMapping(),"IM调用cloudstore getInventory更新商品信息"),
	CloudStoreSynStock("CloudStoreSynStock",new CloudStoreGetEventsMapping(),"IM调用cloudstore getEvents更新库存信息"),
	FilippoSynProduct("FilippoSynProduct",new FilippoSynProductMapping(),"IM调用filippo 接口更新商品信息"),
	FilippoSynStock("FilippoSynStock",new FilippoSynStockMapping(),"IM调用filippo 接口更新库存信息");

	private String code;
	private IMapping mapping;
	private String value;

	private QueueNameEnum(String code,IMapping mapping, String value) {
		this.code = code;
		this.mapping = mapping;
		this.value = value;
	}

	private QueueNameEnum(String code, String value) {
		this.code = code;
		this.value = value;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public IMapping getMapping() {
		return mapping;
	}

	public void setMapping(IMapping mapping) {
		this.mapping = mapping;
	}

	public String getMqCode(){
		try {
			// 查询redis
			String cacheMap = "mqName_mqNameCfg";
			String mqKey = this.code;
			Object objValue = RedisService.getInstance().getMapInfoByKey(cacheMap,mqKey);
			if(objValue != null) {
				return objValue.toString();
			}

			// 查询数据库
			ApiMqService apiMqService = new ApiMqService(DBConnector.sql2o.open());
			ApiMq apiMq = apiMqService.getMqByName(this.getCode());
			if(apiMq != null) {
				String mqValue = this.code+apiMq.getApi_configuration_id();
				RedisService.getInstance().putMap(cacheMap,this.code,mqValue);
				return mqValue;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


}
