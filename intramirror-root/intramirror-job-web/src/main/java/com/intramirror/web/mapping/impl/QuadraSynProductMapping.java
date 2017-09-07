package com.intramirror.web.mapping.impl;

import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.product.api.service.category.ICategoryService;
import com.intramirror.product.core.impl.category.CategoryServiceImpl;
import com.intramirror.web.util.SpringContextUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Service;

import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.mapping.api.IMapping;
import pk.shoplus.service.product.api.IProductService;
import pk.shoplus.service.product.impl.ProductServiceImpl;
import pk.shoplus.util.MapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * Created by wzh on 2017/8/23.
 */
@Service
public class QuadraSynProductMapping implements IMapping{

    private static Logger logger = Logger.getLogger(QuadraSynProductMapping.class);

    @Autowired
    private ICategoryService categoryService;
    
    private ProductEDSManagement productEDSManagement = new ProductEDSManagement();

    private IProductService iProductService = new ProductServiceImpl();
    
	private static final String BASEIMGURL = "http://apiquadra.teknosis.link:89/images/";
    

    @Override
    public Map<String, Object> handleMappingAndExecute(String mqData,String queueNameEnum) {
        logger.info(" start QuadraSynProductMapping.handleMappingAndExecute();");
        
        //如果未注入进来,手动获取bean
        if(categoryService == null){
            categoryService = (ICategoryService)SpringContextUtils.getContext().getBean("productCategoryServiceImpl");
        }


        
        MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());
        try {
        	//获取mq 中消息
            Map<String,Object> mqDataMap = JSONObject.parseObject(mqData);
            
            //封装vendorOptions 对象消息
            ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
            vendorOptions.setStoreCode(mqDataMap.get("store_code").toString());
            vendorOptions.setApiConfigurationId(Long.parseLong(mqDataMap.get("api_configuration_id").toString()));
            vendorOptions.setVendorId(Long.parseLong(mqDataMap.get("vendor_id").toString()));
            
            //封装ProductOptions 对象消息
            Map<String,Object> productMap = JSONObject.parseObject( mqDataMap.get("product").toString());
//            Map<String,Object> productMap = JSONObject.parseObject(mqDataMap.toString());
            ProductEDSManagement.ProductOptions productOptions = this.handleMappingData(productMap,vendorOptions);

            //创建商品
            logger.info("Quadra开始调用商品创建Service by Quadra,productOptions:" + new Gson().toJson(productOptions) + " , vendorOptions:" + new Gson().toJson(vendorOptions));
            Map<String,Object> serviceProductMap = productEDSManagement.createProduct(productOptions,vendorOptions);
            logger.info("Quadra结束调用商品创建结束Service by Quadra,productOptions:" + new Gson().toJson(productOptions)+ " , vendorOptions:" + new Gson().toJson(vendorOptions));
            logger.info("Quadra结束调用商品创建Service by Quadra,serviceProductMap:" + new Gson().toJson(serviceProductMap));

            //修改商品
            if(serviceProductMap != null && serviceProductMap.get("status").equals(StatusType.PRODUCT_ALREADY_EXISTS)) {

                logger.info("Quadra开始调用商品修改Service by Quadra,productOptions:" + new Gson().toJson(productOptions) + " , vendorOptions:" + new Gson().toJson(vendorOptions));
                serviceProductMap = iProductService.updateProduct(productOptions,vendorOptions);
                logger.info("Quadra结束调用商品修改Service by Quadra,serviceProductMap:" + new Gson().toJson(serviceProductMap));
            }
            
            serviceProductMap.put("product_code",productOptions.getCode());
            serviceProductMap.put("color_code",productOptions.getColorCode());
            serviceProductMap.put("brand_id",productOptions.getBrandCode());

            return  serviceProductMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" error message : " + e.getMessage());
            mapUtils.putData("status", StatusType.FAILURE).putData("info","QuadraSynProductMapping error message : " + e.getMessage());
        }
        logger.info(" end QuadraSynProductMapping.handleMappingAndExecute();");
        return mapUtils.getMap();
    }

    
    
    
    
    
    
    
    
    
    /**
     * 封装商品对象
     * @param product
     * @param vendorOptions
     * @return
     * @throws Exception
     */
    public ProductEDSManagement.ProductOptions handleMappingData(Map<String,Object> product, ProductEDSManagement.VendorOptions vendorOptions) throws Exception{
    	
        ProductEDSManagement.ProductOptions productOptions = productEDSManagement.getProductOptions();
    	if(product != null && product.size() > 0 ){
    		
    		if(product.get("CODICE") != null){
        		productOptions.setCode(product.get("CODICE").toString());
    		}

    		if(product.get("IDSTAGIONE") != null){
        		productOptions.setSeasonCode(product.get("IDSTAGIONE").toString());
    		}
//    		productOptions.setSeasonCode("A17");

    		if(product.get("BRAND") != null){
        		productOptions.setBrandName(product.get("BRAND").toString());
    		}

    		if(product.get("IDCOLORE") != null){
        		productOptions.setColorCode(product.get("IDCOLORE").toString());
    		}

    		if(product.get("DESCRIZIONE_CORTA") != null){
        		productOptions.setName(product.get("DESCRIZIONE_CORTA").toString());
    		}

    		if(product.get("LISTINO") != null){
        		productOptions.setSalePrice(product.get("LISTINO").toString());
    		}

//    		productOptions.setMadeIn(product.get("KEY").toString());
//    		productOptions.setComposition(product.get("KEY").toString());	
//    		productOptions.setDescImg(product.get("KEY").toString());
    		
    		List<String> imageList = new ArrayList<String>();
    		if(checkValue(product.get("FOTO1"))){
        		imageList.add(BASEIMGURL + product.get("FOTO1").toString());
    		}
    		if(checkValue(product.get("FOTO2"))){
        		imageList.add(BASEIMGURL + product.get("FOTO2").toString());
    		}
    		if(checkValue(product.get("FOTO3"))){
        		imageList.add(BASEIMGURL + product.get("FOTO3").toString());
    		}
    		if(checkValue(product.get("FOTO4"))){
        		imageList.add(BASEIMGURL + product.get("FOTO4").toString());
    		}
    		if(checkValue(product.get("FOTO5"))){
        		imageList.add(BASEIMGURL + product.get("FOTO5").toString());
    		}
    		if(checkValue(product.get("FOTO6"))){
        		imageList.add(BASEIMGURL + product.get("FOTO6").toString());
    		}
    		if(checkValue(product.get("FOTO7"))){
        		imageList.add(BASEIMGURL + product.get("FOTO7").toString());
    		}
    		if(checkValue(product.get("FOTO8"))){
        		imageList.add(BASEIMGURL + product.get("FOTO8").toString());
    		}
    		logger.info("quadra product handleMappingData coverImg list:"+ new Gson().toJson(imageList));
    		productOptions.setCoverImg(new Gson().toJson(imageList));
    		
//    		productOptions.setImgByFilippo(product.get("KEY").toString());
    		if(product.get("ARTICOLO") != null ){
        		productOptions.setBrandCode(product.get("ARTICOLO").toString());
    		}

            List<ProductEDSManagement.SkuOptions> skuOptionsList = new ArrayList<>();
            ProductEDSManagement.SkuOptions skuOptions = productEDSManagement.getSkuOptions();
            skuOptions.setBarcodes("#");
            
            if(product.get("DISPO") != null ){
                skuOptions.setStock(product.get("DISPO").toString());
            }

            
            if(product.get("TAGLIA") != null ){
                skuOptions.setSize(product.get("TAGLIA").toString());
            }

            skuOptionsList.add(skuOptions);
    		productOptions.setSkus(skuOptionsList);
    		logger.info("quadra product handleMappingData skuOptionsList:"+ new Gson().toJson(skuOptionsList));
    		
    		//根据传过来的类目   获取映射的类目信息
            Map<String, Object> categoryMap = new HashMap<String, Object>();
            categoryMap.put("vendor_id", vendorOptions.getVendorId());
            if(product.get("SESSO_ENG") != null ){
                categoryMap.put("boutique_first_category", product.get("SESSO_ENG").toString().toLowerCase());
            }else{
            	categoryMap.put("boutique_first_category", "");
            }
            if(product.get("GRMODELLO_ITA") != null ){
                categoryMap.put("boutique_second_category", product.get("GRMODELLO_ITA").toString().toLowerCase());
            }else{
            	categoryMap.put("boutique_second_category", "");
            }
            if(product.get("MODELLO_ITA") != null ){
                categoryMap.put("boutique_third_category", product.get("MODELLO_ITA").toString().toLowerCase());
            }else{
            	categoryMap.put("boutique_third_category", "");
            }


            
    		logger.info("quadra product handleMappingData getMappingCategoryInfoByCondition param:"+ new Gson().toJson(categoryMap));
            List<Map<String, Object>> apiCategoryMap = categoryService.getMappingCategoryInfoByCondition(categoryMap);
            if(apiCategoryMap != null && apiCategoryMap.size() > 0) {
                productOptions.setCategoryId(apiCategoryMap.get(0).get("category_id").toString());
            }
//            productOptions.setCategoryId("1532");
            logger.info(" productOptions quadra : " + new Gson().toJson(productOptions));
    	}
		return productOptions;
    }
    
    
    private boolean checkValue(Object val) {
    	if(val != null && StringUtils.isNotBlank(val.toString())){
    		return true;
    	}
    	return false;
		
	}

}
