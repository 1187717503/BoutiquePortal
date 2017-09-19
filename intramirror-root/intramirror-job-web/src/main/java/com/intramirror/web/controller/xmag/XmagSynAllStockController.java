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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.main.api.service.SeasonMappingService;
import com.intramirror.web.mapping.api.IStockMapping;
import com.intramirror.web.mapping.vo.StockOption;
import com.intramirror.web.thread.CommonThreadPool;
import com.intramirror.web.thread.UpdateStockThread;
import com.intramirror.web.util.ApiDataFileUtils;

import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.request.api.IGetPostRequest;
import pk.shoplus.service.request.impl.GetPostRequestService;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.MapUtils;

@Controller
@RequestMapping("/xmagSynAllStock")
public class XmagSynAllStockController implements InitializingBean {

	private final Logger logger = Logger.getLogger(XmagSynAllStockController.class);
	
	public static ProductEDSManagement productEDSManagement = new ProductEDSManagement();

	@Autowired
    private SeasonMappingService seasonMappingService;
	
	 // mapping
    @Resource(name = "xmagSynAllStockMapping")
    private IStockMapping iStockMapping;
	/**
	 * parm Map
	 */
	private Map<String,Object> paramsMap;
	
	@RequestMapping("/syn_stock_all")
	@ResponseBody
	public Map<String, Object> execute(@Param(value = "name") String name) {
		logger.info("------------------------------------------start xmagSynstockAll.populateResult");
		MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());
		
		 // check name
        if(StringUtils.isBlank(name)) {
            return mapUtils.putData("status",StatusType.FAILURE).putData("info","name is null !!!").getMap();
        }
        try{
	        // get params
	        Map<String,Object> param = (Map<String, Object>) paramsMap.get(name);
	        String url = param.get("url").toString();
	        String DBContext = param.get("DBContext").toString();
	        String SeasonCode = param.get("SeasonCode").toString();
	        String typeSync = param.get("typeSync").toString();
	        String Key = param.get("Key").toString();
	
	        String vendor_id = param.get("vendor_id").toString();
	        String eventName = param.get("eventName").toString();
	        int threadNum = Integer.parseInt(param.get("threadNum").toString());
	        ThreadPoolExecutor nugnesExecutor = (ThreadPoolExecutor) param.get("nugnesExecutor");
	        ApiDataFileUtils fileUtils = (ApiDataFileUtils) param.get("fileUtils");
	        
	        //获取当前vendor下面的所有BoutiqueSeasonCode
			logger.info("xmagSynstockAllMappingService param :" +vendor_id);
			List<Map<String, Object>> code = seasonMappingService.getBoutiqueSeasonCode(vendor_id);
			logger.info("xmagSynstockAllMappingService result :" + new Gson().toJson(code) );
			if (null != code && 0 < code.size()){
				for (Map<String, Object> map : code) {
					String appendUrl = url+"?DBContext="+DBContext + "&typeSync="+typeSync +"&Key="+Key+
							"&SeasonCode="+map.get("boutique_season_code").toString();
					String json = "";
			    	//获取数据
		    		IGetPostRequest requestGet = new GetPostRequestService();
		    		logger.info("job XmagAllStock  Call the interface to get the data    url:"+appendUrl);
		    		json = requestGet.requestMethod(GetPostRequestService.HTTP_GET, appendUrl, null);
		    		logger.info("job XmagAllStock result:"+json);
			    	//如果请求数据不为空，放入MQ队列
			    	JSONObject jsonOjbect = JSONObject.parseObject(json);
			    	if (StringUtils.isNotBlank(json)){
			    		fileUtils.bakPendingFile("SeasonCode" + SeasonCode + "_typeSync" + typeSync, json);
			    		List<Map<String, Object>> list = (List<Map<String, Object>>) jsonOjbect.get("listStockData");
			    		logger.info("XmagAllStock list size:"+list.size());
			    		if(list !=null && list.size() > 0 ){
			    			int index = 1;
			    			for(Map<String, Object> product :list ){
		                        Map<String,Object> mqDataMap = new HashMap<String,Object>();
		                        mqDataMap.put("product", product);
		                        mqDataMap.put("vendor_id", vendor_id);
		                        logger.info("Push Index" + index);
		                        String src = new Gson().toJson(mqDataMap);
		                        logger.info("Push data" + src);
		                        // 映射数据 封装VO
		                        logger.info("EdsAllUpdateByStockControllerExecute,mapping,start,stockMap:"+new Gson().toJson(mqDataMap)+",eventName:"+eventName);
		                        StockOption stockOption = iStockMapping.mapping(mqDataMap);
		                        logger.info("EdsAllUpdateByStockControllerExecute,mapping,end,stockMap:"+new Gson().toJson(mqDataMap)+",stockOption:"+new Gson().toJson(stockOption)+",eventName:"+eventName);

		                        // 线程池
		                        logger.info("EdsAllUpdateByStockControllerExecute,execute,startDate:"+ DateUtils.getStrDate(new Date())+",stockMap:"+new Gson().toJson(mqDataMap)+",stockOption:"+new Gson().toJson(stockOption)+",eventName:"+eventName);
		                        CommonThreadPool.execute(eventName,nugnesExecutor,threadNum,new UpdateStockThread(stockOption,fileUtils));
		                        logger.info("EdsAllUpdateByStockControllerExecute,execute,endDate:"+ DateUtils.getStrDate(new Date())+",stockMap:"+new Gson().toJson(mqDataMap)+",stockOption:"+new Gson().toJson(stockOption)+",eventName:"+eventName);
		                        index++;
		            		}
			    		}
			    	}else{
			    		logger.info("job XmagAllStock result null ");
			    	}
			    	appendUrl = "";
				}
				mapUtils.putData("status", StatusType.SUCCESS);
			}else{
				mapUtils.putData("status", StatusType.FAILURE);
				logger.info("job XmagAllStock seasonMappingService result null ");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("xmagSynstockAllExceptions : " + ExceptionUtils.getExceptionDetail(e));
			mapUtils.putData("status", StatusType.FAILURE).putData("info", "error message : " + e.getMessage());
		}
        
        logger.info("------------------------------------------end xmagSynstockAll.populateResult mapUtils : " + new Gson().toJson(mapUtils));
        return mapUtils.getMap();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// xmag
        ThreadPoolExecutor nugnesExecutor =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        Map<String,Object> xmag_all_stock = new HashMap<>();
        xmag_all_stock.put("url","http://net13server2.net/TheApartmentAPI/MyApi/Productslist/GetAllStockForSync");
        xmag_all_stock.put("DBContext","Default");
        xmag_all_stock.put("SeasonCode","080");
        xmag_all_stock.put("typeSync","api");
        xmag_all_stock.put("Key","RTs7g634sH");
       
        xmag_all_stock.put("nugnesExecutor",nugnesExecutor);
        xmag_all_stock.put("vendor_id","19");
        xmag_all_stock.put("threadNum","5");
        xmag_all_stock.put("eventName","Xmag全量更新库存");
        xmag_all_stock.put("fileUtils",new ApiDataFileUtils("xmag","xmag全量更新库存"));
        
        // put data
        paramsMap = new HashMap<>();
        paramsMap.put("xmag_all_stock",xmag_all_stock);

	}

}
