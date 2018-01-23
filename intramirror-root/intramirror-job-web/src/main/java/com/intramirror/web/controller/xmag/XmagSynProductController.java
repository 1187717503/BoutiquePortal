package com.intramirror.web.controller.xmag;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.main.api.service.SeasonMappingService;
import com.intramirror.product.api.service.stock.IUpdateStockService;
import com.intramirror.web.mapping.api.IProductMapping;
import com.intramirror.web.thread.CommonThreadPool;
import com.intramirror.web.thread.UpdateProductThread;
import com.intramirror.web.util.ApiDataFileUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.RedisService;
import pk.shoplus.service.request.api.IGetPostRequest;
import pk.shoplus.service.request.impl.GetPostRequestService;
import static pk.shoplus.util.DateUtils.getTimeByHour;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.MapUtils;

@Controller
@RequestMapping("/xmag_product")
public class XmagSynProductController implements InitializingBean {

	// loggger
	private final Logger logger = Logger.getLogger(XmagSynProductController.class);

	public static ProductEDSManagement productEDSManagement = new ProductEDSManagement();

	@Resource(name = "xmagSynProductAllMapper")
	private IProductMapping iProductMapping;

	private Map<String, Object> paramsMap;

	@Autowired
	private SeasonMappingService seasonMappingService;

	@Resource(name = "updateStockService")
	private IUpdateStockService iUpdateStockService;

	private static RedisService redisService = RedisService.getInstance();

	@RequestMapping("/syn_product")
	@ResponseBody
	public Map<String, Object> syn_product(@Param(value = "name") String name) {
		logger.info("XmagSynProductAllControllerSynProduct,inputParams,name:"+name);
		MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());
		try {
			// check name
			if (StringUtils.isBlank(name)) {
				return mapUtils.putData("status", StatusType.FAILURE).putData("info", "name is null !!!").getMap();
			}

			// get params
			Map<String, Object> param = (Map<String, Object>) paramsMap.get(name);
			String url = param.get("url").toString();
			String DBContext = param.get("DBContext").toString();
			String BrandId = param.get("BrandId").toString();
			String Key = param.get("Key").toString();
			String CategoryId = param.get("CategoryId").toString();
			String vendor_id = param.get("vendor_id").toString();
			String eventName = param.get("eventName").toString();
			int StartIndex = Integer.parseInt(param.get("StartIndex").toString());
			int EndIndex = Integer.parseInt(param.get("EndIndex").toString());
			String currentDate = DateUtils.getStrDate(new Date(),"yyyyMMdd");
			String redisStartIndex = param.get("redisStartIndex") ==null?"":param.get("redisStartIndex").toString();
			String redisEndIndex = param.get("redisEndIndex") ==null?"":param.get("redisEndIndex").toString();
			int threadNum = Integer.parseInt(param.get("threadNum").toString());
			ThreadPoolExecutor nugnesExecutor = (ThreadPoolExecutor) param.get("executor");
			ApiDataFileUtils fileUtils = (ApiDataFileUtils) param.get("fileUtils");
			int flag = 0;

			/*logger.info("XmagSynProductAllControllerSynProduct,getBoutiqueSeasonCode,vendor_id:" +vendor_id);
			List<Map<String, Object>> boutiqueSeasonCodes = seasonMappingService.getBoutiqueSeasonCode(vendor_id);
			logger.info("XmagSynProductAllControllerSynProduct,getBoutiqueSeasonCode,code:"+JSONObject.toJSONString(boutiqueSeasonCodes)+",vendor_id:" +vendor_id);*/

//			for(Map<String,Object> boutiqueSeasonCode : boutiqueSeasonCodes) {
//				SeasonCode = boutiqueSeasonCode.get("boutique_season_code").toString();

				// start 增量更新
				String byDate = "";
				if(StringUtils.isNotBlank(redisStartIndex) && StringUtils.isNotBlank(redisEndIndex)) {
					String rStartIndex = redisService.getKey(redisStartIndex+currentDate);
					String rEndIndex = redisService.getKey(redisEndIndex+currentDate);
					logger.info("XmagSynProductAllControllerSynProduct,index,startIndex:"+rStartIndex+",endIndex:"+rEndIndex+",name:"+name);

					if(StringUtils.isNotBlank(rStartIndex) || StringUtils.isNotBlank(rEndIndex)) {
						StartIndex = Integer.parseInt(rStartIndex);
						EndIndex = Integer.parseInt(rEndIndex);
					}
					byDate = "&DateStart="+getTimeByHour(24)+"&DateEnd="+getTimeByHour(0);
				}
				// end 增量更新


				while (true) {
					String appendUrl = url + "?DBContext=" + DBContext + "&BrandId=" + BrandId + "&SeasonCode="
							+ "&StartIndex=" + StartIndex + "&EndIndex=" + EndIndex + "&Key=" + Key + "&CategoryId="
							+ CategoryId;
					if(StringUtils.isNotBlank(byDate)) {appendUrl = appendUrl + byDate;}

					logger.info("XmagSynProductAllControllerSynProduct,requestMethod,start,appendUrl:"+appendUrl);
					IGetPostRequest requestGet = new GetPostRequestService();
					String json = requestGet.requestMethod(GetPostRequestService.HTTP_GET, appendUrl, null);
					logger.info("XmagSynProductAllControllerSynProduct,requestMethod,end,url:"+appendUrl+",json:"+json);

					if (StringUtils.isNotBlank(json)) {
						JSONObject jsonOjbect = JSONObject.parseObject(json);
						List<Map<String, Object>> productList = (List<Map<String, Object>>) jsonOjbect.get("product");
						logger.info("XmagSynProductAllControllerSynProduct,convertJsonObjectToList,productListSize:"+productList.size()+",productList:"+JSONObject.toJSONString(productList));

						if (productList != null && productList.size() > 0) {
							fileUtils.bakPendingFile("StartIndex" + StartIndex + "_EndIndex" + EndIndex, json);

							for (Map<String, Object> product : productList) {

								logger.info("XmagSynProductAllControllerSynProduct,convertJson,start,product:"+JSONObject.toJSONString(product));
								List<Map<String,Object>> convertProductList = this.convertJson(product);
								logger.info("XmagSynProductAllControllerSynProduct,convertJson,end,product:"+JSONObject.toJSONString(product)+",convertProductList:"+JSONObject.toJSONString(convertProductList));

								for(Map<String,Object> newProduct : convertProductList) {
									Map<String, Object> mqDataMap = new HashMap<String, Object>();
									mqDataMap.put("product", newProduct);
									mqDataMap.put("vendor_id", vendor_id);

									ProductEDSManagement.ProductOptions productOptions = iProductMapping.mapping(mqDataMap);
									ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
									vendorOptions.setVendorId(Long.parseLong(vendor_id));

									logger.info("XmagSynProductAllControllerSynProduct,initParam,productOptions:"
											+ new Gson().toJson(productOptions) + ",vendorOptions:"
											+ new Gson().toJson(vendorOptions) + ",eventName:" + eventName+",mqDataMap:"+JSONObject.toJSONString(mqDataMap));

									// 线程池
									logger.info("XmagSynProductAllControllerSynProduct,execute,start,productOptions:"
											+ new Gson().toJson(productOptions) + ",vendorOptions:"
											+ new Gson().toJson(vendorOptions) + ",eventName:" + eventName+",index:"+flag);
									CommonThreadPool.execute(eventName, nugnesExecutor, threadNum,
											new UpdateProductThread(productOptions, vendorOptions, fileUtils,mqDataMap));
									logger.info("XmagProductAllProducerControllerExecute,execute,end,productOptions:"
											+ new Gson().toJson(productOptions) + ",vendorOptions:"
											+ new Gson().toJson(vendorOptions) + ",eventName:" + eventName);
								}
								StartIndex++;
								EndIndex++;
								flag ++;
							}
						} else {
							logger.info("XmagSynProductAllControllerSynProduct,productIsNull");
							break;
						}
					} else {
						logger.info("XmagSynProductAllControllerSynProduct,jsonIsNull");
						break;
					}
				}
				// start 增量更新
				if(StringUtils.isNotBlank(redisEndIndex) && StringUtils.isNotBlank(redisStartIndex)) {
					redisService.setKey(redisStartIndex+currentDate,StartIndex+"");
					redisService.setKey(redisEndIndex+currentDate,EndIndex+"");
					logger.info("XmagSynProductAllControllerSynProduct,index,setKey,startIndex:"+StartIndex+",endIndex:"+EndIndex+",name:"+name);

				}
				// end 增量更新
//			}

			logger.info("XmagSynProductAllControllerSynProduct,zeroClearing,flag:"+flag+",eventName:"+eventName);
			if(eventName.equals("apartment_product_all_update") && flag > 100 ) {
				logger.info("XmagSynProductAllControllerSynProduct,zeroClearing,start,vendor_id:"+vendor_id+",eventName:"+eventName+",flag:"+flag);
				iUpdateStockService.zeroClearing(Long.parseLong(vendor_id));
				logger.info("XmagSynProductAllControllerSynProduct,zeroClearing,end,vendor_id:"+vendor_id);
			}

			mapUtils.putData("status", StatusType.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("XmagSynProductAllControllerSynProduct,errorMessage:" + ExceptionUtils.getExceptionDetail(e));
			mapUtils.putData("status", StatusType.FAILURE).putData("info", "error message : " + e.getMessage());
		}
		return mapUtils.getMap();
	}

	private List<Map<String,Object>> convertJson(Map<String,Object> product){
		List<Map<String,Object>> mapList = new ArrayList<>();
		try {
			logger.info("XmagSynProductController,convertJson,product:"+JSONObject.toJSONString(product));
			if(product != null) {
				if(product.get("items") == null) {
					mapList.add(product);
					return mapList;
				}

				Map<String, Object> itemMap = com.alibaba.fastjson15.JSONObject.parseObject(product.get("items").toString());
				if(itemMap.get("item") == null) {
					mapList.add(product);
					return mapList;
				}

				List<Map<String, Object>> items = (List<Map<String, Object>>) itemMap.get("item");

				if(items ==  null || items.size() == 0) {
					mapList.add(product);
					return mapList;
				}

				if(items.size() >10) {
					System.out.println("ass");
				}

				Map<String,List<Map<String,Object>>> colorMap = new HashMap<>();
				for (int i = 0; i < items.size(); i++) {
					String colorCode = items.get(i).get("color").toString() ;
					if(colorMap.get(colorCode) == null) {
						List<Map<String,Object>> colorList = new ArrayList<>();
						colorList.add(items.get(i));
						colorMap.put(colorCode,colorList);
					} else {
						List<Map<String,Object>> colorList =colorMap.get(colorCode);
						colorList.add(items.get(i));
						colorMap.put(colorCode,colorList);
					}
				}

				if(colorMap == null || colorMap.size() ==0){
					mapList.add(product);
					return mapList;
				}

				for(String key : colorMap.keySet()) {
					Map<String,Object> newProduct = new HashMap<>(product);
					Map<String, Object> newItems = com.alibaba.fastjson15.JSONObject.parseObject(newProduct.get("items").toString());
					newItems.put("item",colorMap.get(key));
					newProduct.put("items",newItems);
					mapList.add(newProduct);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("XmagSynProductController,convertJson,errorMessage:"+ExceptionUtils.getExceptionDetail(e));
		}
		return mapList;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		paramsMap = new HashMap<>();
		paramsMap.put("apartment_product_all_update",new MapUtils(new HashMap<String, Object>())
				.putData("url","http://net13server2.net/TheApartmentAPI/MyApi/Productslist/GetProducts")
				.putData("DBContext","Default")
				.putData("BrandId","")
				.putData("StartIndex","1")
				.putData("EndIndex","50")
				.putData("Key","RTs7g634sH")
				.putData("CategoryId","")
				.putData("executor",(ThreadPoolExecutor) Executors.newCachedThreadPool())
				.putData("vendor_id","20")
				.putData("threadNum","5")
				.putData("eventName","apartment_product_all_update")
				.putData("fileUtils",new ApiDataFileUtils("apartment", "product_all_update")).getMap());

		paramsMap.put("apartment_product_delta_update", new MapUtils(new HashMap<String, Object>())
				.putData("url","http://net13server2.net/TheApartmentAPI/MyApi/Productslist/GetProducts")
				.putData("DBContext","Default")
				.putData("BrandId","")
				.putData("StartIndex","1")
				.putData("EndIndex","50")
				.putData("Key","RTs7g634sH")
				.putData("CategoryId","")
				.putData("executor",(ThreadPoolExecutor) Executors.newCachedThreadPool())
				.putData("vendor_id","20")
				.putData("threadNum","5")
				.putData("redisStartIndex","apartment_start_index_bydate")
				.putData("redisEndIndex","apartment_end_index_bydate")
				.putData("eventName","apartment_product_delta_update")
				.putData("fileUtils",new ApiDataFileUtils("apartment", "product_delta_update")).getMap());

		paramsMap.put("dolci_product_all_update",new MapUtils(new HashMap<String, Object>())
				.putData("url","http://net13server2.net/dolcitrameapi/MyApi/Productslist/GetProducts")
				.putData("DBContext","Default")
				.putData("BrandId","")
				.putData("StartIndex","1")
				.putData("EndIndex","50")
				.putData("Key","FwkQO9jAYJ")
				.putData("CategoryId","")
				.putData("executor",(ThreadPoolExecutor) Executors.newCachedThreadPool())
				.putData("vendor_id","37")
				.putData("threadNum","5")
				.putData("eventName","dolci_product_all_update")
				.putData("fileUtils",new ApiDataFileUtils("dolci", "product_all_update")).getMap());

		paramsMap.put("dolci_product_delta_update",new MapUtils(new HashMap<String, Object>())
				.putData("url","http://net13server2.net/dolcitrameapi/MyApi/Productslist/GetProducts")
				.putData("DBContext","Default")
				.putData("BrandId","")
				.putData("StartIndex","1")
				.putData("EndIndex","50")
				.putData("Key","FwkQO9jAYJ")
				.putData("CategoryId","")
				.putData("executor",(ThreadPoolExecutor) Executors.newCachedThreadPool())
				.putData("vendor_id","37")
				.putData("threadNum","5")
				.putData("redisStartIndex","dolci_start_index_bydate")
				.putData("redisEndIndex","dolci_end_index_bydate")
				.putData("eventName","dolci_product_delta_update")
				.putData("fileUtils",new ApiDataFileUtils("dolci", "product_delta_update")).getMap());

		paramsMap.put("mimma_product_all_update",new MapUtils(new HashMap<String, Object>())
				.putData("url","http://net13server3.it/mimmaninniapi/MyApi/Productslist/GetProducts")
				.putData("DBContext","Default")
				.putData("BrandId","")
				.putData("StartIndex","1")
				.putData("EndIndex","50")
				.putData("Key","G4jKb0sFid")
				.putData("CategoryId","")
				.putData("executor",(ThreadPoolExecutor) Executors.newCachedThreadPool())
				.putData("vendor_id","12")
				.putData("threadNum","5")
				.putData("eventName","mimma_product_all_update")
				.putData("fileUtils",new ApiDataFileUtils("mimma", "product_all_update")));

		paramsMap.put("mimma_product_delta_update",new MapUtils(new HashMap<String, Object>())
				.putData("url","http://net13server3.it/mimmaninniapi/MyApi/Productslist/GetProducts")
				.putData("DBContext","Default")
				.putData("BrandId","")
				.putData("StartIndex","1")
				.putData("EndIndex","50")
				.putData("Key","G4jKb0sFid")
				.putData("CategoryId","")
				.putData("executor",(ThreadPoolExecutor) Executors.newCachedThreadPool())
				.putData("vendor_id","12")
				.putData("threadNum","5")
				.putData("redisStartIndex","mimma_start_index_bydate")
				.putData("redisEndIndex","mimma_end_index_bydate")
				.putData("eventName","mimma_product_delta_update")
				.putData("fileUtils",new ApiDataFileUtils("mimma", "product_delta_update")).getMap());
	}

}
