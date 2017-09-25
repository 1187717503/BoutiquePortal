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
import com.intramirror.common.utils.DateUtils;
import com.intramirror.web.mapping.api.IStockMapping;
import com.intramirror.web.mapping.vo.StockOption;
import com.intramirror.web.thread.CommonThreadPool;
import com.intramirror.web.thread.UpdateStockThread;
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
@RequestMapping("filippo_stock")
public class FilippoUpdateByStockController  implements InitializingBean {

    // logger
    private static final Logger logger = Logger.getLogger(FilippoUpdateByStockController.class);

    // init params
    private Map<String,Object> paramsMap;

    // getpost util
    private static GetPostRequestUtil getPostRequestUtil = new GetPostRequestUtil();

    private static final String revised = "revised";
	
	private static final String origin = "origin";
	
	private static final String type = ".txt";

    // mapping
    @Resource(name = "filippoSynStockMapping")
    private IStockMapping iStockMapping;
    
    @ResponseBody
    @RequestMapping("/syn_product")
    public Map<String,Object> execute(@Param(value = "name")String name){
        MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());
        logger.info("FilippoUpdateByStockControllerExecute,inputParams,name:"+name);

        // check name
        if(StringUtils.isBlank(name)) {
            return mapUtils.putData("status", StatusType.FAILURE).putData("info","name is null").getMap();
        }
        
        // get param
        Map<String,Object> param = (Map<String, Object>) paramsMap.get(name);
        String vendor_id = param.get("vendor_id").toString();
        String url = param.get("url").toString();
        String filippo_compare_path = param.get("filippo_compare_path").toString();
        ApiDataFileUtils fileUtils = (ApiDataFileUtils) param.get("fileUtils");
        
        
        String eventName = param.get("eventName").toString();
        int threadNum = Integer.parseInt(param.get("threadNum").toString());
        ThreadPoolExecutor filippoExecutor = (ThreadPoolExecutor) param.get("filippoExecutor");

        try {
        	ProductEDSManagement.VendorOptions vendorOption = new ProductEDSManagement().getVendorOptions();
        	vendorOption.setVendorId(Long.parseLong(vendor_id));
        	String getResponse = getPostRequestUtil.requestMethod(GetPostRequestService.HTTP_GET,url,null);
        	
        	Map<String,Object> mqMap = new HashMap<>();
            mqMap.put("vendorOptions",vendorOption);
        	
            if (StringUtils.isNotBlank(getResponse)){
            	fileUtils.bakPendingFile("stock_vendor_id_"+vendor_id,getResponse);
	        	// 1.如果第一次触发此接口,origin file不存在则创建,用作下次调用对比
            	if (!FileUtil.fileExists(filippo_compare_path+origin+type)) {
					FileUtil.createFile(filippo_compare_path,origin+type, getResponse);
				}
	            	
            	FileUtil.createFile(filippo_compare_path, revised+type, getResponse);
                // 2.处理文件,筛选出product所在行的数据
				String originContent = FileUtil.readFileByFilippo(filippo_compare_path+origin+type);
				String revisedContent = FileUtil.readFileByFilippo(filippo_compare_path+revised+type);

                // 3.写入文件,将筛选出的product数据写入文件
				FileUtil.createFile(filippo_compare_path, "compare_origin.txt", originContent);
				FileUtil.createFile(filippo_compare_path, "compare_revised.txt", revisedContent);

                // 4.文件进行对比
                List<DiffRow> diffRows = FileUtil.CompareTxt(filippo_compare_path+"compare_origin.txt"
                		,filippo_compare_path+"compare_revised.txt");

                // 5.消息筛入MQ
                StringBuffer stringBuffer = new StringBuffer();
                int index = 0;
                for(DiffRow diffRow : diffRows) {
                    DiffRow.Tag tag = diffRow.getTag();
                    if(tag == DiffRow.Tag.INSERT || tag == DiffRow.Tag.CHANGE) {
                        logger.info("change -------" + diffRow.getNewLine());
                        stringBuffer.append(diffRow.getNewLine()+"\n");
                        if (!StringUtils.isNotBlank(diffRow.getOldLine())){
							break;
						}
                        mqMap.put("product_data",diffRow.getOldLine());
                        mqMap.put("vendor_id",vendor_id);
	                    iStockMapping.mapping(mqMap);
	                    // 映射数据 封装VO
	                    logger.info("FilippoUpdateByStockControllerExecute,mapping,start,jsonObject:"+JSONObject.toJSONString(mqMap)+",eventName:"+eventName+"index:"+index);
	                    StockOption stockOption = iStockMapping.mapping(mqMap);
	                    logger.info("FilippoUpdateByStockControllerExecute,mapping,end,jsonObject:"+JSONObject.toJSONString(mqMap)+",stockOption:"+JSONObject.toJSONString(stockOption)+",eventName:"+eventName+"index:"+index);

	                    // 线程池
	                    logger.info("EdsAllUpdateByStockControllerExecute,execute,startDate:"+ DateUtils.getStrDate(new Date())+",jsonObject:"+JSONObject.toJSONString(stockOption)+",stockOption:"+JSONObject.toJSONString(stockOption)+",eventName:"+eventName+"index:"+index);
	                    CommonThreadPool.execute(eventName,filippoExecutor,threadNum,new UpdateStockThread(stockOption,fileUtils,mqMap));
	                    logger.info("EdsAllUpdateByStockControllerExecute,execute,endDate:"+ DateUtils.getStrDate(new Date())+",jsonObject:"+JSONObject.toJSONString(stockOption)+",stockOption:"+JSONObject.toJSONString(stockOption)+",eventName:"+eventName+"index:"+index);
                    }
                }  

                // 6.处理结束后备份origin,并重置origin
				String originContentAll = FileUtil.readFile(filippo_compare_path+revised+type);
				FileUtil.createFile(filippo_compare_path, origin+type, originContentAll);
				mapUtils.putData("status",StatusType.SUCCESS).putData("info","SUCCESS !!!");

	            logger.info("FilippoUpdateByStockControllerExecute,getUrlResult SUCCESS");
            }else{
                mapUtils.putData("status", StatusType.FAILURE).putData("info","getUrlResult is null !!!");
                logger.info("FilippoUpdateByStockControllerExecute,getUrlResult is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("FilippoUpdateByStockControllerExecute,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
        }
        logger.info("FilippoUpdateByStockControllerExecute,outputParams,mapUtils:"+ JSONObject.toJSONString(mapUtils));
        return mapUtils.getMap();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // filippo
    	ThreadPoolExecutor filippoExecutor =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        Map<String,Object> filippo_increment_stock = new HashMap<>();
        filippo_increment_stock.put("url","http://stat.filippomarchesani.net:2060/gw/collect.php/?p=1n3r4&o=intra&q=getqty");
        filippo_increment_stock.put("vendor_id","17");
        filippo_increment_stock.put("filippo_compare_path","D:/mnt/compare/filippo/stock/");
        filippo_increment_stock.put("fileUtils",new ApiDataFileUtils("filippo","filippo增量更新库存"));
        filippo_increment_stock.put("eventName","filippo_increment_updateproduct");
        filippo_increment_stock.put("filippoExecutor",filippoExecutor);
        filippo_increment_stock.put("threadNum","5");
        // put initData
        paramsMap = new HashMap<>();
        paramsMap.put("filippo_increment_stock",filippo_increment_stock);
    }
}
