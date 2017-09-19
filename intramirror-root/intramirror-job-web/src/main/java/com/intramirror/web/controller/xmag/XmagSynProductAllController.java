package com.intramirror.web.controller.xmag;

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
import pk.shoplus.service.request.api.IGetPostRequest;
import pk.shoplus.service.request.impl.GetPostRequestService;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.MapUtils;

@Controller
@RequestMapping("/xmagSynProductAll")
public class XmagSynProductAllController implements InitializingBean {

	private final Logger logger = Logger.getLogger(XmagSynProductAllController.class);

	public static ProductEDSManagement productEDSManagement = new ProductEDSManagement();

	// mapping
	@Resource(name = "xmagSynProductAllMapper")
	private IProductMapping iProductMapping;
	/**
	 * parm Map
	 */
	private Map<String, Object> paramsMap;

	@RequestMapping("/syn_product_all")
	@ResponseBody
	public Map<String, Object> execute(@Param(value = "name") String name) {
		logger.info("------------------------------------------start xmagSynProductAll.populateResult");
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
			int StartIndex = Integer.parseInt(param.get("StartIndex").toString());
			int EndIndex = Integer.parseInt(param.get("EndIndex").toString());
			String Key = param.get("Key").toString();
			String CategoryId = param.get("CategoryId").toString();
			String vendor_id = param.get("vendor_id").toString();
			String eventName = param.get("eventName").toString();
			int threadNum = Integer.parseInt(param.get("threadNum").toString());
			ThreadPoolExecutor nugnesExecutor = (ThreadPoolExecutor) param.get("nugnesExecutor");
			ApiDataFileUtils fileUtils = (ApiDataFileUtils) param.get("fileUtils");

			while (true) {

				url = url + "?DBContext=" + DBContext + "&BrandId=" + BrandId + "&SeasonCode=" + SeasonCode
						+ "&StartIndex=" + StartIndex + "&EndIndex=" + EndIndex + "&Key=" + Key + "&CategoryId="
						+ CategoryId;

				String json = "";
				IGetPostRequest requestGet = new GetPostRequestService();
				logger.info("Xmag getProudcts  Call the interface to get the data    url:" + url);
				json = requestGet.requestMethod(GetPostRequestService.HTTP_GET, url, null);
				logger.info("Xmag getProudcts result:" + json);
				JSONObject jsonOjbect = null;

				if (StringUtils.isNotBlank(json)) {
					jsonOjbect = JSONObject.parseObject(json);
					List<Map<String, Object>> list = (List<Map<String, Object>>) jsonOjbect.get("product");
					logger.info("product list size:" + list.size());

					if (list != null && list.size() > 0) {
						fileUtils.bakPendingFile("StartIndex" + StartIndex + "_EndIndex" + EndIndex, json);
						int index = 1;
						for (Map<String, Object> product : list) {
							Map<String, Object> mqDataMap = new HashMap<String, Object>();
							mqDataMap.put("product", product);
							mqDataMap.put("vendor_id", vendor_id);
							logger.info("Push Index" + index);
							String src = new Gson().toJson(mqDataMap);
							logger.info("Xmag parm Map : " + src);

							// 映射数据 封装VO
							ProductEDSManagement.ProductOptions productOptions = iProductMapping.mapping(mqDataMap);
							ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
							vendorOptions.setVendorId(Long.parseLong(vendor_id));

							logger.info("XmagProductAllProducerControllerExecute,initParam,productOptions:"
									+ new Gson().toJson(productOptions) + ",vendorOptions:"
									+ new Gson().toJson(vendorOptions) + ",eventName:" + eventName);

							// 线程池
							logger.info("XmagProductAllProducerControllerExecute,execute,startDate:"
									+ DateUtils.getStrDate(new Date()) + ",productOptions:"
									+ new Gson().toJson(productOptions) + ",vendorOptions:"
									+ new Gson().toJson(vendorOptions) + ",eventName:" + eventName);
							CommonThreadPool.execute(eventName, nugnesExecutor, threadNum,
									new UpdateProductThread(productOptions, vendorOptions, fileUtils));
							logger.info("XmagProductAllProducerControllerExecute,execute,endDate:"
									+ DateUtils.getStrDate(new Date()) + ",productOptions:"
									+ new Gson().toJson(productOptions) + ",vendorOptions:"
									+ new Gson().toJson(vendorOptions) + ",eventName:" + eventName);

							StartIndex++;
							EndIndex++;
							index++;
						}
					}
				} else {
					logger.info("Xmag getProudcts result null ");
					break;
				}

			}
			mapUtils.putData("status", StatusType.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("xmagSynProductAllExceptions : " + ExceptionUtils.getExceptionDetail(e));
			mapUtils.putData("status", StatusType.FAILURE).putData("info", "error message : " + e.getMessage());
		}

		logger.info("------------------------------------------end xmagSynProductAll.populateResult mapUtils : "
				+ new Gson().toJson(mapUtils));
		return mapUtils.getMap();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// xmag
		ThreadPoolExecutor nugnesExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		Map<String, Object> xmag_all_updateProduct = new HashMap<>();
		xmag_all_updateProduct.put("url", "http://net13server2.net/TheApartmentAPI/MyApi/Productslist/GetProducts");
		xmag_all_updateProduct.put("DBContext", "Default");
		xmag_all_updateProduct.put("BrandId", "");
		xmag_all_updateProduct.put("SeasonCode", "079");
		xmag_all_updateProduct.put("StartIndex", "1");
		xmag_all_updateProduct.put("EndIndex", "50");
		xmag_all_updateProduct.put("Key", "RTs7g634sH");
		xmag_all_updateProduct.put("CategoryId", "");
		xmag_all_updateProduct.put("nugnesExecutor", nugnesExecutor);
		xmag_all_updateProduct.put("vendor_id", "19");
		xmag_all_updateProduct.put("threadNum", "5");
		xmag_all_updateProduct.put("eventName", "Xmag全量更新库存");
		xmag_all_updateProduct.put("fileUtils", new ApiDataFileUtils("xmag", "xmag全量更新库存"));

		// put data
		paramsMap = new HashMap<>();
		paramsMap.put("xmag_all_updateProduct", xmag_all_updateProduct);

	}

}
