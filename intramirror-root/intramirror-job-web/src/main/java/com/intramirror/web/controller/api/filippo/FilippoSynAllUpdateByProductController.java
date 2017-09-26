package com.intramirror.web.controller.api.filippo;

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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.web.contants.RedisKeyContants;
import com.intramirror.web.mapping.api.IProductMapping;
import com.intramirror.web.thread.CommonThreadPool;
import com.intramirror.web.thread.UpdateProductThread;
import com.intramirror.web.util.ApiDataFileUtils;
import com.intramirror.web.util.GetPostRequestUtil;

import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.RedisService;
import pk.shoplus.service.request.impl.GetPostRequestService;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.FileUtil;
import pk.shoplus.util.MapUtils;

/**
 * Created by dingyifan on 2017/9/18.
 */
@Controller
@RequestMapping("filippo_product_all")
public class FilippoSynAllUpdateByProductController implements InitializingBean {

	// logger
	private static final Logger logger = Logger.getLogger(FilippoSynAllUpdateByProductController.class);

	// getpost util
	private static GetPostRequestUtil getPostRequestUtil = new GetPostRequestUtil();

	// init params
	private Map<String, Object> paramsMap;

	private static final String origin = "origin";
	
	private static final String type = ".txt";
	
	// redis
    private static RedisService redisService = RedisService.getInstance();

	// mapping
	@Resource(name = "filippoSynProductMapping")
	private IProductMapping iProductMapping;

	private static final String filippo_compare_path = "/mnt/filippo/compare/all/";

//	private static final String filippo_compare_path = "/Users/dingyifan/Documents/fileTest/filippo/compare/all/";

	@ResponseBody
	@RequestMapping("/syn_product")
	public Map<String, Object> execute(@Param(value = "name") String name) {
		MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());
		logger.info("FilippoUpdateByStockControllerExecute,inputParams,name:" + name);

		// check name
		if (StringUtils.isBlank(name)) {
			return mapUtils.putData("status", StatusType.FAILURE).putData("info", "name is null").getMap();
		}

		// get param
		Map<String, Object> param = (Map<String, Object>) paramsMap.get(name);
		String vendor_id = param.get("vendor_id").toString();
		String url = param.get("url").toString();
		ApiDataFileUtils fileUtils = (ApiDataFileUtils) param.get("fileUtils");
		String eventName = param.get("eventName").toString();
		int threadNum = Integer.parseInt(param.get("threadNum").toString());
		ThreadPoolExecutor filippoExecutor = (ThreadPoolExecutor) param.get("filippoExecutor");

		ProductEDSManagement.VendorOptions vendorOption = new ProductEDSManagement().getVendorOptions();
		vendorOption.setVendorId(Long.parseLong(vendor_id));

		Map<String, Object> mqMap = new HashMap<>();
		mqMap.put("vendorOptions", vendorOption);

		try {
			logger.info("FilippoUpdateByStockControllerExecute,requestMethod,url:"+url);
			String getResponse = getPostRequestUtil.requestMethod(GetPostRequestService.HTTP_GET, url, null);
			logger.info("FilippoUpdateByStockControllerExecute,requestMethod,getResponse:"+getResponse);

			if (StringUtils.isNotBlank(getResponse)) {

				fileUtils.bakPendingFile("product_vendor_id_"+vendor_id,getResponse);

				FileUtil.createFile(filippo_compare_path, origin+type, getResponse);
				String pathNameUrl = filippo_compare_path + origin+type;
				logger.info("FilippoUpdateByStockControllerExecute,pathNameUrl:" +pathNameUrl);

				int sum = 0;

				//保存源文件
                List<String> mqLists = FileUtil.readFileByFilippoList(pathNameUrl);
                logger.info("pathNameUrl : " +pathNameUrl);

                int index = 0;
        		if (mqLists.size()%10==0){
        			index = mqLists.size()/10;
        		}else{
        			index = mqLists.size()/10 +1;
        		}
                int count = 0;
                int pageSize = 0;
                //拆分數據
                StringBuffer comments = new StringBuffer();
        		for (String src : mqLists) {
        			count++;
        			if (count == index){
        				count = 0;
        				pageSize ++;
        				FileUtil.createFile(filippo_compare_path, origin+pageSize+type, comments.toString());
        				comments = new StringBuffer();
        			}
        			comments.append(src +"\n");
        		}

        		if (StringUtils.isNotBlank(comments.toString())){
        			FileUtil.createFile(filippo_compare_path, origin+(pageSize+1)+type, comments.toString());
        		}

        		String data = DateUtils.getStrDate(new Date(), "yyyyMMdd");
        		String fileValue = redisService.getKey("FM"+data)==null?"":redisService.get("FM"+data);
        		if (StringUtils.isNotBlank(fileValue)){
        			if (10!=Integer.parseInt(fileValue)){
        				for (int i = Integer.parseInt(fileValue); i <= 10; i++) {
                			String path = filippo_compare_path + origin+i+type;
                			redisService.put("FM"+data, i+"");
                			if (FileUtil.fileExists(path)){
                				logger.info("FilippoUpdateByproductControllerExecute,path:"+path);
                				List<String> list = FileUtil.readFileByFilippoList(path);
                				for (String mqStr : list) {
        	            			sum++;
        	    					mqMap.put("product_data", mqStr);
        	    					mqMap.put("full_update_product","1");
        	    					mqMap.put("vendor_id", vendor_id);
        	    					ProductEDSManagement.ProductOptions productOptions = iProductMapping.mapping(mqMap);
    								logger.info("FilippoUpdateByproductControllerExecute,mqStr:"+mqStr+",productOptions:"+JSONObject.toJSONString(productOptions));

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
                			
        				}
        			}
        		}else{
        			for (int i = 1; i <= 10; i++) {
            			String path = filippo_compare_path + origin+i+type;
            			redisService.put("FM"+data, i+"");
            			if (FileUtil.fileExists(path)){
            				logger.info("FilippoUpdateByproductControllerExecute,path:"+path);
            				List<String> list = FileUtil.readFileByFilippoList(path);
            				for (String mqStr : list) {
								sum++;
    	    					mqMap.put("product_data", mqStr);
    	    					mqMap.put("full_update_product","1");
    	    					mqMap.put("vendor_id", vendor_id);
    	    					ProductEDSManagement.ProductOptions productOptions = iProductMapping.mapping(mqMap);
								logger.info("FilippoUpdateByproductControllerExecute,mqStr:"+mqStr+",productOptions:"+JSONObject.toJSONString(productOptions));

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
            			
    				}
        		}
        		
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
		Map<String, Object> filippo_all_updateproduct = new HashMap<>();
		filippo_all_updateproduct.put("url",
				"http://stat.filippomarchesani.net:2060/gw/collect.php/?p=1n3r4&o=intra&q=getall");
		filippo_all_updateproduct.put("vendor_id", "17");
		filippo_all_updateproduct.put("filippo_compare_path", "/mnt/filippo/compare/all/");
		filippo_all_updateproduct.put("fileUtils", new ApiDataFileUtils("filippo", "filippo_all_updateproduct"));
		filippo_all_updateproduct.put("eventName", "filippo_all_updateproduct");
		filippo_all_updateproduct.put("filippoExecutor", filippoExecutor);
		filippo_all_updateproduct.put("threadNum", "5");

		// put initData
		paramsMap = new HashMap<>();
		paramsMap.put("filippo_all_updateproduct", filippo_all_updateproduct);
	}
}
