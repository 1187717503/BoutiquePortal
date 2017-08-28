package com.intramirror.web.enums;

import com.intramirror.web.mapping.impl.QuadraSynProductMapping;
import com.intramirror.web.mapping.impl.XmagSynProductMapping;

import pk.shoplus.DBConnector;
import pk.shoplus.model.*;
import pk.shoplus.service.ApiMqService;
import pk.shoplus.service.RedisService;
import pk.shoplus.service.mapping.api.IMapping;
import pk.shoplus.service.mapping.impl.*;

public enum QueueNameJobEnum {

	AtelierUpdateOrderStatus("AtelierUpdateOrderStatus",new OrderAtelierUpdateStatusMapping(),"Atelier调用IM接口修改订单状态"),
	CloudStoreCreateOrder("CloudStoreCreateOrder",new OrderCloudStoreMapping(),"调用CloudStore接口创建订单"),
	CloudStoreUpdateOrderStatus("CloudStoreUpdateOrderStatus",new OrderCloudStoreUpdateStatusMapping(),"调用CloudStoreUpdateOrderStatus接口更新订单状态"),
	EdsCreateOrder("EdsCreateOrder",new OrderEDSMapping(),"调用EDS接口创建订单"),
	EdsGetOrderStatus("EdsGetOrderStatus",new OrderEdsUpdateStatusMapping(),"调用EDS接口获取订单状态"),

	EdsCreateProduct("EdsCreateProduct",new ProductEDSMapping(),"调用EDS接口创建商品"),
	EdsUpdateAllProduct("EdsUpdateAllProduct",new ProductEDSMapping(),"调用EDS接口创建商品-全量"),

	EdsUpdateStock("EdsUpdateStock",new ProductStockEDSMapping(),"调用EDS接口修改库存"),

	AtelierCreateProductLucianaBari("AtelierCreateProductLucianaBari",new ProductAtelierMapping(),"Atelier调用IM接口创建商品"),
	AtelierCreateProductDante("AtelierCreateProductDante",new ProductAtelierMapping(),"Atelier调用IM接口创建商品"),
	AtelierCreateProductICinqueFiori("AtelierCreateProductICinqueFiori",new ProductAtelierMapping(),"Atelier调用IM接口创建商品"),
	AtelierCreateProductMimmaNinni("AtelierCreateProductMimmaNinni",new ProductAtelierMapping(),"Atelier调用IM接口创建商品"),
	AtelierCreateProductDiPierro("AtelierCreateProductDiPierro",new ProductAtelierMapping(),"Atelier调用IM接口创建商品"),
	AtelierCreateProductGisaBoutique("AtelierCreateProductGisaBoutique",new ProductAtelierMapping(),"Atelier调用IM接口创建商品"),
	AtelierCreateProductWiseBoutique("AtelierCreateProductWiseBoutique",new ProductAtelierMapping(),"Atelier调用IM接口创建商品"),

	AtelierUpdateSkuStockLucianaBari("AtelierUpdateSkuStockLucianaBari",new ProductStockAtelierMapping(),"Atelier调用IM修改库存"),
	AtelierUpdateSkuStockDante("AtelierUpdateSkuStockDante",new ProductStockAtelierMapping(),"Atelier调用IM修改库存"),
	AtelierUpdateSkuStockICinqueFiori("AtelierUpdateSkuStockICinqueFiori",new ProductStockAtelierMapping(),"Atelier调用IM修改库存"),
	AtelierUpdateSkuStockMimmaNinni("AtelierUpdateSkuStockMimmaNinni",new ProductStockAtelierMapping(),"Atelier调用IM修改库存"),
	AtelierUpdateSkuStockDiPierro("AtelierUpdateSkuStockDiPierro",new ProductStockAtelierMapping(),"Atelier调用IM修改库存"),
	AtelierUpdateSkuStockGisaBoutique("AtelierUpdateSkuStockGisaBoutique",new ProductStockAtelierMapping(),"Atelier调用IM修改库存"),
	AtelierUpdateSkuStockWiseBoutique("AtelierUpdateSkuStockWiseBoutique",new ProductStockAtelierMapping(),"Atelier调用IM修改库存"),

	AtelierUpdateProductLucianaBari("AtelierUpdateProductLucianaBari",new UpdateProductMapping(),"Atelier调用IM接口修改商品信息"),
	AtelierUpdateProductDante("AtelierUpdateProductDante",new UpdateProductMapping(),"Atelier调用IM接口修改商品信息"),
	AtelierUpdateProductICinqueFiori("AtelierUpdateProductICinqueFiori",new UpdateProductMapping(),"Atelier调用IM接口修改商品信息"),
	AtelierUpdateProductMimmaNinni("AtelierUpdateProductMimmaNinni",new UpdateProductMapping(),"Atelier调用IM接口修改商品信息"),
	AtelierUpdateProductDiPierro("AtelierUpdateProductDiPierro",new UpdateProductMapping(),"Atelier调用IM接口修改商品信息"),
	AtelierUpdateProductGisaBoutique("AtelierUpdateProductGisaBoutique",new UpdateProductMapping(),"Atelier调用IM接口修改商品信息"),
	AtelierUpdateProductWiseBoutique("AtelierUpdateProductWiseBoutique",new UpdateProductMapping(),"Atelier调用IM接口修改商品信息"),

	CloudStoreSynProduct("CloudStoreSynProduct",new CloudStoreGetInventoryMapping(),"IM调用cloudstore getInventory更新商品信息"),
	CloudStoreSynStock("CloudStoreSynStock",new CloudStoreGetEventsMapping(),"IM调用cloudstore getEvents更新库存信息"),

	FilippoSynProduct("FilippoSynProduct",new FilippoSynProductMapping(),"IM调用filippo 接口更新商品信息"),
	FilippoSynAllProduct("FilippoSynAllProduct",new FilippoSynProductMapping(),"IM调用filippo 接口更新商品信息-全量"),

	FilippoSynStock("FilippoSynStock",new FilippoSynStockMapping(),"IM调用filippo 接口更新库存信息"),
	
	QuadraSynAllProduct("QuadraSynAllProduct",new QuadraSynProductMapping(),"IM调用quadra 接口更新商品信息"),
	
	XmagSynProduct("XmagSynProduct",new XmagSynProductMapping(),"IM调用Xmag 接口更新商品信息"),
	
	QuadraSynDayProduct("QuadraSynDayProduct",new QuadraSynProductMapping(),"IM调用quadra 接口更新商品信息");
	

	private String code;
	private IMapping mapping;
	private String value;

	private QueueNameJobEnum(String code, IMapping mapping, String value) {
		this.code = code;
		this.mapping = mapping;
		this.value = value;
	}

	private QueueNameJobEnum(String code, String value) {
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
