package com.intramirror.web.controller.xmag;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.intramirror.main.api.service.SeasonMappingService;
import com.intramirror.web.mapping.api.IStockMapping;
import com.intramirror.web.mapping.vo.StockOption;
import com.intramirror.web.thread.CommonThreadPool;
import com.intramirror.web.thread.UpdateStockThread;
import com.intramirror.web.util.ApiDataFileUtils;
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
import pk.shoplus.service.request.api.IGetPostRequest;
import pk.shoplus.service.request.impl.GetPostRequestService;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.MapUtils;

/**
 * @author dingyifan
 */
@Controller
@RequestMapping("/xmag_stock")
public class XmagSynAllStockController implements InitializingBean {

	// logger
	private final Logger logger = Logger.getLogger(XmagSynAllStockController.class);

	public static ProductEDSManagement productEDSManagement = new ProductEDSManagement();

	@Autowired
    private SeasonMappingService seasonMappingService;

    @Resource(name = "xmagSynAllStockMapping")
    private IStockMapping iStockMapping;

	private Map<String,Object> paramsMap;
	
	@RequestMapping("/syn_stock")
	@ResponseBody
	public Map<String, Object> syn_stock(@Param(value = "name") String name) {
		logger.info("XmagSynAllStockControllerSyn_stock,inputParams,name:"+name);
		MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());
		
		 // check name
        if(StringUtils.isBlank(name)) {
            return mapUtils.putData("status",StatusType.FAILURE).putData("info","name is null !!!").getMap();
        }

        try{
        	// get init params
	        Map<String,Object> param = (Map<String, Object>) paramsMap.get(name);
	        logger.info("XmagSynAllStockControllerSyn_stock,getInitParam:"+JSONObject.toJSONString(param));
	        String url = param.get("url").toString();
	        String DBContext = param.get("DBContext").toString();
	        String SeasonCode = param.get("SeasonCode").toString();
	        String typeSync = param.get("typeSync").toString();
	        String Key = param.get("Key").toString();
	
	        String vendor_id = param.get("vendor_id").toString();
	        String eventName = param.get("eventName").toString();
	        int threadNum = Integer.parseInt(param.get("threadNum").toString());
	        ThreadPoolExecutor nugnesExecutor = (ThreadPoolExecutor) param.get("executor");
	        ApiDataFileUtils fileUtils = (ApiDataFileUtils) param.get("fileUtils");

			logger.info("XmagSynAllStockControllerSyn_stock,getBoutiqueSeasonCode,vendor_id:" +vendor_id);
			List<Map<String, Object>> code = seasonMappingService.getBoutiqueSeasonCode("20");
			logger.info("XmagSynAllStockControllerSyn_stock,getBoutiqueSeasonCode,code:"+JSONObject.toJSONString(code)+",vendor_id:" +vendor_id);

			if (null != code && 0 < code.size()){
				for (Map<String, Object> map : code) {
					String appendUrl = url+"?DBContext="+DBContext + "&typeSync="+typeSync +"&Key="+Key+
							"&SeasonCode="+map.get("boutique_season_code").toString();

					logger.info("XmagSynAllStockControllerSyn_stock,requestMethod,start,appendUrl:"+appendUrl);
		    		IGetPostRequest requestGet = new GetPostRequestService();
		    		String json = requestGet.requestMethod(GetPostRequestService.HTTP_GET, appendUrl, null);
					logger.info("XmagSynAllStockControllerSyn_stock,requestMethod,end,appendUrl:"+appendUrl+",json:"+json);

			    	JSONObject jsonOjbect = JSONObject.parseObject(json);
			    	if (StringUtils.isNotBlank(json)){
			    		fileUtils.bakPendingFile("SeasonCode" + SeasonCode + "_typeSync" + typeSync, json);
			    		List<Map<String, Object>> stockList = (List<Map<String, Object>>) jsonOjbect.get("listStockData");
			    		logger.info("XmagSynAllStockControllerSyn_stock,conventJsonObjectToList,stockListSize:"+stockList.size()+",stockList:"+JSONObject.toJSONString(stockList));

			    		if(stockList !=null && stockList.size() > 0 ){

			    			for(Map<String, Object> product :stockList ){
		                        Map<String,Object> mqDataMap = new HashMap<String,Object>();
		                        mqDataMap.put("product", product);
		                        mqDataMap.put("vendor_id", vendor_id);

		                        logger.info("XmagSynAllStockControllerSyn_stock,mapping,start,stockMap:"+new Gson().toJson(mqDataMap)+",eventName:"+eventName);
		                        StockOption stockOption = iStockMapping.mapping(mqDataMap);
		                        logger.info("XmagSynAllStockControllerSyn_stock,mapping,end,stockMap:"+new Gson().toJson(mqDataMap)+",stockOption:"+new Gson().toJson(stockOption)+",eventName:"+eventName);

		                        logger.info("XmagSynAllStockControllerSyn_stock,execute,stockMap:"+new Gson().toJson(mqDataMap)+",stockOption:"+new Gson().toJson(stockOption)+",eventName:"+eventName);
		                        CommonThreadPool.execute(eventName,nugnesExecutor,threadNum,new UpdateStockThread(stockOption,fileUtils,mqDataMap));
		                        logger.info("XmagSynAllStockControllerSyn_stock,execute,stockMap:"+new Gson().toJson(mqDataMap)+",stockOption:"+new Gson().toJson(stockOption)+",eventName:"+eventName);
		            		}
			    		}
			    	}
				}
				mapUtils.putData("status", StatusType.SUCCESS);
			}else{
				mapUtils.putData("status", StatusType.FAILURE);
				logger.info("XmagSynAllStockControllerSyn_stock,seasonMappingIsNull,param:"+JSONObject.toJSONString(param));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("XmagSynAllStockControllerSyn_stock,errorMessage:" + ExceptionUtils.getExceptionDetail(e));
			mapUtils.putData("status", StatusType.FAILURE).putData("info", "error message : " + e.getMessage());
		}
        
        logger.info("XmagSynAllStockControllerSyn_stock,outputParams,mapUtils:" + new Gson().toJson(mapUtils));
        return mapUtils.getMap();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		/*// xmag
        ThreadPoolExecutor nugnesExecutor =;
        Map<String,Object> xmag_all_stock = new HashMap<>();
        xmag_all_stock.put("url","http://net13server2.net/TheApartmentAPI/MyApi/Productslist/GetAllStockForSync");
        xmag_all_stock.put("DBContext","Default");
        xmag_all_stock.put("SeasonCode","080");
        xmag_all_stock.put("typeSync","api");
        xmag_all_stock.put("Key","RTs7g634sH");
        xmag_all_stock.put("nugnesExecutor",nugnesExecutor);
        xmag_all_stock.put("vendor_id","20"); // 20
        xmag_all_stock.put("threadNum","5");
        xmag_all_stock.put("vendorName","The Apartment Cosenza");
        xmag_all_stock.put("eventName","apartment_stock_all_update");
        xmag_all_stock.put("fileUtils",new ApiDataFileUtils("xmag","apartment_stock_all_update"));*/
        
        paramsMap = new HashMap<>();
        // apartment
        paramsMap.put("apartment_stock_all_update",new MapUtils(new HashMap<String, Object>())
				.putData("url","http://net13server2.net/TheApartmentAPI/MyApi/Productslist/GetAllStockForSync")
				.putData("DBContext","Default")
				.putData("SeasonCode","080")
				.putData("typeSync","api")
				.putData("Key","RTs7g634sH")
				.putData("executor",(ThreadPoolExecutor) Executors.newCachedThreadPool())
				.putData("vendor_id","20")
				.putData("threadNum","5")
				.putData("eventName","apartment_stock_all_update")
				.putData("fileUtils",new ApiDataFileUtils("apartment","stock_all_update")).getMap());

        // dolci
		paramsMap.put("dolci_stock_all_update",new MapUtils(new HashMap<String, Object>())
				.putData("url","http://net13server2.net/dolcitrameAPI/MyApi/Productslist/GetAllStockForSync")
				.putData("DBContext","Default")
				.putData("SeasonCode","080")
				.putData("typeSync","api")
				.putData("Key","FwkQO9jAYJ")
				.putData("executor",(ThreadPoolExecutor) Executors.newCachedThreadPool())
				.putData("vendor_id","37")
				.putData("threadNum","5")
				.putData("eventName","dolci_stock_all_update")
				.putData("fileUtils",new ApiDataFileUtils("dolci","stock_all_update")).getMap());

		// mimma
		paramsMap.put("mimma_stock_all_update",new MapUtils(new HashMap<String, Object>())
				.putData("url","http://net13server3.it/mimmaninniapi/MyApi/Productslist/GetAllStockForSync")
				.putData("DBContext","Default")
				.putData("SeasonCode","080")
				.putData("typeSync","api")
				.putData("Key","G4jKb0sFid")
				.putData("executor",(ThreadPoolExecutor) Executors.newCachedThreadPool())
				.putData("vendor_id","12")
				.putData("threadNum","5")
				.putData("eventName","mimma_stock_all_update")
				.putData("fileUtils",new ApiDataFileUtils("mimma","stock_all_update")).getMap());

		// mimma2
		paramsMap.put("mimma2_stock_all_update",new MapUtils(new HashMap<String, Object>())
				.putData("url","http://net13server3.it/mimmaninniapi/MyApi/Productslist/GetAllStockForSync")
				.putData("DBContext","Domini2")
				.putData("SeasonCode","080")
				.putData("typeSync","api")
				.putData("Key","G4jKb0sFid")
				.putData("executor",(ThreadPoolExecutor) Executors.newCachedThreadPool())
				.putData("vendor_id","39")
				.putData("threadNum","5")
				.putData("eventName","mimma2_stock_all_update")
				.putData("fileUtils",new ApiDataFileUtils("mimma2","stock_all_update")).getMap());

		// spinnaker
		paramsMap.put("spinnaker_stock_all_update",new MapUtils(new HashMap<String, Object>())
				.putData("url","http://net13server.net/spinnakerapi/MyApi/Productslist/GetAllStockForSync")
				.putData("DBContext","Default")
				.putData("SeasonCode","080")
				.putData("typeSync","api")
				.putData("Key","0mFLb4VtKS")
				.putData("executor",(ThreadPoolExecutor) Executors.newCachedThreadPool())
				.putData("vendor_id","38")
				.putData("threadNum","5")
				.putData("eventName","spinnaker_stock_all_update")
				.putData("fileUtils",new ApiDataFileUtils("spinnaker","stock_all_update")).getMap());

		// spinnaker Sanremo
		paramsMap.put("spinnakerSanremo_stock_all_update",new MapUtils(new HashMap<String, Object>())
				.putData("url","http://net13server.net/spinnakerapi/MyApi/Productslist/GetAllStockForSync")
				.putData("DBContext","Sanremo")
				.putData("SeasonCode","080")
				.putData("typeSync","api")
				.putData("Key","0mFLb4VtKS")
				.putData("executor",(ThreadPoolExecutor) Executors.newCachedThreadPool())
				.putData("vendor_id","40")
				.putData("threadNum","5")
				.putData("eventName","spinnakerSanremo_stock_all_update")
				.putData("fileUtils",new ApiDataFileUtils("spinnakerSanremo","stock_all_update")).getMap());

		// spinnaker Portofino
		paramsMap.put("spinnakerPortofino_stock_all_update",new MapUtils(new HashMap<String, Object>())
				.putData("url","http://net13server.net/spinnakerapi/MyApi/Productslist/GetAllStockForSync")
				.putData("DBContext","Portofino")
				.putData("SeasonCode","080")
				.putData("typeSync","api")
				.putData("Key","0mFLb4VtKS")
				.putData("executor",(ThreadPoolExecutor) Executors.newCachedThreadPool())
				.putData("vendor_id","41")
				.putData("threadNum","5")
				.putData("eventName","spinnakerPortofino_stock_all_update")
				.putData("fileUtils",new ApiDataFileUtils("spinnakerPortofino","stock_all_update")).getMap());

		// statu
		paramsMap.put("satu_stock_all_update",new MapUtils(new HashMap<String, Object>())
				.putData("url","http://net13server2.net/SatuAPI/MyApi/Productslist/GetAllStockForSync")
				.putData("DBContext","Default")
				.putData("SeasonCode","080")
				.putData("typeSync","api")
				.putData("Key","7R3FbSYqQo")
				.putData("executor",(ThreadPoolExecutor) Executors.newCachedThreadPool())
				.putData("vendor_id","46")
				.putData("threadNum","5")
				.putData("eventName","satu_stock_all_update")
				.putData("fileUtils",new ApiDataFileUtils("satu","stock_all_update")).getMap());

		// suite
		paramsMap.put("suite_stock_all_update",new MapUtils(new HashMap<String, Object>())
				.putData("url","http://www.net13server3.it/suitapi/MyApi/Productslist/GetAllStockForSync")
				.putData("DBContext","Default")
				.putData("SeasonCode","080")
				.putData("typeSync","api")
				.putData("Key","nLRqWwYf3t")
				.putData("executor",(ThreadPoolExecutor) Executors.newCachedThreadPool())
				.putData("vendor_id","47")
				.putData("threadNum","5")
				.putData("eventName","suite_stock_all_update")
				.putData("fileUtils",new ApiDataFileUtils("suite","stock_all_update")).getMap());

		// Gallery
		paramsMap.put("gallery_stock_all_update",new MapUtils(new HashMap<String, Object>())
				.putData("url","http://www.net13server3.it/galleryapi/MyApi/Productslist/GetAllStockForSync")
				.putData("DBContext","Default")
				.putData("SeasonCode","080")
				.putData("typeSync","api")
				.putData("Key","8Cx6IGcfrc")
				.putData("executor",(ThreadPoolExecutor) Executors.newCachedThreadPool())
				.putData("vendor_id","48")
				.putData("threadNum","5")
				.putData("eventName","gallery_stock_all_update")
				.putData("fileUtils",new ApiDataFileUtils("gallery","stock_all_update")).getMap());
	}

}
