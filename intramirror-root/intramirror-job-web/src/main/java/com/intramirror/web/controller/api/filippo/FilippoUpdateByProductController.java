package com.intramirror.web.controller.api.filippo;

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

import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.web.mapping.api.IProductMapping;
import com.intramirror.web.thread.CommonThreadPool;
import com.intramirror.web.thread.UpdateProductThread;
import com.intramirror.web.util.ApiDataFileUtils;
import com.intramirror.web.util.GetPostRequestUtil;

import difflib.DiffRow;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.request.impl.GetPostRequestService;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.FileUtil;
import pk.shoplus.util.MapUtils;

/**
 * Created by dingyifan on 2017/9/18.
 */
@Controller
@RequestMapping("filippo_product")
public class FilippoUpdateByProductController implements InitializingBean {

	// logger
	private static final Logger logger = Logger.getLogger(FilippoUpdateByProductController.class);

	// getpost util
	private static GetPostRequestUtil getPostRequestUtil = new GetPostRequestUtil();

	// init params
	private Map<String, Object> paramsMap;


	private static final String revised = "revised";
	
	private static final String origin = "origin";
	
	private static final String type = ".txt";

	// mapping
	@Resource(name = "filippoSynProductMapping")
	private IProductMapping iProductMapping;

	@ResponseBody
	@RequestMapping("/syn_product")
	public Map<String, Object> execute(@Param(value = "name") String name) {
		MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());
		logger.info("start FilippoUpdateByStockControllerExecute,inputParams,name:" + name);

		// check name
		if (StringUtils.isBlank(name)) {
			return mapUtils.putData("status", StatusType.FAILURE).putData("info", "name is null").getMap();
		}

		// get param
		Map<String, Object> param = (Map<String, Object>) paramsMap.get(name);
		String vendor_id = param.get("vendor_id").toString();
		String url = param.get("url").toString();
		String filippo_compare_path = param.get("filippo_compare_path").toString();
		ApiDataFileUtils fileUtils = (ApiDataFileUtils) param.get("fileUtils");

		String eventName = param.get("eventName").toString();
		int threadNum = Integer.parseInt(param.get("threadNum").toString());
		ThreadPoolExecutor filippoExecutor = (ThreadPoolExecutor) param.get("filippoExecutor");

		ProductEDSManagement.VendorOptions vendorOption = new ProductEDSManagement().getVendorOptions();
		vendorOption.setVendorId(Long.parseLong(vendor_id));

		Map<String, Object> mqMap = new HashMap<>();
		mqMap.put("vendorOptions", vendorOption);
		try {
			logger.info(" start invoke filippo api !" + url);
			String getResponse = getPostRequestUtil.requestMethod(GetPostRequestService.HTTP_GET, url, null);
			logger.info(" end invoke filippo api !");
			if (StringUtils.isNotBlank(getResponse)) {
				fileUtils.bakPendingFile("product_vendor_id_"+vendor_id,getResponse);
				// origin 不存在创建
				// 1.如果第一次触发此接口,origin file不存在则创建,用作下次调用对比
				if (!FileUtil.fileExists(filippo_compare_path+origin+type)) {
					FileUtil.createFile(filippo_compare_path, origin+type, getResponse);
				}
				FileUtil.createFile(filippo_compare_path, revised+type, getResponse);
				// 备份
				String originContent = FileUtil.readFileByFilippo(filippo_compare_path+origin+type);
				String revisedContent = FileUtil.readFileByFilippo(filippo_compare_path+revised+type);

				FileUtil.createFile(filippo_compare_path, "compare_origin.txt", originContent);
				FileUtil.createFile(filippo_compare_path, "compare_revised.txt", revisedContent);
				List<DiffRow> diffRows = FileUtil.CompareTxt(filippo_compare_path+"compare_origin.txt",
						filippo_compare_path+"compare_revised.txt");

				// 对比文件处理
				int sum = 0;
				StringBuffer stringBuffer = new StringBuffer();
				for (DiffRow diffRow : diffRows) {
					DiffRow.Tag tag = diffRow.getTag();
					if (tag == DiffRow.Tag.INSERT || tag == DiffRow.Tag.CHANGE) {
						sum++;
						logger.info("FilippoUpdateByproductControllerExecute,change -------" + diffRow.getNewLine());
						stringBuffer.append(diffRow.getNewLine() + "\n");
						if (!StringUtils.isNotBlank(diffRow.getOldLine())){
							break;
						}
						mqMap.put("product_data", diffRow.getOldLine());
						mqMap.put("vendor_id", vendor_id);
						ProductEDSManagement.ProductOptions productOptions = iProductMapping.mapping(mqMap);
						// 线程池
						logger.info("FilippoUpdateByproductControllerExecute,execute,startDate:"
								+ DateUtils.getStrDate(new Date()) + ",productOptions:"
								+ new Gson().toJson(productOptions) + ",vendorOptions:"
								+ new Gson().toJson(vendorOption) + ",eventName:" + eventName + "index:" + sum);
						CommonThreadPool.execute(eventName, filippoExecutor, threadNum,
								new UpdateProductThread(productOptions, vendorOption, fileUtils, mqMap));
						logger.info("FilippoUpdateByproductControllerExecute,execute,endDate:"
								+ DateUtils.getStrDate(new Date()) + ",productOptions:"
								+ new Gson().toJson(productOptions) + ",vendorOptions:"
								+ new Gson().toJson(vendorOption) + ",eventName:" + eventName + "index:" + sum);

					}
				}
				// 6.处理结束后备份origin,并重置origin
				String originContentAll = FileUtil.readFile(filippo_compare_path+revised+type);
				logger.info("FilippoUpdateByproductControllerExecute,origin : "+originContentAll);
				FileUtil.createFile(filippo_compare_path, origin+type, originContentAll);
				mapUtils.putData("status",StatusType.SUCCESS).putData("info","SUCCESS !!!");
			} else {
				// 日志
				logger.info("FilippoUpdateByproductControllerExecute,getUrlResult is null");
				mapUtils.putData("status", StatusType.FAILURE).putData("info","getUrlResult is null !!!");
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("SynSkuProducerByFilippoController,errorMessage:" + ExceptionUtils.getExceptionDetail(e));
		}
		logger.info("end SynSkuProducerByFilippoController populateResult mapUtils,mapUtils:"
				+ JSONObject.toJSONString(mapUtils));
		return mapUtils.getMap();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// filippo
		ThreadPoolExecutor filippoExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		Map<String, Object> filippo_increment_updateproduct = new HashMap<>();
		filippo_increment_updateproduct.put("url",
				"http://stat.filippomarchesani.net:2060/gw/collect.php/?p=1n3r4&o=intra&q=getall");
		filippo_increment_updateproduct.put("vendor_id", "17");
		filippo_increment_updateproduct.put("filippo_compare_path", "/mnt/compare/filippo/product/");
		filippo_increment_updateproduct.put("fileUtils", new ApiDataFileUtils("filippo", "filippo增量更新库存"));
		filippo_increment_updateproduct.put("eventName", "filippo_increment_updateproduct");
		filippo_increment_updateproduct.put("filippoExecutor", filippoExecutor);
		filippo_increment_updateproduct.put("threadNum", "5");
		// put initData
		paramsMap = new HashMap<>();
		paramsMap.put("filippo_increment_updateproduct", filippo_increment_updateproduct);
	}

}
