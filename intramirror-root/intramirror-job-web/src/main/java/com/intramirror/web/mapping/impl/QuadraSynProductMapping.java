package com.intramirror.web.mapping.impl;

import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.product.api.service.category.ICategoryService;
import com.intramirror.product.core.impl.category.CategoryServiceImpl;
import com.intramirror.web.mapping.api.IProductMapping;
import com.intramirror.web.util.SpringContextUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Service;

import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.model.ProductEDSManagement.ProductOptions;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.mapping.api.IMapping;
import pk.shoplus.service.product.api.IProductService;
import pk.shoplus.service.product.impl.ProductServiceImpl;
import pk.shoplus.util.MapUtils;

import java.util.*;


/**
 * Created by wzh on 2017/8/23.
 */
@Service
public class QuadraSynProductMapping implements IProductMapping{

    private static Logger logger = Logger.getLogger(QuadraSynProductMapping.class);

    @Autowired
    private ICategoryService categoryService;
    
    private ProductEDSManagement productEDSManagement = new ProductEDSManagement();

    private IProductService iProductService = new ProductServiceImpl();
    
//	private static final String BASEIMGURL = "http://apiquadra.teknosis.link:89/images/";
    private static final String BASEIMGURL = "http://apiquadra.teknosis.link:444/foto?";
    
    
    /**
     * 封装商品对象
     * @param dataMap
     * @return
     * @throws Exception
     */
	@Override
	public ProductOptions mapping(Map<String, Object> dataMap) {
		logger.info("job quadra product mapping param dataMap:"+ new Gson().toJson(dataMap));
		
        //如果未注入进来,手动获取bean
        if(categoryService == null){
            categoryService = (ICategoryService)SpringContextUtils.getContext().getBean("productCategoryServiceImpl");
        }
        ProductEDSManagement.ProductOptions productOptions = productEDSManagement.getProductOptions();
        
        //封装ProductOptions 对象消息
        Map<String,Object> product = JSONObject.parseObject(dataMap.get("product").toString());
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
    		logger.info("job quadra product mapping coverImg list:"+ new Gson().toJson(imageList));
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
    		logger.info("job quadra product mapping skuOptionsList:"+ new Gson().toJson(skuOptionsList));
    		
    		//根据传过来的类目   获取映射的类目信息
            Map<String, Object> categoryMap = new HashMap<String, Object>();
            categoryMap.put("vendor_id", Long.parseLong(dataMap.get("vendor_id").toString()));
            if(product.get("SESSO_ENG") != null ){
                categoryMap.put("boutique_first_category", product.get("SESSO_ENG").toString().toLowerCase());
				productOptions.setCategory1(product.get("SESSO_ENG").toString().toLowerCase());
            }else{
            	categoryMap.put("boutique_first_category", "");
            }
            if(product.get("GRMODELLO_ITA") != null ){
                categoryMap.put("boutique_second_category", product.get("GRMODELLO_ITA").toString().toLowerCase());
				productOptions.setCategory2(product.get("GRMODELLO_ITA").toString().toLowerCase());
			}else{
            	categoryMap.put("boutique_second_category", "");
            }
            if(product.get("MODELLO_ITA") != null ){
                categoryMap.put("boutique_third_category", product.get("MODELLO_ITA").toString().toLowerCase());
				productOptions.setCategory3(product.get("MODELLO_ITA").toString().toLowerCase());
			}else{
            	categoryMap.put("boutique_third_category", "");
            }


            productOptions.setCategory_name(JSONObject.toJSONString(categoryMap));
            productOptions.setLast_check(new Date());
            logger.info("job quadra product mapping productOptions: " + new Gson().toJson(productOptions));
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
