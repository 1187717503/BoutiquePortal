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
@RequestMapping("/job/aiDucaProduct")
public class AiDucaProductController implements InitializingBean{

    private static Logger logger = LoggerFactory.getLogger(AiDucaProductController.class);
    
	@Autowired
	private AiDucaSynProductMapping aiDucaSynProductMapping;
	
	@Autowired
	private AiDucaSynAllStockMapping aiDucaSynAllStockMapping;
	
	
    // mapping
    @Resource(name = "edsUpdateByStockMapping")
    private IStockMapping iStockMapping;
    
    // init params
    private Map<String,Object> paramsMap;
    
    @RequestMapping(value = "/getAllSKU",method = RequestMethod.GET)
    @ResponseBody
    public ResultMessage getAllSKU(@Param(value = "name")String name){
    	logger.info("job aiDuca getAllSKU 入参:"+name);
        ResultMessage resultMessage = ResultMessage.getInstance();
        
        try {
        	//获取初始化参数
            Map<String,Object> param = (Map<String, Object>) paramsMap.get(name);
        	
            Date date = new Date();
            DateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
            String strDate = format1.format(date);
            
        	if(param != null && param.size() > 0 ){
        		//获取初始化参数
        		ThreadPoolExecutor aiducaExecutor = (ThreadPoolExecutor) param.get("aiducaProductExecutor");
        	    String eventName = param.get("eventName").toString();
        	    ProductEDSManagement productEDSManagement = new ProductEDSManagement();
                String currentDate = DateUtils.getStrDate(new Date(),"yyyyMMddHHmmss");
                ApiDataFileUtils fileUtils = (ApiDataFileUtils) param.get("fileUtils");
                
                //http 请求获取数据
        		GetPostRequestUtil requestGet = new GetPostRequestUtil();
                logger.info("job aiDuca getAllSKU  调用接口获取商品列表   url:"+param.get("url").toString());
                String json = requestGet.requestMethodType(GetPostRequestUtil.HTTP_GET,param.get("url").toString(),null);
                
                if(StringUtils.isNotBlank(json)) {
                	
                    // aiDuca 拆分接口返回的product存入线程池
                    logger.info("job aiDuca getAllSKU 返回的json 数据转换成对象");
                	List<Map<String, Object>> List = (List<Map<String, Object>>) JSONObject.parse(json);
                	

	                String originProductPath = Contants.aiduca_file_path + Contants.aiduca_origin_product_all + Contants.aiduca_file_type;
	                String revisedProductPath = Contants.aiduca_file_path + Contants.aiduca_revised_product_all + Contants.aiduca_file_type;
	                
	                //如果第一次更新,全部添加到线程池  不需要比较
	                if(!FileUtil.fileExists(originProductPath)) {
	                	logger.info("job aiDuca getAllSKU  第一次更新,全部添加到MQ 不需要比较  ");
	                	FileUtil.createFileByType(Contants.aiduca_file_path,Contants.aiduca_origin_product_all + Contants.aiduca_file_type,converString(List));
	                	
	                	if(List !=null && List.size() > 0 ){
                		//原始数据存文件
                		fileUtils.bakPendingFile("aiduca-"+currentDate,json);
                		
                        logger.info("job aiDuca getAllSKU  遍历解析商品列表   存入MQ队列    productList Size:"+List.size());
                        int i = 0;
                		for(Map<String, Object> productInfo :List ){
                			
                            Map<String,Object> mqDataMap = new HashMap<String,Object>();
                            mqDataMap.put("product", productInfo.toString());
                            mqDataMap.put("store_code", param.get("store_code").toString());
                            mqDataMap.put("vendor_id", param.get("vendor_id").toString());
                            
                            // 放入线程池
                			System.out.println(i++);
                			logger.info("job aiDuca getAllSKU 商品信息存入线程池生成商品    mqDataMap:"+new Gson().toJson(mqDataMap));
//                            QueueUtils.putMessage(mqDataMap, "",urlMap.get("url").toString(), QueueNameJobEnum.AiDucaSynAllProduct);
                            
                            // 映射数据 封装VO
                            ProductEDSManagement.ProductOptions productOptions = aiDucaSynProductMapping.mapping(mqDataMap);
                            ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                            vendorOptions.setVendorId(Long.parseLong(param.get("vendor_id").toString()));
                            logger.info("job aiDuca getAllSKU,initParam,productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName);

                            // 线程池
                            logger.info("job aiDuca getAllSKU,execute,startDate:"+DateUtils.getStrDate(new Date())+",productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName);
                            CommonThreadPool.execute(eventName,aiducaExecutor,Integer.parseInt(param.get("threadNum").toString()),new UpdateProductThread(productOptions,vendorOptions,fileUtils,mqDataMap));
                            logger.info("job aiDuca getAllSKU,execute,endDate:"+DateUtils.getStrDate(new Date())+",productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName);
                            
                            //跳出循环,测试用
//                            if(i>120){
//                            	break;
//                            }
                		}
                	  }

	                }else{
	                	
	                	logger.info("job aiDuca getAllSKU  比较文件差异,获取修改的增量加入线程池  ");
		            	// 1.创建
		                FileUtil.createFileByType(Contants.aiduca_file_path,Contants.aiduca_revised_product_all+ Contants.aiduca_file_type,converString(List));
		
		                // 2.文件进行对比
		                List<DiffRow> diffRows = FileUtil.CompareTxtByType(originProductPath,revisedProductPath);
		                logger.info("job aiDuca getAllSKU  比较文件差异,差异数量为: "+diffRows.size());
		
		                // 3.消息筛入线程池
//		                StringBuffer stringBuffer = new StringBuffer();
		                int i = 0;
		                for(DiffRow diffRow : diffRows) {
		                    DiffRow.Tag tag = diffRow.getTag();
		                    if(tag == DiffRow.Tag.INSERT || tag == DiffRow.Tag.CHANGE) {
		                        logger.info("job aiDuca getAllSKU change -------" + new Gson().toJson(diffRow));
//		                        stringBuffer.append(diffRow.getNewLine()+"\n");
		                        
	                            Map<String,Object> mqDataMap = new HashMap<String,Object>();
	                            //读取出来会自动带换行符,需要转换
	                            mqDataMap.put("product", diffRow.getNewLine().replace("<br>", ""));
	                            mqDataMap.put("store_code", param.get("store_code").toString());
	                            mqDataMap.put("vendor_id", param.get("vendor_id").toString());
	                            
	                            i++;
	                            // 放入线程池
	                			logger.info("job aiDuca getAllSKU 商品信息存入线程池生成商品    mqDataMap:"+new Gson().toJson(mqDataMap));
	                            
	                            // 映射数据 封装VO
	                            ProductEDSManagement.ProductOptions productOptions = aiDucaSynProductMapping.mapping(mqDataMap);
	                            ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
	                            vendorOptions.setVendorId(Long.parseLong(param.get("vendor_id").toString()));
	                            logger.info("job aiDuca getAllSKU,initParam,productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName);

	                            // 线程池
	                            logger.info("job aiDuca getAllSKU,execute,startDate:"+DateUtils.getStrDate(new Date())+",productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName);
	                            CommonThreadPool.execute(eventName,aiducaExecutor,Integer.parseInt(param.get("threadNum").toString()),new UpdateProductThread(productOptions,vendorOptions,fileUtils,mqDataMap));
	                            logger.info("job aiDuca getAllSKU,execute,endDate:"+DateUtils.getStrDate(new Date())+",productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName);
	                            
//	                            //跳出循环,测试用
//	                            if(i>120){
//	                            	break;
//	                            }
		                    } 
		                }
		                
		                // 4.处理结束后备份origin,并重置origin
		                String originContentAll = FileUtil.readFile(originProductPath);
		                FileUtil.createFileByType(Contants.aiduca_file_path,"bak_"+Contants.aiduca_origin_product_all + Contants.aiduca_file_type,originContentAll);
		                FileUtil.createFileByType(Contants.aiduca_file_path,"bak_"+Contants.aiduca_revised_product_all  + Contants.aiduca_file_type,converString(List));
//		                FileUtil.createFileByType(Contants.aiduca_file_path,"bak_"+Contants.aiduca_change_product_all +strDate+ Contants.aiduca_file_type,stringBuffer.toString());
		                FileUtil.createFileByType(Contants.aiduca_file_path,Contants.aiduca_origin_product_all + Contants.aiduca_file_type,converString(List));
	                }   

                }else{
                	 logger.info("job aiDuca getAllSKU  请求接口获取的商品对象   返回数据为空");
                     resultMessage.errorStatus().addMsg("job aiDuca getAllSKU 请求接口获取的商品对象   返回数据为空    url:"+param.get("url").toString());
                     return resultMessage;
                }
            	
                resultMessage.successStatus().addMsg("SUCCESS");
        	}

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" error message : {}",e.getMessage());
            resultMessage.errorStatus().addMsg("error message : " + e.getMessage());
        }
        return resultMessage;
    }
    
    
    
    
    
    
    @RequestMapping(value = "/getAllStock",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getAllStock(@Param(value = "name")String name){
    	logger.info("job aiDuca getAllStock 入参:"+name);
        MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());
    	
        // check name
        if(StringUtils.isBlank(name)) {
            return mapUtils.putData("status", StatusType.FAILURE).putData("info","name is null !!!").getMap();
        }
    	
        try {
        	//获取初始化参数
            Map<String,Object> param = (Map<String, Object>) paramsMap.get(name);
        	
            Date date = new Date();
            DateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
            String strDate = format1.format(date);
            
        	
        	if(param != null && param.size() > 0 ){
        		//获取初始化参数
        		ThreadPoolExecutor aiducaExecutor = (ThreadPoolExecutor) param.get("aiducaStockExecutor");
        	    String eventName = param.get("eventName").toString();
//                String currentDate = DateUtils.getStrDate(new Date(),"yyyyMMddHHmmss");
                ApiDataFileUtils fileUtils = (ApiDataFileUtils) param.get("fileUtils");
                
                //http 请求获取数据
        		GetPostRequestUtil requestGet = new GetPostRequestUtil();
                logger.info("job aiDuca getAllStock  调用接口获取商品库存列表  url:"+param.get("url").toString());
                String json = requestGet.requestMethodType(GetPostRequestUtil.HTTP_GET,param.get("url").toString(),null);
                
                if(StringUtils.isNotBlank(json)) {
                	
                    // aiDuca 拆分接口返回的product放入线程池
                    logger.info("job aiDuca getAllStock 返回的json 数据转换成对象");
                	List<Map<String, Object>> List = (List<Map<String, Object>>) JSONObject.parse(json);
                	
	                String originProductPath = Contants.aiduca_file_path + Contants.aiduca_origin_stock_all + Contants.aiduca_file_type;
	                String revisedProductPath = Contants.aiduca_file_path + Contants.aiduca_revised_stock_all + Contants.aiduca_file_type;
	                
	                
	                //如果第一次更新,全部添加到线程池 不需要比较
	                if(!FileUtil.fileExists(originProductPath)) {
	                	logger.info("job aiDuca getAllStock  第一次更新,全部添加到线程池 不需要比较  ");
	                	FileUtil.createFileByType(Contants.aiduca_file_path,Contants.aiduca_origin_stock_all + Contants.aiduca_file_type,converString(List));
	                	
	                	if(List !=null && List.size() > 0 ){
                		
                        logger.info("job aiDuca getAllStock  遍历解析Stock列表   商品库存信息存入线程池修改库存    List Size:"+List.size());
                        int i = 0;
                		for(Map<String, Object> productInfo :List ){
                			
                            Map<String,Object> mqDataMap = new HashMap<String,Object>();
                            mqDataMap.put("product", productInfo.toString());
                            mqDataMap.put("store_code", param.get("store_code").toString());
                            mqDataMap.put("vendor_id", param.get("vendor_id").toString());
                            
                            // 放入线程池
                			System.out.println(i++);
                			logger.info("job aiDuca getAllStock 库存信息存入线程池    mqDataMap:"+new Gson().toJson(mqDataMap));
//                            QueueUtils.putMessage(mqDataMap, "",param.get("url").toString(), QueueNameJobEnum.AiDucaSynAllStock);
                            
                            // 映射数据 封装VO
                            logger.info("job aiDuca getAllStock,mapping,start,stockMap:"+new Gson().toJson(mqDataMap)+",eventName:"+eventName);
                            StockOption stockOption = aiDucaSynAllStockMapping.mapping(mqDataMap);
                            logger.info("job aiDuca getAllStock,mapping,end,stockMap:"+new Gson().toJson(mqDataMap)+",stockOption:"+new Gson().toJson(stockOption)+",eventName:"+eventName);

                            // 线程池
                            logger.info("job aiDuca getAllStock,execute,startDate:"+ DateUtils.getStrDate(new Date())+",stockMap:"+new Gson().toJson(mqDataMap)+",stockOption:"+new Gson().toJson(stockOption)+",eventName:"+eventName);
                            CommonThreadPool.execute(eventName,aiducaExecutor,Integer.parseInt(param.get("threadNum").toString()),new UpdateStockThread(stockOption,fileUtils,mqDataMap));
                            logger.info("job aiDuca getAllStock,execute,endDate:"+ DateUtils.getStrDate(new Date())+",stockMap:"+new Gson().toJson(mqDataMap)+",stockOption:"+new Gson().toJson(stockOption)+",eventName:"+eventName);
                            
                            //跳出循环,测试用
//                            if(i>120){
//                            	break;
//                            }
                		}
                	  }

	                }else{
	                	
	                	logger.info("job aiDuca getAllStock  比较文件差异,获取修改的增量加入线程池消费  ");
		            	// 1.创建
		                FileUtil.createFileByType(Contants.aiduca_file_path,Contants.aiduca_revised_stock_all+ Contants.aiduca_file_type,converString(List));
		
		                // 2.文件进行对比
		                List<DiffRow> diffRows = FileUtil.CompareTxtByType(originProductPath,revisedProductPath);
		                logger.info("job aiDuca getAllStock  比较文件差异,差异数量为: "+diffRows.size());
		
		                // 3.消息筛入线程池
//		                StringBuffer stringBuffer = new StringBuffer();
		                int i = 0;
		                for(DiffRow diffRow : diffRows) {
		                    DiffRow.Tag tag = diffRow.getTag();
		                    if(tag == DiffRow.Tag.INSERT || tag == DiffRow.Tag.CHANGE) {
		                        logger.info("job aiDuca getAllStock change -------" + new Gson().toJson(diffRow));
//		                        stringBuffer.append(diffRow.getNewLine()+"\n");
		                        
	                            Map<String,Object> mqDataMap = new HashMap<String,Object>();
	                            //读取出来会自动带换行符,需要转换
	                            mqDataMap.put("product", diffRow.getNewLine().replace("<br>", ""));
	                            mqDataMap.put("store_code", param.get("store_code").toString());
	                            mqDataMap.put("vendor_id", param.get("vendor_id").toString());
	                            
	                            i++;
	                            // 放入线程池
	                			logger.info("job aiDuca getAllStock 商品库存信息存入线程池修改库存    mqDataMap:"+new Gson().toJson(mqDataMap));
//	                            QueueUtils.putMessage(mqDataMap, "",param.get("url").toString(), QueueNameJobEnum.AiDucaSynAllStock);
	                			
	                            // 映射数据 封装VO
	                            logger.info("job aiDuca getAllStock,mapping,start,stockMap:"+new Gson().toJson(mqDataMap)+",eventName:"+eventName);
	                            StockOption stockOption = aiDucaSynAllStockMapping.mapping(mqDataMap);
	                            logger.info("job aiDuca getAllStock,mapping,end,stockMap:"+new Gson().toJson(mqDataMap)+",stockOption:"+new Gson().toJson(stockOption)+",eventName:"+eventName);

	                            // 线程池
	                            logger.info("job aiDuca getAllStock,execute,startDate:"+ DateUtils.getStrDate(new Date())+",stockMap:"+new Gson().toJson(mqDataMap)+",stockOption:"+new Gson().toJson(stockOption)+",eventName:"+eventName);
	                            CommonThreadPool.execute(eventName,aiducaExecutor,Integer.parseInt(param.get("threadNum").toString()),new UpdateStockThread(stockOption,fileUtils,mqDataMap));
	                            logger.info("job aiDuca getAllStock,execute,endDate:"+ DateUtils.getStrDate(new Date())+",stockMap:"+new Gson().toJson(mqDataMap)+",stockOption:"+new Gson().toJson(stockOption)+",eventName:"+eventName);
	                            
//	                            //跳出循环,测试用
//	                            if(i>120){
//	                            	break;
//	                            }
		                    } 
		                }
		                
		                // 4.处理结束后备份origin,并重置origin
		                String originContentAll = FileUtil.readFile(originProductPath);
		                FileUtil.createFileByType(Contants.aiduca_file_path,"bak_"+Contants.aiduca_origin_stock_all + Contants.aiduca_file_type,originContentAll);
		                FileUtil.createFileByType(Contants.aiduca_file_path,"bak_"+Contants.aiduca_revised_stock_all  + Contants.aiduca_file_type,converString(List));
//		                FileUtil.createFileByType(Contants.aiduca_file_path,"bak_"+Contants.aiduca_change_stock_all +strDate+ Contants.aiduca_file_type,stringBuffer.toString());
		                FileUtil.createFileByType(Contants.aiduca_file_path,Contants.aiduca_origin_stock_all + Contants.aiduca_file_type,converString(List));
	                }
                    

                }else{
                	 logger.info("job aiDuca getAllStock  请求接口获取的商品对象     返回数据为空");
                     mapUtils.putData("status",StatusType.FAILURE).putData("info","job aiDuca getAllStock 请求接口获取的商品对象   返回数据为空    url:"+param.get("url").toString());
                     return mapUtils.getMap();
                }
            	
                mapUtils.putData("status",StatusType.SUCCESS).putData("info","success");
        	}

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("job aiDuca getAllStock error message : {}",e.getMessage());
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
        // AiDucaSynAllProduct
        ThreadPoolExecutor aiducaAllProductExecutor =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        Map<String,Object> aiducaAllUpdateproduct = new HashMap<>();
        aiducaAllUpdateproduct.put("url","http://rest.alducadaosta.com/api/Catalog/Sku4Platform?Username=IntraMirror&Password=%2B%2BInt%3DMir%2B%2B");
        aiducaAllUpdateproduct.put("vendor_id","20");
        aiducaAllUpdateproduct.put("store_code","AIDUCA");
        aiducaAllUpdateproduct.put("threadNum","10");
        aiducaAllUpdateproduct.put("aiducaProductExecutor",aiducaAllProductExecutor);
        aiducaAllUpdateproduct.put("eventName","aiduca全量更新商品");
        aiducaAllUpdateproduct.put("fileUtils",new ApiDataFileUtils("aiduca","aiduca全量更新商品"));

        // AiDucaSynAllStock
        ThreadPoolExecutor aiducaStockExecutor =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        Map<String,Object> aiducaUpdateStock = new HashMap<>();
        aiducaUpdateStock.put("url","http://rest.alducadaosta.com/api/stock/Stock4sku/all?Username=IntraMirror&Password=%2B%2BInt%3DMir%2B%2B");
        aiducaUpdateStock.put("vendor_id","20");
        aiducaUpdateStock.put("store_code","AIDUCA");
        aiducaUpdateStock.put("threadNum","10");
        aiducaUpdateStock.put("aiducaStockExecutor",aiducaStockExecutor);
        aiducaUpdateStock.put("eventName","aiduca增量更新库存");
        aiducaUpdateStock.put("datetime",DateUtils.getStrDate(new Date()));
        aiducaUpdateStock.put("fileUtils",new ApiDataFileUtils("aiduca","aiduca增量更新当日商品"));

        // put data
        paramsMap = new HashMap<>();
        paramsMap.put("aiducaAllUpdateproduct",aiducaAllUpdateproduct);
        paramsMap.put("aiducaUpdateStock",aiducaUpdateStock);
		
	}
    
        
}
