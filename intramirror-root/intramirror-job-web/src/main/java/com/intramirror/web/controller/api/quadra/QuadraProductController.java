package com.intramirror.web.controller.api.quadra;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.intramirror.common.help.ExceptionUtils;

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
import pk.shoplus.util.FileUtil;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.web.mapping.impl.QuadraSynProductMapping;
import com.intramirror.web.thread.CommonThreadPool;
import com.intramirror.web.thread.UpdateProductThread;
import com.intramirror.web.util.ApiDataFileUtils;
import com.intramirror.web.util.GetPostRequestUtil;

import difflib.DiffRow;

@Controller
@RequestMapping("/job/product")
public class QuadraProductController implements InitializingBean{

    private static Logger logger = Logger.getLogger(QuadraProductController.class);
    
	@Autowired
	private QuadraSynProductMapping productMappingService ;
    
    // init params
    private Map<String,Object> paramsMap;
    
    @RequestMapping(value = "/updateProductAll",method = RequestMethod.GET)
    @ResponseBody
    public ResultMessage updateProductAll(@Param(value = "name")String name){
        logger.info("job quadra updateProductAll,param:name="+name);
        ResultMessage resultMessage = ResultMessage.getInstance();

        try {
        	//获取初始化参数
            Map<String,Object> param = (Map<String, Object>) paramsMap.get(name);
//            logger.info("job quadra updateProductAll 初始化参数:"+new Gson().toJson(param));
            
        	if(param != null && param.size() > 0 ){
        		
        		ThreadPoolExecutor quadraExecutor = (ThreadPoolExecutor) param.get("quadraExecutor");
        	    String eventName = param.get("eventName").toString();
        	    ProductEDSManagement productEDSManagement = new ProductEDSManagement();
                String currentDate = DateUtils.getStrDate(new Date(),"yyyyMMddHHmmss");
                ApiDataFileUtils fileUtils = (ApiDataFileUtils) param.get("fileUtils");
        		   
        		//http 请求获取数据
        		GetPostRequestUtil requestGet = new GetPostRequestUtil();
                logger.info("job quadra updateProductAll  Call the interface to get the data  url:"+param.get("url").toString());
                String json = requestGet.requestMethodType(GetPostRequestUtil.HTTP_GET,param.get("url").toString(),null);
                if(StringUtils.isNotBlank(json)) {
                	
                    // Quadra 拆分接口返回的product放入线程池调用
                    logger.info("job quadra updateProductAll 返回的json 数据转换成对象");
                	List<Map<String, Object>> List = (List<Map<String, Object>>) JSONObject.parse(json);
                	
                	if(List !=null && List.size() > 0 ){
                		//原始数据存文件
                		fileUtils.bakPendingFile("quadra-"+currentDate,json);
                        logger.info("job quadra updateProduct  遍历解析商品列表   存入线程池消费    productList Size:"+List.size());
                        
                        int i = 0;
                		for(Map<String, Object> productInfo :List ){
                			
                            Map<String,Object> mqDataMap = new HashMap<String,Object>();
                            mqDataMap.put("product", productInfo.toString());
                            mqDataMap.put("store_code", param.get("store_code").toString());
                            mqDataMap.put("vendor_id", param.get("vendor_id").toString());
                            mqDataMap.put("full_update_product","1");
                            
                            // 放入线程池
                			System.out.println(i++);
//                            logger.info("job quadra UpdateProductAll,putMessage,mqDateMap:"+new Gson().toJson(mqDataMap)+",urlMap:"+new Gson().toJson(param));
//                            QueueUtils.putMessage(mqDataMap, "",param.get("url").toString(), QueueNameJobEnum.QuadraSynAllProduct);
                            
                            // 映射数据 封装VO
                            ProductEDSManagement.ProductOptions productOptions = productMappingService.mapping(mqDataMap);
                            ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                            vendorOptions.setVendorId(Long.parseLong(param.get("vendor_id").toString()));
                            logger.info("job quadra updateProduct,initParam,productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName);

                            // 线程池
                            logger.info("job quadra updateProduct,execute,startDate:"+DateUtils.getStrDate(new Date())+",productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName);
                            CommonThreadPool.execute(eventName,quadraExecutor,Integer.parseInt(param.get("threadNum").toString()),new UpdateProductThread(productOptions,vendorOptions,fileUtils));
                            logger.info("job quadra updateProduct,execute,endDate:"+DateUtils.getStrDate(new Date())+",productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName);
                            
                            //跳出循环,测试用
//                            if(i>150){
//                            	break;
//                            }
                		}
                	}
                    

                }else{
                	 logger.info("job quadra updateProductAll  请求接口获取的商品对象数据为空");
                }
            	
                resultMessage.successStatus().addMsg("SUCCESS");
        	}

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("job quadra UpdateProductAll,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
            resultMessage.errorStatus().addMsg("error message : " + e.getMessage());
        }
        return resultMessage;
    }
    
    
    
    
    @RequestMapping(value = "/updateProductDay",method = RequestMethod.GET)
    @ResponseBody
    public ResultMessage updateProductDay(@Param(value = "name")String name){
    	logger.info("job quadra updateProductDay,param:name="+name);
        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
        	//获取初始化参数
            Map<String,Object> param = (Map<String, Object>) paramsMap.get(name);
//            logger.info("job quadra updateProductDay 初始化参数:"+new Gson().toJson(param));
        	
        	if(param != null && param.size() > 0){
        		
        		ThreadPoolExecutor quadraExecutor = (ThreadPoolExecutor) param.get("quadraExecutor");
        	    String eventName = param.get("eventName").toString();
        	    ProductEDSManagement productEDSManagement = new ProductEDSManagement();
                String currentDate = DateUtils.getStrDate(new Date(),"yyyyMMddHHmmss");
                ApiDataFileUtils fileUtils = (ApiDataFileUtils) param.get("fileUtils");
                
        		GetPostRequestUtil requestGet = new GetPostRequestUtil();
                logger.info("job quadra updateProductDay  Call the interface to get the data    url:"+param.get("url").toString());
                String json = requestGet.requestMethodType(GetPostRequestUtil.HTTP_GET,param.get("url").toString(),null);
                
                if(StringUtils.isNotBlank(json)) {
            		//原始数据存文件
            		fileUtils.bakPendingFile("quadra-"+currentDate,json);
            		
                    // Quadra 拆分接口返回的product存入线程池消费
                    logger.info("job quadra updateProductDay 返回的json 数据转换成对象");
                	List<Map<String, Object>> List = (List<Map<String, Object>>) JSONObject.parse(json);

	                String originProductPath = Contants.quadra_file_path + Contants.quadra_origin_product_day + Contants.quadra_file_type;
	                String revisedProductPath = Contants.quadra_file_path + Contants.quadra_revised_product_day + Contants.quadra_file_type;
	                
	                //如果第一次更新,全部添加到线程池消费   不需要比较
	                if(!FileUtil.fileExists(originProductPath)) {
	                	logger.info("job quadra updateProductDay  第一次更新,全部添加 不需要比较  ");
	                	FileUtil.createFileByType(Contants.quadra_file_path,Contants.quadra_origin_product_day + Contants.quadra_file_type,converString(List));
	                	
	                	if(List !=null && List.size() > 0 ){
	                		
	                        logger.info("job quadra updateProductDay  遍历解析商品列表   存入线程池消费    productList Size:"+List.size());
	                        int i = 0;
	                		for(Map<String, Object> productInfo :List ){
	                			
	                            Map<String,Object> mqDataMap = new HashMap<String,Object>();
	                            mqDataMap.put("product", productInfo.toString());
	                            mqDataMap.put("store_code", param.get("store_code").toString());
	                            mqDataMap.put("vendor_id", param.get("vendor_id").toString());
	                            
	                            // 放入线程池
	                			System.out.println(i++);
	                			
	                            // 映射数据 封装VO
	                            ProductEDSManagement.ProductOptions productOptions = productMappingService.mapping(mqDataMap);
	                            ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
	                            vendorOptions.setVendorId(Long.parseLong(param.get("vendor_id").toString()));
	                            logger.info("job quadra updateProductDay,initParam,productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName);

	                            // 线程池
	                            logger.info("job quadra updateProductDay,execute,startDate:"+DateUtils.getStrDate(new Date())+",productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName);
	                            CommonThreadPool.execute(eventName,quadraExecutor,Integer.parseInt(param.get("threadNum").toString()),new UpdateProductThread(productOptions,vendorOptions,fileUtils));
	                            logger.info("job quadra updateProductDay,execute,endDate:"+DateUtils.getStrDate(new Date())+",productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName);
	                            
//	                            //跳出循环,测试用
//	                            if(i>50){
//	                            	break;
//	                            }
	                		}
	                	}
	                }else{
	                	
	                	logger.info("job quadra updateProductDay  比较文件差异,获取修改的增量加入线程池消费  ");
		            	// 1.创建
		                FileUtil.createFileByType(Contants.quadra_file_path,Contants.quadra_revised_product_day+ Contants.quadra_file_type,converString(List));
		
		                // 2.文件进行对比
		                List<DiffRow> diffRows = FileUtil.CompareTxtByType(originProductPath,revisedProductPath);
		
		                // 3.消息添加到线程池消费
//		                StringBuffer stringBuffer = new StringBuffer();
		                int i=0;
		                for(DiffRow diffRow : diffRows) {
		                    DiffRow.Tag tag = diffRow.getTag();
		                    if(tag == DiffRow.Tag.INSERT || tag == DiffRow.Tag.CHANGE) {
		                        logger.info("change -------" + new Gson().toJson(diffRow));
//		                        stringBuffer.append(diffRow.getNewLine()+"\n");
		                        
	                            Map<String,Object> mqDataMap = new HashMap<String,Object>();
	                            //读取出来会自动带换行符,需要转换
	                            mqDataMap.put("product", diffRow.getNewLine().replace("<br>", ""));
	                            mqDataMap.put("store_code", param.get("store_code").toString());
	                            mqDataMap.put("vendor_id", param.get("vendor_id").toString());
	                            
	                            System.out.println(i++);
	                            // 放入线程池
	                            // 映射数据 封装VO
	                            ProductEDSManagement.ProductOptions productOptions = productMappingService.mapping(mqDataMap);
	                            ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
	                            vendorOptions.setVendorId(Long.parseLong(param.get("vendor_id").toString()));
	                            logger.info("job quadra updateProductDay,initParam,productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName);

	                            // 线程池
	                            logger.info("job quadra updateProductDay,execute,startDate:"+DateUtils.getStrDate(new Date())+",productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName);
	                            CommonThreadPool.execute(eventName,quadraExecutor,Integer.parseInt(param.get("threadNum").toString()),new UpdateProductThread(productOptions,vendorOptions,fileUtils));
	                            logger.info("job quadra updateProductDay,execute,endDate:"+DateUtils.getStrDate(new Date())+",productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName);
	                            
	                            //跳出循环,测试用
//	                            if(i>50){
//	                            	break;
//	                            }
		                    } 
		                }
		                
		                // 4.处理结束后备份origin,并重置origin
		                String originContentAll = FileUtil.readFile(originProductPath);
		                FileUtil.createFileByType(Contants.quadra_file_path,"bak_"+Contants.quadra_origin_product_day + Contants.quadra_file_type,originContentAll);
		                FileUtil.createFileByType(Contants.quadra_file_path,"bak_"+Contants.quadra_revised_product_day  + Contants.quadra_file_type,converString(List));
//		                FileUtil.createFileByType(Contants.quadra_file_path,"bak_"+Contants.quadra_change_product_day +strDate+ Contants.quadra_file_type,stringBuffer.toString());
		                FileUtil.createFileByType(Contants.quadra_file_path,Contants.quadra_origin_product_day + Contants.quadra_file_type,converString(List));
	                }
	                
	                
                }else{
               	 logger.info("job quadra updateProductDay  请求接口获取的商品对象数据为空");
               }
            	
                resultMessage.successStatus().addMsg("SUCCESS");
        	}

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("QuadraProductControllerUpdateProductDay,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
            resultMessage.errorStatus().addMsg("error message : " + e.getMessage());
        }
        return resultMessage;
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
        // QuadraSynAllProduct
        ThreadPoolExecutor quadraAllProductExecutor =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        Map<String,Object> quadraAllUpdateproduct = new HashMap<>();
        quadraAllUpdateproduct.put("url","http://apiquadra.teknosis.link:444/tabella?nome=inventory_all");
        quadraAllUpdateproduct.put("vendor_id","19");
        quadraAllUpdateproduct.put("store_code","QUADRA");
        quadraAllUpdateproduct.put("threadNum","10");
        quadraAllUpdateproduct.put("quadraExecutor",quadraAllProductExecutor);
        quadraAllUpdateproduct.put("eventName","quadra全量更新商品");
        quadraAllUpdateproduct.put("fileUtils",new ApiDataFileUtils("quadra","quadra全量更新商品"));

        // QuadraSynDayProduct
        ThreadPoolExecutor quadraDayUpdateproductExecutor =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        Map<String,Object> quadraDayUpdateproduct = new HashMap<>();
        quadraDayUpdateproduct.put("url","http://apiquadra.teknosis.link:444/tabella?nome=inventory_day");
        quadraDayUpdateproduct.put("vendor_id","19");
        quadraDayUpdateproduct.put("store_code","QUADRA");
        quadraDayUpdateproduct.put("threadNum","10");
        quadraDayUpdateproduct.put("quadraExecutor",quadraDayUpdateproductExecutor);
        quadraDayUpdateproduct.put("eventName","quadra增量更新当日商品");
        quadraDayUpdateproduct.put("datetime",DateUtils.getStrDate(new Date()));
        quadraDayUpdateproduct.put("fileUtils",new ApiDataFileUtils("quadra","quadr增量更新当日商品"));

        // put data
        paramsMap = new HashMap<>();
        paramsMap.put("quadraAllUpdateproduct",quadraAllUpdateproduct);
        paramsMap.put("quadraDayUpdateproduct",quadraDayUpdateproduct);
    }
    
        
}
