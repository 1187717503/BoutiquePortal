package com.intramirror.web.mapping.impl.aiduca;

import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.product.api.service.category.ICategoryService;
import com.intramirror.web.mapping.api.IProductMapping;
import com.intramirror.web.util.SpringContextUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.model.ProductEDSManagement.ProductOptions;
import pk.shoplus.util.ExceptionUtils;

/**
 * Created by wzh on 2017/9/05.
 */
@Service
public class AiDucaSynProductMapping implements IProductMapping{

    private static Logger logger = Logger.getLogger(AiDucaSynProductMapping.class);

    @Autowired
    private ICategoryService categoryService;
    
    private ProductEDSManagement productEDSManagement = new ProductEDSManagement();

	@Override
	public ProductOptions mapping(Map<String, Object> dataMap) {
		logger.info("AiDucaSynProductMapping,inputParams,dataMap:"+JSONObject.toJSONString(dataMap));
		ProductEDSManagement.ProductOptions productOptions = productEDSManagement.getProductOptions();
		try {
			if(categoryService == null){
				categoryService = (ICategoryService)SpringContextUtils.getContext().getBean("productCategoryServiceImpl");
			}

			//封装ProductOptions 对象消息
			Map<String,Object> product = JSONObject.parseObject(dataMap.get("product").toString());

			if(product != null && product.size() > 0 ){

				if(product.get("product_id") != null){
					productOptions.setCode(product.get("product_id").toString());
				}

				if(product.get("season") != null){
					productOptions.setSeasonCode(product.get("season").toString());
				}

				if(product.get("brand") != null){
					productOptions.setBrandName(product.get("brand").toString());
				}

				if(product.get("product_var") != null && StringUtils.isNotBlank(product.get("product_var").toString())){
					productOptions.setColorCode(product.get("product_var").toString());
				}else{
					productOptions.setColorCode("#");
				}

				if(product.get("title") != null){
					productOptions.setName(product.get("title").toString());
				}

				if(product.get("market_price") != null){
					productOptions.setSalePrice(product.get("market_price").toString());
				}

				if(product.get("made") != null){
					productOptions.setMadeIn(product.get("made").toString());
				}

				if(product.get("texture") != null){
					productOptions.setComposition(product.get("texture").toString());
				}

				if(product.get("measurement") != null){
					productOptions.setHeigit(product.get("measurement").toString());
				}

				List<String> imageList = new ArrayList<String>();
				if(checkValue(product.get("item_imgs"))){
					imageList = (List<String>) com.alibaba.fastjson.JSONObject.parse(product.get("item_imgs").toString());
				}
				logger.info("job mapping AiDuca product handleMappingData coverImg list:"+ new Gson().toJson(imageList));
				productOptions.setCoverImg(new Gson().toJson(imageList));
				productOptions.setDescImg(new Gson().toJson(imageList));

				if(product.get("product_code") != null ){
					productOptions.setBrandCode(product.get("product_code").toString());
				}

				List<ProductEDSManagement.SkuOptions> skuOptionsList = new ArrayList<>();
				ProductEDSManagement.SkuOptions skuOptions = productEDSManagement.getSkuOptions();

				if(product.get("stock") != null ){
					skuOptions.setStock(product.get("stock").toString());
				}


				if(product.get("size") != null ){
					skuOptions.setSize(product.get("size").toString());
				}

				if(product.get("sku_id") != null ){
					skuOptions.setBarcodes(product.get("sku_id").toString());
				}else{
					skuOptions.setBarcodes("#");
				}

				skuOptionsList.add(skuOptions);
				productOptions.setSkus(skuOptionsList);
				logger.info("job mapping AiDuca product handleMappingData skuOptionsList:"+ new Gson().toJson(skuOptionsList));

				//根据传过来的类目获取映射的类目信息
				Map<String, Object> categoryMap = new HashMap<String, Object>();
				categoryMap.put("vendor_id", Long.parseLong(dataMap.get("vendor_id").toString()));
				if(product.get("suitable") != null ){
					categoryMap.put("boutique_first_category", product.get("suitable").toString().toLowerCase());
					productOptions.setCategory1(product.get("suitable").toString().toLowerCase());
				}else{
					categoryMap.put("boutique_first_category", "");
				}
				if(product.get("category_1") != null ){
					categoryMap.put("boutique_second_category", product.get("category_1").toString().toLowerCase());
					productOptions.setCategory2(product.get("category_1").toString().toLowerCase());
				}else{
					categoryMap.put("boutique_second_category", "");
				}
				if(product.get("category_3") != null ){
					categoryMap.put("boutique_third_category", product.get("category_3").toString().toLowerCase());
					productOptions.setCategory3(product.get("category_3").toString().toLowerCase());
				}else{
					categoryMap.put("boutique_third_category", "");
				}
				productOptions.setCategory_name(JSONObject.toJSONString(categoryMap));
			}
		} catch (Exception e) {
        	e.printStackTrace();
			logger.info("AiDucaSynProductMapping,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
		}
		logger.info("AiDucaSynProductMapping,outputParams,productOptions:"+JSONObject.toJSONString(productOptions));
		return productOptions;
	}

    private boolean checkValue(Object val) {
    	if(val != null && StringUtils.isNotBlank(val.toString())){
    		return true;
    	}
    	return false;
		
	}

}
