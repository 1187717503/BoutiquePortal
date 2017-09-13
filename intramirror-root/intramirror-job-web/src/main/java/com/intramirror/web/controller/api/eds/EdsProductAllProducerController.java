package com.intramirror.web.controller.api.eds;

import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.web.mapping.api.IDataMapping;
import com.intramirror.web.util.GetPostRequestUtil;
import com.intramirror.web.vo.ApiProductVo;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pk.shoplus.model.EDSProduct;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.model.Result;
import pk.shoplus.service.request.impl.GetPostRequestService;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.MapUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * eds all update product
 */
@Controller
@RequestMapping("/eds")
public class EdsProductAllProducerController implements InitializingBean {

    // logger
    private final Logger logger = Logger.getLogger(EdsProductAllProducerController.class);

    // getpost util
    private static GetPostRequestUtil getPostRequestUtil = new GetPostRequestUtil();

    // create product
    public static ProductEDSManagement productEDSManagement = new ProductEDSManagement();

    // mapping
    @Resource(name = "edsProductMapping")
    private IDataMapping iDataMapping;

    // init params
    private Map<String,Object> paramsMap;

    @RequestMapping("/syn_all_product_producer")
    @ResponseBody
    public Map<String,Object> execute(@Param(value = "name")String name) {
        MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());
        logger.info("EdsProductAllProducerControllerExecute,startExecute,name:"+name);

        // check name
        if(StringUtils.isBlank(name)) {
            return mapUtils.putData("status",StatusType.FAILURE).putData("info","name is null !!!").getMap();
        }

        // get params
        Map<String,Object> param = (Map<String, Object>) paramsMap.get("nugnes");
        String url = param.get("url").toString();
        String store_code = param.get("store_code").toString();
        String vendor_id = param.get("vendor_id").toString();
        int limit = Integer.parseInt(param.get("limit").toString());
        int offset = Integer.parseInt(param.get("offset").toString());
        int threadNum = Integer.parseInt(param.get("threadNum").toString());

        try {
            while (true) {
                String appendUrl = url + "?storeCode=" + store_code + "&limit=" + limit +"&offset=" + offset;

                logger.info("EdsProductAllProducerControllerExecute,startRequestMethod,appendUrl:"+appendUrl);
                String responseData = getPostRequestUtil.requestMethod(GetPostRequestService.HTTP_GET,appendUrl,null);
                if(StringUtils.isBlank(responseData)) {
                    logger.info("EdsProductAllProducerControllerExecute,");
                    break;
                }
                logger.info("EdsProductAllProducerControllerExecute,endRequestMethod,appendUrl:"+appendUrl+",responseData:"+responseData);

                Map<String, Result> map = (Map<String, Result>) JSONObject.parse(responseData);
                Map<String, Object> mapResult = (Map<String, Object>) map.get("results");
                List<EDSProduct> edsProductList = (List<EDSProduct>) mapResult.get("items");
                if(edsProductList == null || edsProductList.size() == 0) {break;}

                List<ApiProductVo> apiProductVos = new ArrayList<>();
                for(int i = 0,len = edsProductList.size();i<len;i++){
                    Map<String,Object> mqDataMap = new HashMap<String,Object>();
                    mqDataMap.put("reqCode", mapResult.get("reqCode"));
                    mqDataMap.put("count", mapResult.get("count"));
                    mqDataMap.put("product", edsProductList.get(i));
                    mqDataMap.put("store_code", store_code);
                    mqDataMap.put("vendor_id", vendor_id);
                    mqDataMap.put("full_update_product","1");

                    ProductEDSManagement.ProductOptions productOptions = iDataMapping.mapping(mqDataMap);
                    ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                    vendorOptions.setVendorId(Long.parseLong(vendor_id));

                    logger.info("EdsProductAllProducerControllerExecute,initParam,productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions));

                    if(productOptions != null) {
                        if(apiProductVos.size() == threadNum) {
                            apiProductVos = new ArrayList<>();
                        }

                        ApiProductVo apiProductVo = new ApiProductVo();
                        apiProductVo.setProductOptions(productOptions);
                        apiProductVo.setVendorOptions(vendorOptions);
                        apiProductVos.add(apiProductVo);
                    } else {
                        // TODO
                        logger.info("EdsProductAllProducerControllerExecute,errorInitParam,productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions));
                    }
                }

                if(apiProductVos.size() > 0 && apiProductVos.size() <= threadNum){

                } else {
                    logger.info("EdsProductAllProducerControllerExecute,apiProductVos length is error !!!" + new Gson().toJson(apiProductVos));
                }

                offset = offset + limit;
                break;
            }
            logger.info("EdsProductAllProducerControllerExecute,executeEnd,offset:"+offset+",limit:"+limit+",url:"+url+",store_code:"+store_code);
            mapUtils.putData("status",StatusType.SUCCESS).putData("info","success");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("EdsProductAllProducerControllerExecute,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
        }

        logger.info("EdsProductAllProducerControllerExecute,endExecute,mapUtils:"+new Gson().toJson(mapUtils));
        return mapUtils.getMap();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // nugnes
        Map<String,Object> object = new HashMap<>();
        object.put("url","http://nugnes.edstema.it/api/v3.0/products/condensed");
        object.put("vendor_id","9");
        object.put("store_code","X3ZMV");
        object.put("limit","100");
        object.put("offset","0");
        object.put("threadNum","30");

        // put data
        paramsMap = new HashMap<>();
        paramsMap.put("nugnes",object);
    }
}
