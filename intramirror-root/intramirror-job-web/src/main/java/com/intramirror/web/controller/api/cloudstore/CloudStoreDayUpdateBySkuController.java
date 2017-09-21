package com.intramirror.web.controller.api.cloudstore;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.web.mapping.api.IProductMapping;
import com.intramirror.web.mapping.api.IStockMapping;
import com.intramirror.web.mapping.vo.StockOption;
import com.intramirror.web.thread.CommonThreadPool;
import com.intramirror.web.thread.UpdateProductThread;
import com.intramirror.web.thread.UpdateStockThread;
import com.intramirror.web.util.ApiDataFileUtils;
import com.intramirror.web.util.GetPostRequestUtil;

import pk.shoplus.common.Contants;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.request.api.IGetPostRequest;
import pk.shoplus.service.request.impl.GetPostRequestService;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.MapUtils;
import pk.shoplus.vo.ResultMessage;

@Controller
@RequestMapping("/cloudstore_day")
public class CloudStoreDayUpdateBySkuController implements InitializingBean{

    private final Logger logger = Logger.getLogger(CloudStoreDayUpdateBySkuController.class);
    
    // init params
    private Map<String,Object> paramsMap;

    // getpost util
    private static GetPostRequestUtil getPostRequestUtil = new GetPostRequestUtil();
    
    // create product
    public static ProductEDSManagement productEDSManagement = new ProductEDSManagement();
    
    // mapping
    @Resource(name = "cloudStoreProductMapping")
    private IProductMapping iProductMapping;

    // mapping
    @Resource(name = "cloudStoreUpdateByStockMapping")
    private IStockMapping iStockMapping;
    
    Integer index = 0;
    Integer step = 0;
    @RequestMapping("/syn_sku")
    @ResponseBody
    public Map<String,Object> execute(@Param(value = "name")String name){
    	Map<String, Object> dataMap = new HashMap<String, Object>();
//    	Connection conn = null ;
    	MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());
    	try {
    		IGetPostRequest getPostRequestService = new GetPostRequestService();
    		 // check name
            if(StringUtils.isBlank(name)) {
                return mapUtils.putData("status",StatusType.FAILURE).putData("info","name is null !!!").getMap();
            }
            
            // get params
            Map<String,Object> param = (Map<String, Object>) paramsMap.get(name);
            String url = param.get("url").toString();
            String merchantId = param.get("merchantId").toString();
            String token = param.get("token").toString();
            String vendor_id = param.get("vendor_id").toString();
            String eventName = param.get("eventName").toString();
            String storeCode = param.get("store_code").toString();
            int limit = Integer.parseInt(param.get("limit").toString());
            int offset = Integer.parseInt(param.get("offset").toString());
            int threadNum = Integer.parseInt(param.get("threadNum").toString());
            ThreadPoolExecutor nugnesExecutor = (ThreadPoolExecutor) param.get("nugnesExecutor");
            ApiDataFileUtils fileUtils = (ApiDataFileUtils) param.get("fileUtils");
            //拼接参数
            String appendUrl = url + "?storeCode=" + storeCode + "&limit=" + limit +"&offset=" + offset 
            		+"&merchantId=" + merchantId + "&token=" + token;
            logger.info("CloudStoreGetEventsControllerHandleSplitData,getUrl,appendUrl:"+appendUrl);
            
            
            Map<String, Object> params = new HashMap<>();
            params.put("token", token);
            params.put("merchantId", merchantId);
            
            
            // 调用cloudstore 的 getEvents接口
			logger.info("CloudStoreGetEventsControllerHandleSplitData,requestMethod,url:"+appendUrl+",params:"+new Gson().toJson(params));
			String result = getPostRequestService.requestMethod(GetPostRequestService.HTTP_POST,appendUrl,new Gson().toJson(params));
			logger.info("CloudStoreGetEventsControllerHandleSplitData,requestMethod,apiParamMap:"+result);
			
			boolean check = true;
            
            if(StringUtils.isBlank(result)){
            	dataMap.put("info","RequestService.requestMethod 返回结果为空");
            	check = false;
            }else{
            	fileUtils.bakPendingFile("stepDay"+step,result);
            }
//            test data
//            result = "{\"status\":\"ok\",\"data\":{\"events\":[{\"_id\":{\"$id\":\"5937c45d8b674caa2d3232e2\"},\"shop_id\":{\"$id\":\"590ecdf225c9fe7f4e1980c4\"},\"type\":0,\"date\":{\"sec\":1494581557,\"usec\":462000},\"additional_info\":{\"qty_diff\":-1,\"shop_from\":{\"$id\":\"561121e328499f880b0041a7\"},\"qty\":3,\"from\":\"shop\",\"sku\":\"A055F200_6685-L\",\"order_id\":{\"$id\":\"59158135e4b09f20a1de9919\"}}},{\"_id\":{\"$id\":\"5937c45d8b674caa2d3232e3\"},\"shop_id\":{\"$id\":\"590ecdf225c9fe7f4e1980c4\"},\"type\":0,\"date\":{\"sec\":1494594997,\"usec\":655000},\"additional_info\":{\"qty_diff\":1,\"shop_from\":{\"$id\":\"561121e328499f880b0041a7\"},\"qty\":2,\"from\":\"shop\",\"sku\":\"A055F200_6685-L\",\"order_id\":{\"$id\":\"5915b5b5e4b09f20a1de9bee\"}}},{\"_id\":{\"$id\":\"5937c45d8b674caa2d3232f0\"},\"shop_id\":{\"$id\":\"590ecdf225c9fe7f4e1980c4\"},\"type\":1,\"date\":{\"sec\":1494602807,\"usec\":634000},\"additional_info\":{\"qty_diff\":-1,\"shop_from\":{\"$id\":\"561121e328499f880b0041a7\"},\"from\":\"shop\",\"sku\":\"H6269720CR_1100-S\",\"order_id\":{\"$id\":\"5915d437e4b09f20a1de9eca\"}}}]}}";
//          result = "{\"status\":\"ok\",\"data\":{\"events\":[{\"_id\":{\"$id\":\"5937c45d8b674caa2d3232fd\"},\"shop_id\":{\"$id\":\"590ecdf225c9fe7f4e1980c4\"},\"type\":3,\"date\":{\"sec\":1488886056,\"usec\":830000},\"additional_info\":{\"item\":{\"_id\":{\"$id\":\"58be99a5d32af78734767811\"},\"sku\":\"16060_001-40\",\"title\":\"Maglia oversize con micro-paillettes\",\"desc\":\"Maglia oversize con micro-paillettes\",\"cur\":\"1\",\"brand\":\"Snobby Sheep\",\"qty\":0,\"stock_price\":268,\"price\":268,\"cat_ids\":[{\"$id\":\"561d7300b49dbb9c2c551c2f\"}],\"color_en\":\"panna\",\"mnf_code\":\"16060_001\",\"dim_h\":\"0\",\"color\":\"panna\",\"material_en\":\"Polyester\",\"color_shop\":\"panna\",\"sku_parent\":\"16060_001\",\"care_en\":\"Product Care: Hand wash\",\"season_shop\":\"SS17\",\"season\":\"SS17\",\"desc_en\":\"Sequined oversize sweater\",\"barcode\":\"4000001895045\",\"sex_shop\":\"Female\",\"dim_d\":\"0\",\"made\":\"Designed in Italy\",\"sex\":\"Female\",\"weight\":\"00.01\",\"made_en\":\"Designed in Italy\",\"size_shop\":\"40\",\"dim_w\":\"0\",\"size\":\"40\",\"material\":\"Cotone, Poliestere\",\"title_en\":\"Sequined oversize sweater\",\"age\":\"Adult\",\"care\":\"Product Care: Lavare a mano\",\"images\":[\"http://185.54.173.11/docs/reposImages/POTSS17_Shang0014_D/16060_001/16060_001-1.jpg\",\"http://185.54.173.11/docs/reposImages/POTSS17_Shang0014_D/16060_001/16060_001-2.jpg\"]}}},{\"_id\":{\"$id\":\"5937c45d8b674caa2d3232fc\"},\"shop_id\":{\"$id\":\"590ecdf225c9fe7f4e1980c4\"},\"type\":3,\"date\":{\"sec\":1488886056,\"usec\":830000},\"additional_info\":{\"item\":{\"_id\":{\"$id\":\"58be99a5d32af78734767812\"},\"sku\":\"16060_001-38\",\"title\":\"Maglia oversize con micro-paillettes\",\"desc\":\"Maglia oversize con micro-paillettes\",\"cur\":\"1\",\"brand\":\"Snobby Sheep\",\"qty\":0,\"stock_price\":268,\"price\":268,\"cat_ids\":[{\"$id\":\"561d7300b49dbb9c2c551c2f\"}],\"color_en\":\"panna\",\"mnf_code\":\"16060_001\",\"dim_h\":\"0\",\"color\":\"panna\",\"material_en\":\"Polyester\",\"color_shop\":\"panna\",\"sku_parent\":\"16060_001\",\"care_en\":\"Product Care: Hand wash\",\"season_shop\":\"SS17\",\"season\":\"SS17\",\"desc_en\":\"Sequined oversize sweater\",\"barcode\":\"4000001895038\",\"sex_shop\":\"Female\",\"dim_d\":\"0\",\"made\":\"Designed in Italy\",\"sex\":\"Female\",\"weight\":\"00.01\",\"made_en\":\"Designed in Italy\",\"size_shop\":\"38\",\"dim_w\":\"0\",\"size\":\"38\",\"material\":\"Cotone, Poliestere\",\"title_en\":\"Sequined oversize sweater\",\"age\":\"Adult\",\"care\":\"Product Care: Lavare a mano\",\"images\":[\"http://185.54.173.11/docs/reposImages/POTSS17_Shang0014_D/16060_001/16060_001-1.jpg\",\"http://185.54.173.11/docs/reposImages/POTSS17_Shang0014_D/16060_001/16060_001-2.jpg\"]}}},{\"_id\":{\"$id\":\"5937c45d8b674caa2d3232ff\"},\"shop_id\":{\"$id\":\"590ecdf225c9fe7f4e1980c4\"},\"type\":3,\"date\":{\"sec\":1488886056,\"usec\":831000},\"additional_info\":{\"item\":{\"_id\":{\"$id\":\"58be99a5d32af78734767814\"},\"sku\":\"16060_001-44\",\"title\":\"Maglia oversize con micro-paillettes\",\"desc\":\"Maglia oversize con micro-paillettes\",\"cur\":\"1\",\"brand\":\"Snobby Sheep\",\"qty\":0,\"stock_price\":268,\"price\":268,\"cat_ids\":[{\"$id\":\"561d7300b49dbb9c2c551c2f\"}],\"color_en\":\"panna\",\"mnf_code\":\"16060_001\",\"dim_h\":\"0\",\"color\":\"panna\",\"material_en\":\"Polyester\",\"color_shop\":\"panna\",\"sku_parent\":\"16060_001\",\"care_en\":\"Product Care: Hand wash\",\"season_shop\":\"SS17\",\"season\":\"SS17\",\"desc_en\":\"Sequined oversize sweater Ribbed neckline, cuffs and hem Asymmetric hemline Product Care: Hand wash 88%, 12%, Polyester maniche: Short Sleeves Scollo/scollatura: Round-neckedTaglia del capo indossato nella foto: SConversione taglie: ITColore: Silver, Cream\",\"barcode\":\"4000001895069\",\"sex_shop\":\"Female\",\"dim_d\":\"0\",\"made\":\"Designed in Italy\",\"sex\":\"Female\",\"weight\":\"00.01\",\"made_en\":\"Designed in Italy\",\"size_shop\":\"44\",\"dim_w\":\"0\",\"size\":\"44\",\"material\":\"Cotone, Poliestere\",\"title_en\":\"Sequined oversize sweater\",\"age\":\"Adult\",\"care\":\"Product Care: Lavare a mano\",\"images\":[\"http://185.54.173.11/docs/reposImages/POTSS17_Shang0014_D/16060_001/16060_001-1.jpg\",\"http://185.54.173.11/docs/reposImages/POTSS17_Shang0014_D/16060_001/16060_001-2.jpg\"]}}}]}}";
            //返回的events集合
			JSONArray eventsArray = null;
            if(check){
				//转换成json格式，处理结果
            	JSONObject resultJson = JSONObject.parseObject(result);
				//JSONObject resultJson = test();
				if(resultJson != null && "ok".equals(resultJson.getString("status"))){
					logger.info("CloudStoreGetEventsControllerHandleSplitData,SUCCESS rsult:"+resultJson.toString());
					dataMap.put("status", StatusType.SUCCESS);
					//获取数据
					JSONObject dataJson = resultJson.getJSONObject("data");
					
					eventsArray = dataJson.getJSONArray("events");
					
				}else if(resultJson != null && "ko".equals(resultJson.getString("status"))){
					logger.info("CloudStoreGetEventsControllerHandleSplitData, ko rsult:"+resultJson.toString());
					dataMap.put("info", resultJson.getString("messages"));
					check = false;
				}else{
					dataMap.put("info", "返回结果:"+resultJson.toString());
					logger.info("CloudStoreGetEventsControllerHandleSplitData, rsult:"+resultJson.toString());
					check = false;
				}
            }

			logger.info("CloudStoreGetEventsControllerHandleSplitData,eventsArray:"+eventsArray);
			//遍历events集合 判断类型 调用不同的处理
			if(check && eventsArray!= null && eventsArray.size()>0){
				logger.info("CloudStoreGetEventsControllerHandleSplitData,eventsArray:"+eventsArray.toJSONString());
				for(int i = 0;i<eventsArray.size();i++){
					JSONObject eventsInfo = eventsArray.getJSONObject(i);
					ResultMessage resultMessage = this.putGetEventsData(eventsInfo,vendor_id,
							eventName, nugnesExecutor,threadNum,fileUtils);
					logger.info("resultMessage : " + new Gson().toJson(resultMessage));
					logger.info("CloudStoreGetEventsControllerHandleSplitData Index :"+i);
				}
			}
            
		} catch (Exception e) {
			dataMap.put("status", StatusType.FAILURE);
			dataMap.put("info", "系统异常: "+e.getMessage());
			logger.info("CloudStoreGetEventsControllerPopulateResult,errorMessage:"+ExceptionUtils.getExceptionDetail(e));
            e.printStackTrace();
        }
    	return dataMap;
    }
    
    public ResultMessage putGetEventsData(JSONObject jsonObject,String vendor_id,
    		String eventName,ThreadPoolExecutor nugnesExecutor,int threadNum,ApiDataFileUtils fileUtils) {
		ResultMessage resultMessage = new ResultMessage();
		resultMessage.sStatus(true).sMsg("SUCCESS");
		try {
			int type = jsonObject.getInteger("type");

			if(type == Contants.EVENTS_TYPE_0 || type == Contants.EVENTS_TYPE_1 || type == Contants.EVENTS_TYPE_4) {

				// update stock
				 // 映射数据 封装VO
                logger.info("EdsAllUpdateByStockControllerExecute,mapping,start,jsonObject:"+new Gson().toJson(jsonObject)+",eventName:"+eventName);
                StockOption stockOption = iStockMapping.mapping(jsonObject);
				stockOption.setVendor_id(vendor_id);
                logger.info("EdsAllUpdateByStockControllerExecute,mapping,end,jsonObject:"+new Gson().toJson(jsonObject)+",stockOption:"+new Gson().toJson(stockOption)+",eventName:"+eventName);

                // 线程池
                logger.info("EdsAllUpdateByStockControllerExecute,execute,startDate:"+ DateUtils.getStrDate(new Date())+",jsonObject:"+new Gson().toJson(jsonObject)+",stockOption:"+new Gson().toJson(stockOption)+",eventName:"+eventName);
                CommonThreadPool.execute(eventName,nugnesExecutor,threadNum,new UpdateStockThread(stockOption,fileUtils,jsonObject));
                logger.info("EdsAllUpdateByStockControllerExecute,execute,endDate:"+ DateUtils.getStrDate(new Date())+",jsonObject:"+new Gson().toJson(jsonObject)+",stockOption:"+new Gson().toJson(stockOption)+",eventName:"+eventName);
			} else if(type == Contants.EVENTS_TYPE_2) {

				// get vendor
				 // get params
				ProductEDSManagement.VendorOptions vendorOption = productEDSManagement.getVendorOptions();
                vendorOption.setVendorId(Long.parseLong(vendor_id));

				// TODO 多个商品时,数据格式???
				JSONObject additionalInfo = jsonObject.getJSONObject("additional_info");
				JSONArray items = additionalInfo.getJSONArray("item");
				Iterator<Object> iterators =  items.iterator();
				while (iterators.hasNext()) {
					JSONObject item = (JSONObject) iterators.next();

					// put mq
					Map<String,Object> mqMap = new HashMap<>();
					mqMap.put("vendorOption",vendorOption);
					mqMap.put("responseBody",item.toJSONString());
					logger.info("CloudStoreGetEventsControllerPutGetEventsData,CloudStoreSynProduct,jsonObject:"+jsonObject.toJSONString());
					 // 映射数据 封装VO
                    ProductEDSManagement.ProductOptions productOptions = iProductMapping.mapping(mqMap);
                    ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                    vendorOptions.setVendorId(Long.parseLong(vendor_id));
                    logger.info("cloudStoreProductDAYProducerControllerExecute,initParam,productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName);
               
                    // 线程池
                    logger.info("cloudStoreProductDAYProducerControllerExecute,execute,mqMap:"+JSONObject.toJSONString(mqMap)+",startDate:"+DateUtils.getStrDate(new Date())+",productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName);
                    CommonThreadPool.execute(eventName,nugnesExecutor,threadNum,new UpdateProductThread(productOptions,vendorOptions,fileUtils,mqMap));
                    logger.info("cloudStoreProductDAYProducerControllerExecute,execute,mqMap:"+JSONObject.toJSONString(mqMap)+",endDate:"+DateUtils.getStrDate(new Date())+",productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName);
               
				}

			} else if(type == Contants.EVENTS_TYPE_3) {
				// get vendor
				ProductEDSManagement.VendorOptions vendorOption = productEDSManagement.getVendorOptions();
                vendorOption.setVendorId(Long.parseLong(vendor_id));

				// update product
				JSONObject additionalInfo = jsonObject.getJSONObject("additional_info");
				JSONObject item = additionalInfo.getJSONObject("item");

				// put mq
				Map<String,Object> mqMap = new HashMap<>();
				mqMap.put("vendorOption",vendorOption);
				mqMap.put("responseBody",item.toJSONString());
				logger.info("CloudStoreGetEventsControllerPutGetEventsData,CloudStoreSynProduct,jsonObject:"+jsonObject.toJSONString());
				 // 映射数据 封装VO
				logger.info("CloudStoreGetEventsControllerPutGetEventsData,CloudStoreSynProduct,mqMap:"+new Gson().toJson(mqMap));
                ProductEDSManagement.ProductOptions productOptions = iProductMapping.mapping(mqMap);
                ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                vendorOptions.setVendorId(Long.parseLong(vendor_id));
                logger.info("cloudStoreProductDAYProducerControllerExecute,initParam,productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName);
           
                // 线程池
                logger.info("cloudStoreProductDAYProducerControllerExecute,execute,startDate:"+DateUtils.getStrDate(new Date())+",productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName);
                CommonThreadPool.execute(eventName,nugnesExecutor,threadNum,new UpdateProductThread(productOptions,vendorOptions,fileUtils,mqMap));
                logger.info("cloudStoreProductDAYProducerControllerExecute,execute,endDate:"+DateUtils.getStrDate(new Date())+",productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName);
           
			} else if(type == Contants.EVENTS_TYPE_5){
				logger.info("updateOrderStatus,Function temporarily not on-line !");
				resultMessage.sStatus(false).sMsg("updateOrderStatus,Function temporarily not on-line !");
				//暂时不上
//				this.updateOrderStatus(jsonObject);
			} else {
				resultMessage.sStatus(false).sMsg("FAILD");
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("CloudStoreGetEventsControllerPutGetEventsData,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
			resultMessage.sStatus(false).sMsg("errorMessage : 未知异常!!!" + e.getMessage());
		}
		return resultMessage;
	}

    @Override
    public void afterPropertiesSet() throws Exception {
        // nugnes
        ThreadPoolExecutor nugnesExecutor =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        Map<String,Object> tony_day_updateProduct = new HashMap<>();
        tony_day_updateProduct.put("url","http://sandbox.cloudstore.srl/ws/getEvents");
        tony_day_updateProduct.put("limit","500");
        tony_day_updateProduct.put("offset","0");
        tony_day_updateProduct.put("merchantId","55f707f6b49dbbe14ec6354d");
        tony_day_updateProduct.put("token","018513a51480a5fd0f456ee543b7c78a");
        tony_day_updateProduct.put("store_code","daiqueren");
        tony_day_updateProduct.put("nugnesExecutor",nugnesExecutor);
        tony_day_updateProduct.put("vendor_id","16");
        tony_day_updateProduct.put("threadNum","5");
        tony_day_updateProduct.put("eventName","product_delta_update");
        tony_day_updateProduct.put("datetime",DateUtils.getStrDate(new Date()));
        tony_day_updateProduct.put("fileUtils",new ApiDataFileUtils("tony","product_delta_update"));
        // put data
        paramsMap = new HashMap<>();
        paramsMap.put("tony_day_updateProduct",tony_day_updateProduct);
    }

    
}
