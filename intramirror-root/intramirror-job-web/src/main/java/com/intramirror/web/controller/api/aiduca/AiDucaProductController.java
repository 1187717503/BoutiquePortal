package com.intramirror.web.controller.api.aiduca;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import pk.shoplus.util.MapUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.web.mapping.api.IStockMapping;
import com.intramirror.web.mapping.impl.aiduca.AiDucaSynAllStockMapping;
import com.intramirror.web.mapping.impl.aiduca.AiDucaSynProductMapping;
import com.intramirror.web.mapping.vo.StockOption;
import com.intramirror.web.thread.CommonThreadPool;
import com.intramirror.web.thread.UpdateProductThread;
import com.intramirror.web.thread.UpdateStockThread;
import com.intramirror.web.util.ApiDataFileUtils;
import com.intramirror.web.util.GetPostRequestUtil;

import difflib.DiffRow;

@Controller
@RequestMapping("/alduca_product")
public class AiDucaProductController implements InitializingBean{

	// logger
    private static Logger logger = LoggerFactory.getLogger(AiDucaProductController.class);

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

							for(Map<String, Object> productInfo :productList ){

								Map<String,Object> mqDataMap = new HashMap<String,Object>();
								mqDataMap.put("product", productInfo.toString());
								mqDataMap.put("store_code", param.get("store_code").toString());
								mqDataMap.put("vendor_id", param.get("vendor_id").toString());
								logger.info("AiDucaProductControllerSyn_product,mqDataMap:"+JSONObject.toJSONString(mqDataMap));

								ProductEDSManagement.ProductOptions productOptions = aiDucaSynProductMapping.mapping(mqDataMap);
								ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
								vendorOptions.setVendorId(Long.parseLong(param.get("vendor_id").toString()));
								logger.info("AiDucaProductControllerSyn_product,initParam,productOptions:"+JSONObject.toJSONString(productOptions)+",vendorOptions:"+JSONObject.toJSONString(vendorOptions)+",eventName:"+eventName+",param:"+JSONObject.toJSONString(param)+",mqDataMap:"+JSONObject.toJSONString(mqDataMap));

								logger.info("AiDucaProductControllerSyn_product,execute,start,productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName+",mqDataMap:"+JSONObject.toJSONString(mqDataMap)+",param:"+JSONObject.toJSONString(param));
								CommonThreadPool.execute(eventName,aiducaExecutor,Integer.parseInt(param.get("threadNum").toString()),new UpdateProductThread(productOptions,vendorOptions,fileUtils,mqDataMap));
								logger.info("AiDucaProductControllerSyn_product,execute,end,productOptions::"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName+",mqDataMap:"+JSONObject.toJSONString(mqDataMap)+",param:"+JSONObject.toJSONString(param));
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

    @RequestMapping(value = "/syn_stock",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getAllStock(@Param(value = "name")String name){
    	logger.info("AiDucaProductControllerGetAllStock,inputParams,name:"+name);
        MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());

        // check name
        if(StringUtils.isBlank(name)) {
            return mapUtils.putData("status", StatusType.FAILURE).putData("info","name is null !!!").getMap();
        }
    	
        try {

        	// get initParam
        	Map<String,Object> param = (Map<String, Object>) paramsMap.get(name);
            logger.info("AiDucaProductControllerGetAllStock,initParam,param:"+JSONObject.toJSONString(param));
			ThreadPoolExecutor aiducaExecutor = (ThreadPoolExecutor) param.get("aiducaStockExecutor");
			String eventName = param.get("eventName").toString();
			ApiDataFileUtils fileUtils = (ApiDataFileUtils) param.get("fileUtils");

			// invoke api
			GetPostRequestUtil requestGet = new GetPostRequestUtil();
			String json = requestGet.requestMethodType(GetPostRequestUtil.HTTP_GET,param.get("url").toString(),null);
			logger.info("AiDucaProductControllerGetAllStock,requestMethodType,json:"+json);

			if(StringUtils.isNotBlank(json)) {

				List<Map<String, Object>> stockList = (List<Map<String, Object>>) JSONObject.parse(json);
				logger.info("AiDucaProductControllerGetAllStock,convertStringToList,stockListSize:"+stockList.size()+",stockList:"+JSONObject.toJSONString(stockList));

				String originProductPath = Contants.aiduca_file_path + Contants.aiduca_origin_stock_all + Contants.aiduca_file_type;
				String revisedProductPath = Contants.aiduca_file_path + Contants.aiduca_revised_stock_all + Contants.aiduca_file_type;

				if(!FileUtil.fileExists(originProductPath)) {
					FileUtil.createFileByType(Contants.aiduca_file_path,Contants.aiduca_origin_stock_all + Contants.aiduca_file_type,converString(stockList));
					for(Map<String, Object> productInfo :stockList ){

						Map<String,Object> mqDataMap = new HashMap<String,Object>();
						mqDataMap.put("product", productInfo.toString());
						mqDataMap.put("store_code", param.get("store_code").toString());
						mqDataMap.put("vendor_id", param.get("vendor_id").toString());

						logger.info("AiDucaProductControllerGetAllStock,mapping,start,mqDataMap:"+new Gson().toJson(mqDataMap)+",eventName:"+eventName+",param:"+JSONObject.toJSONString(param));
						StockOption stockOption = aiDucaSynAllStockMapping.mapping(mqDataMap);
						logger.info("AiDucaProductControllerGetAllStock,mapping,end,mqDataMap:"+new Gson().toJson(mqDataMap)+",stockOption:"+new Gson().toJson(stockOption)+",eventName:"+eventName+",param:"+JSONObject.toJSONString(param));

						logger.info("AiDucaProductControllerGetAllStock,execute,start,mqDataMap:"+new Gson().toJson(mqDataMap)+",stockOption:"+new Gson().toJson(stockOption)+",eventName:"+eventName+",param:"+JSONObject.toJSONString(param));
						CommonThreadPool.execute(eventName,aiducaExecutor,Integer.parseInt(param.get("threadNum").toString()),new UpdateStockThread(stockOption,fileUtils,mqDataMap));
						logger.info("AiDucaProductControllerGetAllStock,execute,end,mqDataMap:"+new Gson().toJson(mqDataMap)+",stockOption:"+new Gson().toJson(stockOption)+",eventName:"+eventName+",param:"+JSONObject.toJSONString(param));
					}

				}else{

					FileUtil.createFileByType(Contants.aiduca_file_path,Contants.aiduca_revised_stock_all+ Contants.aiduca_file_type,converString(stockList));

					List<DiffRow> diffRows = FileUtil.CompareTxtByType(originProductPath,revisedProductPath);
					logger.info("AiDucaProductControllerGetAllStock,diffRowsSize:"+diffRows.size()+",diffRows:"+JSONObject.toJSONString(diffRows));

					for(DiffRow diffRow : diffRows) {
						DiffRow.Tag tag = diffRow.getTag();
						if(tag == DiffRow.Tag.INSERT || tag == DiffRow.Tag.CHANGE) {
							logger.info("AiDucaProductControllerGetAllStock,change,diffRows:"+JSONObject.toJSONString(diffRow));

							Map<String,Object> mqDataMap = new HashMap<String,Object>();
							//读取出来会自动带换行符,需要转换
							mqDataMap.put("product", diffRow.getNewLine().replace("<br>", ""));
							mqDataMap.put("store_code", param.get("store_code").toString());
							mqDataMap.put("vendor_id", param.get("vendor_id").toString());

							// 映射数据 封装VO
							logger.info("AiDucaProductControllerGetAllStock,mapping,start,mqDataMap:"+new Gson().toJson(mqDataMap)+",eventName:"+eventName);
							StockOption stockOption = aiDucaSynAllStockMapping.mapping(mqDataMap);
							logger.info("AiDucaProductControllerGetAllStock,mapping,end,mqDataMap:"+new Gson().toJson(mqDataMap)+",stockOption:"+new Gson().toJson(stockOption)+",eventName:"+eventName+",param:"+JSONObject.toJSONString(param));

							// 线程池
							logger.info("AiDucaProductControllerGetAllStock,execute,start,mqDataMap:"+new Gson().toJson(mqDataMap)+",stockOption:"+new Gson().toJson(stockOption)+",eventName:"+eventName+",param:"+JSONObject.toJSONString(param));
							CommonThreadPool.execute(eventName,aiducaExecutor,Integer.parseInt(param.get("threadNum").toString()),new UpdateStockThread(stockOption,fileUtils,mqDataMap));
							logger.info("AiDucaProductControllerGetAllStock,execute,end,mqDataMap:"+new Gson().toJson(mqDataMap)+",stockOption:"+new Gson().toJson(stockOption)+",eventName:"+eventName+",param:"+JSONObject.toJSONString(param));

						}
					}

					// 4.处理结束后备份origin,并重置origin
					String originContentAll = FileUtil.readFile(originProductPath);
					FileUtil.createFileByType(Contants.aiduca_file_path,"bak_"+Contants.aiduca_origin_stock_all + Contants.aiduca_file_type,originContentAll);
					FileUtil.createFileByType(Contants.aiduca_file_path,"bak_"+Contants.aiduca_revised_stock_all  + Contants.aiduca_file_type,converString(stockList));
					FileUtil.createFileByType(Contants.aiduca_file_path,Contants.aiduca_origin_stock_all + Contants.aiduca_file_type,converString(stockList));
				}


			}else{
				logger.info("AiDucaProductControllerGetAllStock,paramIsNull,name:"+name);
				 mapUtils.putData("status",StatusType.FAILURE).putData("info","job aiDuca getAllStock 请求接口获取的商品对象   返回数据为空    url:"+param.get("url").toString());
				 return mapUtils.getMap();
			}
			mapUtils.putData("status",StatusType.SUCCESS).putData("info","success");
        } catch (Exception e) {
            e.printStackTrace();
			logger.info("AiDucaProductControllerGetAllStock,errorMessage:"+ExceptionUtils.getExceptionDetail(e));
            mapUtils.putData("status",StatusType.FAILURE).putData("info",ExceptionUtils.getExceptionDetail(e));
        }
        return mapUtils.getMap();
    }

    /**
     * 转换类型
     * @param list
     * @return
     */
    public String converString(List<Map<String, Object>> list){
    	 StringBuffer stringBuffer = new StringBuffer();
    	 
    	 if(list != null && list.size() > 0 ){
    		 for(Map<String, Object> info : list){
    			 stringBuffer.append(info.toString()).append("\n");
    		 }
    	 }
    	return stringBuffer.toString();
    }

	@Override
	public void afterPropertiesSet() throws Exception {
        // alduca_product_delta_update
        ThreadPoolExecutor alducaDeltaProductExecutor =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        Map<String,Object> alduca_product_delta_update = new HashMap<>();
		alduca_product_delta_update.put("url","http://rest.alducadaosta.com/api/Catalog/Sku4Platform?Username=IntraMirror&Password=%2B%2BInt%3DMir%2B%2B");
		alduca_product_delta_update.put("vendor_id","19");
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
		alduca_product_all_update.put("vendor_id","19");
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
		alduca_stock_all_update.put("vendor_id","19");
		alduca_stock_all_update.put("store_code","AIDUCA");
		alduca_stock_all_update.put("threadNum","5");
		alduca_stock_all_update.put("vendorName","Al Duca Daosta");
		alduca_stock_all_update.put("aiducaStockExecutor",alducaAllStockExecutor);
		alduca_stock_all_update.put("eventName","alduca_stock_all_update");
		alduca_stock_all_update.put("datetime",DateUtils.getStrDate(new Date()));
		alduca_stock_all_update.put("fileUtils",new ApiDataFileUtils("alduca","alduca_stock_all_update"));

        // put data
        paramsMap = new HashMap<>();
		paramsMap.put("alduca_product_all_update",alduca_product_all_update);
		paramsMap.put("alduca_product_delta_update",alduca_product_delta_update);
		paramsMap.put("alduca_stock_all_update",alduca_stock_all_update);
	}
}
