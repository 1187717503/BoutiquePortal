package com.intramirror.web.controller.xmag;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;

import com.intramirror.main.api.service.SeasonMappingService;
import com.intramirror.web.contants.RedisKeyContants;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.web.mapping.api.IProductMapping;
import com.intramirror.web.thread.CommonThreadPool;
import com.intramirror.web.thread.UpdateProductThread;
import com.intramirror.web.util.ApiDataFileUtils;

import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.RedisService;
import pk.shoplus.service.request.api.IGetPostRequest;
import pk.shoplus.service.request.impl.GetPostRequestService;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.MapUtils;

import static pk.shoplus.util.DateUtils.getTimeByHour;

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
			String SeasonCode = param.get("SeasonCode").toString();
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

			logger.info("XmagSynProductAllControllerSynProduct,getBoutiqueSeasonCode,vendor_id:" +vendor_id);
			List<Map<String, Object>> boutiqueSeasonCodes = seasonMappingService.getBoutiqueSeasonCode(vendor_id);
			logger.info("XmagSynProductAllControllerSynProduct,getBoutiqueSeasonCode,code:"+JSONObject.toJSONString(boutiqueSeasonCodes)+",vendor_id:" +vendor_id);

			for(Map<String,Object> boutiqueSeasonCode : boutiqueSeasonCodes) {
				SeasonCode = boutiqueSeasonCode.get("boutique_season_code").toString();

				// start 增量更新
				String byDate = "";
				if(StringUtils.isNotBlank(redisStartIndex) && StringUtils.isNotBlank(redisEndIndex)) {
					String rStartIndex = redisService.getKey(redisStartIndex+SeasonCode+currentDate);
					String rEndIndex = redisService.getKey(redisEndIndex+SeasonCode+currentDate);
					logger.info("XmagSynProductAllControllerSynProduct,index,startIndex:"+rStartIndex+",endIndex:"+rEndIndex+",name:"+name);

					if(StringUtils.isNotBlank(rStartIndex) || StringUtils.isNotBlank(rEndIndex)) {
						StartIndex = Integer.parseInt(rStartIndex);
						EndIndex = Integer.parseInt(rEndIndex);
					}
					byDate = "&DateStart="+getTimeByHour(24)+"&DateEnd="+getTimeByHour(0);
				}
				// end 增量更新


				while (true) {
					String appendUrl = url + "?DBContext=" + DBContext + "&BrandId=" + BrandId + "&SeasonCode=" + SeasonCode
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
								Map<String, Object> mqDataMap = new HashMap<String, Object>();
								mqDataMap.put("product", product);
								mqDataMap.put("vendor_id", vendor_id);

								ProductEDSManagement.ProductOptions productOptions = iProductMapping.mapping(mqDataMap);
								ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
								vendorOptions.setVendorId(Long.parseLong(vendor_id));

								logger.info("XmagSynProductAllControllerSynProduct,initParam,productOptions:"
										+ new Gson().toJson(productOptions) + ",vendorOptions:"
										+ new Gson().toJson(vendorOptions) + ",eventName:" + eventName);

								// 线程池
								logger.info("XmagSynProductAllControllerSynProduct,execute,start,productOptions:"
										+ new Gson().toJson(productOptions) + ",vendorOptions:"
										+ new Gson().toJson(vendorOptions) + ",eventName:" + eventName);
								CommonThreadPool.execute(eventName, nugnesExecutor, threadNum,
										new UpdateProductThread(productOptions, vendorOptions, fileUtils,mqDataMap));
								logger.info("XmagProductAllProducerControllerExecute,execute,end,productOptions:"
										+ new Gson().toJson(productOptions) + ",vendorOptions:"
										+ new Gson().toJson(vendorOptions) + ",eventName:" + eventName);

								StartIndex++;
								EndIndex++;
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
					redisService.setKey(redisStartIndex+SeasonCode+currentDate,StartIndex+"");
					redisService.setKey(redisEndIndex+SeasonCode+currentDate,EndIndex+"");
					logger.info("XmagSynProductAllControllerSynProduct,index,setKey,startIndex:"+StartIndex+",endIndex:"+EndIndex+",name:"+name);

				}
				// end 增量更新
			}

			mapUtils.putData("status", StatusType.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("XmagSynProductAllControllerSynProduct,errorMessage:" + ExceptionUtils.getExceptionDetail(e));
			mapUtils.putData("status", StatusType.FAILURE).putData("info", "error message : " + e.getMessage());
		}
		return mapUtils.getMap();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		ThreadPoolExecutor xmag_all_updateProduct_executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		Map<String, Object> xmag_all_updateProduct = new HashMap<>();
		xmag_all_updateProduct.put("url", "http://net13server2.net/TheApartmentAPI/MyApi/Productslist/GetProducts");
		xmag_all_updateProduct.put("DBContext", "Default");
		xmag_all_updateProduct.put("BrandId", "");
		xmag_all_updateProduct.put("SeasonCode", "079");
		xmag_all_updateProduct.put("StartIndex", "1");
		xmag_all_updateProduct.put("EndIndex", "50");
		xmag_all_updateProduct.put("Key", "RTs7g634sH");
		xmag_all_updateProduct.put("CategoryId", "");
		xmag_all_updateProduct.put("executor", xmag_all_updateProduct_executor);
		xmag_all_updateProduct.put("vendor_id", "19");
		xmag_all_updateProduct.put("threadNum", "5");
		xmag_all_updateProduct.put("eventName", "apartment_product_all_update");
		xmag_all_updateProduct.put("fileUtils", new ApiDataFileUtils("xmag", "apartment_product_all_update"));

		ThreadPoolExecutor apartment_product_delta_update_executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		Map<String, Object> apartment_product_delta_update = new HashMap<>();
		apartment_product_delta_update.put("url", "http://net13server2.net/TheApartmentAPI/MyApi/Productslist/GetProducts");
		apartment_product_delta_update.put("DBContext", "Default");
		apartment_product_delta_update.put("BrandId", "");
		apartment_product_delta_update.put("SeasonCode", "079");
		apartment_product_delta_update.put("StartIndex", "1");
		apartment_product_delta_update.put("EndIndex", "10");
		apartment_product_delta_update.put("Key", "RTs7g634sH");
		apartment_product_delta_update.put("CategoryId", "");
		apartment_product_delta_update.put("executor", apartment_product_delta_update_executor);
		apartment_product_delta_update.put("vendor_id", "19");
		apartment_product_delta_update.put("threadNum", "5");
		apartment_product_delta_update.put("redisStartIndex", RedisKeyContants.apartment_start_index_bydate);
		apartment_product_delta_update.put("redisEndIndex",RedisKeyContants.apartment_end_index_bydate);
		apartment_product_delta_update.put("eventName", "apartment_product_delta_update");
		apartment_product_delta_update.put("fileUtils", new ApiDataFileUtils("xmag", "apartment_product_delta_update"));

		// put data
		paramsMap = new HashMap<>();
		paramsMap.put("apartment_product_all_update", xmag_all_updateProduct);
		paramsMap.put("apartment_product_delta_update", apartment_product_delta_update);



	}

}
