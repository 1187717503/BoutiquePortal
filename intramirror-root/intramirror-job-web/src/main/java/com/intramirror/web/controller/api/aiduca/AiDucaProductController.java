package com.intramirror.web.controller.api.aiduca;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.product.api.service.stock.IUpdateStockService;
import com.intramirror.web.mapping.api.IStockMapping;
import com.intramirror.web.mapping.impl.aiduca.AiDucaSynAllStockMapping;
import com.intramirror.web.mapping.impl.aiduca.AiDucaSynProductMapping;
import com.intramirror.web.mapping.vo.StockOption;
import com.intramirror.web.thread.CommonThreadPool;
import com.intramirror.web.thread.UpdateProductThread;
import com.intramirror.web.thread.UpdateStockThread;
import com.intramirror.web.util.ApiDataFileUtils;
import com.intramirror.web.util.GetPostRequestUtil;
import com.intramirror.web.util.RandomUtils;
import difflib.DiffRow;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pk.shoplus.common.Contants;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.FileUtil;

@Controller
@RequestMapping("/alduca_product")
public class AiDucaProductController implements InitializingBean{

	// logger
    private static Logger logger = Logger.getLogger(AiDucaProductController.class);

    // product mapping
	@Autowired
	private AiDucaSynProductMapping aiDucaSynProductMapping;

	// stock mapping
	@Autowired
	private AiDucaSynAllStockMapping aiDucaSynAllStockMapping;

	// stock mapping api
    @Resource(name = "edsUpdateByStockMapping")
    private IStockMapping iStockMapping;
    
    // init params
    private Map<String,Object> paramsMap;

	@Resource(name = "updateStockService")
	private IUpdateStockService iUpdateStockService;
    
    @RequestMapping(value = "/syn_product",method = RequestMethod.GET)
    @ResponseBody
    public ResultMessage syn_product(@Param(value = "name")String name){
    	logger.info("AiDucaProductControllerSyn_product,inputParams,name:"+name);
        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
            Map<String,Object> param = (Map<String, Object>) paramsMap.get(name);
        	if(param != null && param.size() > 0 ){

        		// get param
        		ThreadPoolExecutor aiducaExecutor = (ThreadPoolExecutor) param.get("aiducaProductExecutor");
        	    String eventName = param.get("eventName").toString();
        	    ProductEDSManagement productEDSManagement = new ProductEDSManagement();
                String currentDate = DateUtils.getStrDate(new Date(),"yyyyMMddHHmmss");
                ApiDataFileUtils fileUtils = (ApiDataFileUtils) param.get("fileUtils");

                // invoke api
        		GetPostRequestUtil requestGet = new GetPostRequestUtil();
                logger.info("AiDucaProductControllerSyn_product,requestMethodType,param:"+JSONObject.toJSONString(param));
                String json = requestGet.requestMethodType(GetPostRequestUtil.HTTP_GET,param.get("url").toString(),null);
				logger.info("AiDucaProductControllerSyn_product,requestMethodType,param:"+JSONObject.toJSONString(param)+",json:"+json);

				if(StringUtils.isNotBlank(json)) {
                	List<Map<String, Object>> productList = (List<Map<String, Object>>) JSONObject.parse(json);
                	logger.info("AiDucaProductControllerSyn_product,convertList,productList:"+JSONObject.toJSONString(productList));

	                String originProductPath = Contants.aiduca_file_path + Contants.aiduca_origin_product_all + Contants.aiduca_file_type;
	                String revisedProductPath = Contants.aiduca_file_path + Contants.aiduca_revised_product_all + Contants.aiduca_file_type;
	                
	                if(!FileUtil.fileExists(originProductPath) || eventName.contains("all")) {
	                	if(!eventName.contains("all")) {
							logger.info("AiDucaProductControllerSyn_product,first,originProductPath:"+originProductPath);
							FileUtil.createFileByType(Contants.aiduca_file_path,Contants.aiduca_origin_product_all + Contants.aiduca_file_type,converString(productList));
						}
	                	if(productList !=null && productList.size() > 0 ){

	                		fileUtils.bakPendingFile("aiduca-"+currentDate,json);
							logger.info("AiDucaProductControllerSyn_product,bakPendingFile,listSize:"+productList.size());
							int flag = 0;
							for(Map<String, Object> productInfo :productList ){
								flag ++;
								Map<String,Object> mqDataMap = new HashMap<String,Object>();
								mqDataMap.put("product", productInfo.toString());
								mqDataMap.put("store_code", param.get("store_code").toString());
								mqDataMap.put("vendor_id", param.get("vendor_id").toString());
								logger.info("AiDucaProductControllerSyn_product,mqDataMap:"+JSONObject.toJSONString(mqDataMap));

								ProductEDSManagement.ProductOptions productOptions = aiDucaSynProductMapping.mapping(mqDataMap);
								ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
								vendorOptions.setVendorId(Long.parseLong(param.get("vendor_id").toString()));
								logger.info("AiDucaProductControllerSyn_product,initParam,productOptions:"+JSONObject.toJSONString(productOptions)+",vendorOptions:"+JSONObject.toJSONString(vendorOptions)+",eventName:"+eventName+",param:"+JSONObject.toJSONString(param)+",mqDataMap:"+JSONObject.toJSONString(mqDataMap));

								logger.info("AiDucaProductControllerSyn_product,execute,start,productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName+",mqDataMap:"+JSONObject.toJSONString(mqDataMap)+",param:"+JSONObject.toJSONString(param)+",index:"+flag);
								CommonThreadPool.execute(eventName,aiducaExecutor,Integer.parseInt(param.get("threadNum").toString()),new UpdateProductThread(productOptions,vendorOptions,fileUtils,mqDataMap));
								logger.info("AiDucaProductControllerSyn_product,execute,end,productOptions::"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName+",mqDataMap:"+JSONObject.toJSONString(mqDataMap)+",param:"+JSONObject.toJSONString(param)+",index:"+flag);
							}

							if(eventName.contains("all")) {
								logger.info("AiDucaProductControllerSyn_product,zeroClearing,flag:"+flag);
								if(eventName.equals("alduca_product_all_update") && flag > 100 ) {
									logger.info("AiDucaProductControllerSyn_product,zeroClearing,start,vendor_id:"+param.get("vendor_id").toString());
									iUpdateStockService.zeroClearing(Long.parseLong(param.get("vendor_id").toString()));
									logger.info("AiDucaProductControllerSyn_product,zeroClearing,end,vendor_id:"+param.get("vendor_id").toString());
								}
							}

                	  }


	                } else {
		                FileUtil.createFileByType(Contants.aiduca_file_path,Contants.aiduca_revised_product_all+ Contants.aiduca_file_type,converString(productList));
		
		                List<DiffRow> diffRows = FileUtil.CompareTxtByType(originProductPath,revisedProductPath);
		                logger.info("AiDucaProductControllerSyn_product,CompareTxtByType,diffRowsSize: "+diffRows.size());

		                for(DiffRow diffRow : diffRows) {
		                    DiffRow.Tag tag = diffRow.getTag();
		                    if(tag == DiffRow.Tag.INSERT || tag == DiffRow.Tag.CHANGE) {
		                        logger.info("AiDucaProductControllerSyn_product,changeDiff:" + new Gson().toJson(diffRow));

	                            Map<String,Object> mqDataMap = new HashMap<String,Object>();
	                            //读取出来会自动带换行符,需要转换
	                            mqDataMap.put("product", diffRow.getNewLine().replace("<br>", ""));
	                            mqDataMap.put("store_code", param.get("store_code").toString());
	                            mqDataMap.put("vendor_id", param.get("vendor_id").toString());
	                            
	                            // 放入线程池
	                			logger.info("jAiDucaProductControllerSyn_product,mqDataMap:"+JSONObject.toJSONString(mqDataMap));
	                            
	                            // 映射数据 封装VO
	                            ProductEDSManagement.ProductOptions productOptions = aiDucaSynProductMapping.mapping(mqDataMap);
	                            ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
	                            vendorOptions.setVendorId(Long.parseLong(param.get("vendor_id").toString()));
	                            logger.info("AiDucaProductControllerSyn_product,initParam,productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName+",param:"+JSONObject.toJSONString(param)+",mqDataMap:"+JSONObject.toJSONString(mqDataMap));

	                            // 线程池
	                            logger.info("AiDucaProductControllerSyn_product,execute,start,productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName+",mqDataMap:"+JSONObject.toJSONString(mqDataMap)+",param:"+JSONObject.toJSONString(param));
	                            CommonThreadPool.execute(eventName,aiducaExecutor,Integer.parseInt(param.get("threadNum").toString()),new UpdateProductThread(productOptions,vendorOptions,fileUtils,mqDataMap));
	                            logger.info("AiDucaProductControllerSyn_product,execute,end,productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName+",mqDataMap:"+JSONObject.toJSONString(mqDataMap)+",param:"+JSONObject.toJSONString(param));
		                    } 
		                }
		                
		                String originContentAll = FileUtil.readFile(originProductPath);
		                FileUtil.createFileByType(Contants.aiduca_file_path,"bak_"+Contants.aiduca_origin_product_all + Contants.aiduca_file_type,originContentAll);
		                FileUtil.createFileByType(Contants.aiduca_file_path,"bak_"+Contants.aiduca_revised_product_all  + Contants.aiduca_file_type,converString(productList));
		                FileUtil.createFileByType(Contants.aiduca_file_path,Contants.aiduca_origin_product_all + Contants.aiduca_file_type,converString(productList));
	                }
                }else{
					logger.info("AiDucaProductControllerSyn_product,paramIsNull,name:"+name);
                     resultMessage.errorStatus().addMsg("job aiDuca getAllSKU 请求接口获取的商品对象   返回数据为空    url:"+param.get("url").toString());
                     return resultMessage;
                }
            	
                resultMessage.successStatus().addMsg("SUCCESS");
        	}
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("AiDucaProductControllerSyn_product,errorMessage:"+ExceptionUtils.getExceptionDetail(e));
            resultMessage.errorStatus().addMsg("error message : " + e.getMessage());
        }
        return resultMessage;
    }

    public String converString(List<Map<String, Object>> list){
    	 StringBuffer stringBuffer = new StringBuffer();
    	 
    	 if(list != null && list.size() > 0 ){
    		 for(Map<String, Object> info : list){
    			 stringBuffer.append(info.toString()).append("\n");
    		 }
    	 }
    	return stringBuffer.toString();
    }

	@RequestMapping(value = "/syn_stock",method = RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> syn_stock(@Param(value = "name")String name){
    	logger.info("AlducaProductControllerSyn_stock,inputParams,name:"+name);
		try {

			// 获取基础配置
			Map<String,Object> baseMap = (Map<String, Object>) paramsMap.get(name);
			logger.info("AlducaProductControllerSyn_stock,getMap,baseMap:"+JSONObject.toJSONString(baseMap));

			// 获取基础配置中数据
			String url = baseMap.get("url").toString();
			Long vendor_id = Long.parseLong(baseMap.get("vendor_id").toString());
			int thread_num = Integer.parseInt(baseMap.get("threadNum").toString());
			String event_name = baseMap.get("eventName").toString();
			ApiDataFileUtils apiDataFileUtils = (ApiDataFileUtils) baseMap.get("fileUtils");
			ThreadPoolExecutor executor = (ThreadPoolExecutor) baseMap.get("aiducaStockExecutor");

			// 调用Alduca接口
			GetPostRequestUtil requestGET = new GetPostRequestUtil();
			logger.info("AlducaProductControllerSyn_stock,start,requestMethodType,url:"+url);
			String responseBody = requestGET.requestMethodType(GetPostRequestUtil.HTTP_GET,url,null);
			logger.info("AlducaProductControllerSyn_stock,end,requestMethodType,url:"+url+",responseBody:"+responseBody);
			apiDataFileUtils.bakPendingFile(RandomUtils.getDateRandom(),responseBody);

			// 转换格式
			List<Map<String, Object>> stockObjList = (List<Map<String, Object>>) JSONObject.parse(responseBody);
			logger.info("AlducaProductControllerSyn_stock,JsonParse,stockObjList.Size:"+stockObjList.size()+",stockObjList:"+JSONObject.toJSONString(stockObjList));

			// 请求数据
			String originProductPath = Contants.aiduca_file_path + Contants.aiduca_origin_stock_all + Contants.aiduca_file_type;
			String revisedProductPath = Contants.aiduca_file_path + Contants.aiduca_revised_stock_all + Contants.aiduca_file_type;
			if(event_name.contains("all")) {
				this.handleStock(stockObjList,vendor_id,event_name,executor,thread_num,apiDataFileUtils,false);

				logger.info("AiDucaProductControllerSyn_product,zeroClearing,flag:"+stockObjList.size());
				if(stockObjList.size() > 100 ) {
					logger.info("AiDucaProductControllerSyn_product,zeroClearing,start,vendor_id:"+vendor_id+",size:"+stockObjList.size());
					iUpdateStockService.zeroClearing(vendor_id);
					logger.info("AiDucaProductControllerSyn_product,zeroClearing,end,vendor_id:"+vendor_id+",size:"+stockObjList.size());
				}
			} else if(!FileUtil.fileExists(originProductPath)) {
				FileUtil.createFileByType(Contants.aiduca_file_path,Contants.aiduca_origin_stock_all + Contants.aiduca_file_type,converString(stockObjList));
			} else {
				FileUtil.createFileByType(Contants.aiduca_file_path,Contants.aiduca_revised_stock_all+ Contants.aiduca_file_type,converString(stockObjList));

				List<DiffRow> diffRows = FileUtil.CompareTxtByType(originProductPath,revisedProductPath);
				logger.info("AlducaProductControllerSyn_stock,CompareTxtByType,diffRowsSize:"+diffRows.size()+",diffRows:"+JSONObject.toJSONString(diffRows));

				List<Map<String,Object>> changeData = new ArrayList<>();
				List<Map<String,Object>> deleteData = new ArrayList<>();
				for(DiffRow diffRow : diffRows) {
					DiffRow.Tag tag = diffRow.getTag();
					if(tag == DiffRow.Tag.INSERT || tag == DiffRow.Tag.CHANGE) {

						logger.info("AlducaProductControllerSyn_stock,insertandchange,diffRow:"+JSONObject.toJSONString(diffRow));
						String stockMapStr = diffRow.getNewLine().replace("<br>","");

						if(StringUtils.isNotBlank(stockMapStr)) {
							Map<String,Object> stockMap = JSONObject.parseObject(stockMapStr);
							changeData.add(stockMap);
						}
					} else if(tag == DiffRow.Tag.DELETE){

						logger.info("AlducaProductControllerSyn_stock,delete,diffRow:"+JSONObject.toJSONString(diffRow));
						String stockMapStr = diffRow.getOldLine().replace("<br>","");

						if(StringUtils.isNotBlank(stockMapStr)) {
							Map<String,Object> stockMap = JSONObject.parseObject(stockMapStr);
							deleteData.add(stockMap);
						}
					}
				}
				this.handleStock(changeData,vendor_id,event_name,executor,thread_num,apiDataFileUtils,false);
				this.handleStock(deleteData,vendor_id,event_name,executor,thread_num,apiDataFileUtils,true);
				FileUtil.createFileByType(Contants.aiduca_file_path,Contants.aiduca_origin_stock_all + Contants.aiduca_file_type,converString(stockObjList));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("AlducaProductControllerSyn_stock,errorMessage:"+ExceptionUtils.getExceptionDetail(e));
		}
    	return null;
	}

	private void handleStock(List<Map<String, Object>> stockObjList, Long vendor_id, String eventName, ThreadPoolExecutor executor, int threadNum, ApiDataFileUtils fileUtils,boolean stockZero){
		if(stockObjList == null || stockObjList.size() == 0) {
			return;
		}

		for(int i = 0,len=stockObjList.size();i<len;i++) {
			Map<String,Object> stockObj = stockObjList.get(i);

			Map<String,Object> mqDataMap = new HashMap<String,Object>();
			mqDataMap.put("product", stockObj);
			mqDataMap.put("vendor_id", vendor_id);

			logger.info("AlducaProductController,start,mapping,mqDataMap:"+JSONObject.toJSONString(mqDataMap)+",i:"+i);
			StockOption stockOption = aiDucaSynAllStockMapping.mapping(mqDataMap);
			if(stockZero) {stockOption.setQuantity("0");}
			logger.info("AlducaProductController,end,mapping,mqDataMap:"+JSONObject.toJSONString(mqDataMap)+",i:"+i+",stockOption:"+JSONObject.toJSONString(stockOption));

			logger.info("AlducaProductControllerSyn_stock,execute,mqDataMap:"+JSONObject.toJSONString(mqDataMap)+",i:"+i+",stockOption:"+JSONObject.toJSONString(stockOption)+",eventName:"+eventName);
			CommonThreadPool.execute(eventName,executor,threadNum,new UpdateStockThread(stockOption,fileUtils,mqDataMap));
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
        // alduca_product_delta_update
        ThreadPoolExecutor alducaDeltaProductExecutor =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        Map<String,Object> alduca_product_delta_update = new HashMap<>();
		alduca_product_delta_update.put("url","http://rest.alducadaosta.com/api/Catalog/Sku4Platform?Username=IntraMirror&Password=%2B%2BInt%3DMir%2B%2B");
		alduca_product_delta_update.put("vendor_id","22");
		alduca_product_delta_update.put("store_code","AIDUCA");
		alduca_product_delta_update.put("threadNum","5");
		alduca_product_delta_update.put("vendorName","Al Duca Daosta");
		alduca_product_delta_update.put("aiducaProductExecutor",alducaDeltaProductExecutor);
		alduca_product_delta_update.put("eventName","alduca_product_delta_update");
		alduca_product_delta_update.put("fileUtils",new ApiDataFileUtils("alduca","alduca_product_delta_update"));

		// alduca_product_all_update
		ThreadPoolExecutor alducaAllProductExecutor =(ThreadPoolExecutor) Executors.newCachedThreadPool();
		Map<String,Object> alduca_product_all_update = new HashMap<>();
		alduca_product_all_update.put("url","http://rest.alducadaosta.com/api/Catalog/Sku4Platform?Username=IntraMirror&Password=%2B%2BInt%3DMir%2B%2B");
		alduca_product_all_update.put("vendor_id","22");
		alduca_product_all_update.put("store_code","AIDUCA");
		alduca_product_all_update.put("threadNum","5");
		alduca_product_all_update.put("vendorName","Al Duca Daosta");
		alduca_product_all_update.put("aiducaProductExecutor",alducaAllProductExecutor);
		alduca_product_all_update.put("eventName","alduca_product_all_update");
		alduca_product_all_update.put("fileUtils",new ApiDataFileUtils("alduca","alduca_product_all_update"));

        // alduca_stock_all_update
        ThreadPoolExecutor alducaAllStockExecutor =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        Map<String,Object> alduca_stock_all_update = new HashMap<>();
		alduca_stock_all_update.put("url","http://rest.alducadaosta.com/api/stock/Stock4sku/all?Username=IntraMirror&Password=%2B%2BInt%3DMir%2B%2B");
		alduca_stock_all_update.put("vendor_id","22");
		alduca_stock_all_update.put("store_code","AIDUCA");
		alduca_stock_all_update.put("threadNum","5");
		alduca_stock_all_update.put("vendorName","Al Duca Daosta");
		alduca_stock_all_update.put("aiducaStockExecutor",alducaAllStockExecutor);
		alduca_stock_all_update.put("eventName","alduca_stock_all_update");
		alduca_stock_all_update.put("fileUtils",new ApiDataFileUtils("alduca","stock_all_update"));

		// alduca_stock_all_update
		ThreadPoolExecutor alducaDeltaStockExecutor =(ThreadPoolExecutor) Executors.newCachedThreadPool();
		Map<String,Object> alduca_stock_delta_update = new HashMap<>();
		alduca_stock_delta_update.put("url","http://rest.alducadaosta.com/api/stock/Stock4sku/all?Username=IntraMirror&Password=%2B%2BInt%3DMir%2B%2B");
		alduca_stock_delta_update.put("vendor_id","22");
		alduca_stock_delta_update.put("store_code","AIDUCA");
		alduca_stock_delta_update.put("threadNum","5");
		alduca_stock_delta_update.put("vendorName","Al Duca Daosta");
		alduca_stock_delta_update.put("aiducaStockExecutor",alducaDeltaStockExecutor);
		alduca_stock_delta_update.put("eventName","alduca_stock_delta_update");
		alduca_stock_delta_update.put("fileUtils",new ApiDataFileUtils("alduca","stock_delta_update"));

        // put data
        paramsMap = new HashMap<>();
		paramsMap.put("alduca_product_all_update",alduca_product_all_update);
		paramsMap.put("alduca_product_delta_update",alduca_product_delta_update);
		paramsMap.put("alduca_stock_all_update",alduca_stock_all_update);
		paramsMap.put("alduca_stock_delta_update",alduca_stock_delta_update);

	}
}
