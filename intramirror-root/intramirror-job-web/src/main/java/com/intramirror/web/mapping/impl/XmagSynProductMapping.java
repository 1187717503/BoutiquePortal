package com.intramirror.web.mapping.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.product.api.service.category.ICategoryService;
import com.intramirror.web.util.SpringContextUtils;

import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.model.ProductEDSManagement.SkuOptions;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.mapping.api.IMapping;
import pk.shoplus.service.product.api.IProductService;
import pk.shoplus.service.product.impl.ProductServiceImpl;
import pk.shoplus.util.MapUtils;

/**
 * @author yml
 * @date 2017/8/24
 */
@Service
public class XmagSynProductMapping implements IMapping{

    private static Logger logger = Logger.getLogger(XmagSynProductMapping.class);
    
    @Autowired
    private ICategoryService categoryService;

    private ProductEDSManagement productEDSManagement = new ProductEDSManagement();

    private IProductService iProductService = new ProductServiceImpl();

    @Override
    public Map<String, Object> handleMappingAndExecute(String mqData,String queueNameEnum) {
        logger.info(" start XmagSynProductMapping.handleMappingAndExecute();");
        MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());
        try {
        	//如果未注入进来,手动获取bean
            if(categoryService == null){
                categoryService = (ICategoryService)SpringContextUtils.getContext().getBean("productCategoryServiceImpl");
            }
        	logger.info("mqData : ===========>>>>>>" +mqData);
            Map<String,Object> mqDataMap = JSONObject.parseObject(mqData);
            
          //封装vendorOptions 对象消息
            ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
            vendorOptions.setStoreCode(mqDataMap.get("store_code").toString());
            vendorOptions.setApiConfigurationId(Long.parseLong(mqDataMap.get("api_configuration_id").toString()));
            vendorOptions.setVendorId(Long.parseLong(mqDataMap.get("vendor_id").toString()));
            
            //封装ProductOptions 对象消息 .substring(1, (mqDataMap.get("product").toString().length()-1)
            Map<String,Object> productMap = JSONObject.parseObject(mqDataMap.get("product").toString());
            ProductEDSManagement.ProductOptions productOptions = this.handleMappingData(productMap,vendorOptions);

            //创建商品
            logger.info("Xmag 开始调用商品创建Service by Xmag ,productOptions:" + new Gson().toJson(productOptions) + " , vendorOptions:" + new Gson().toJson(vendorOptions));
            Map<String,Object> serviceProductMap = productEDSManagement.createProduct(productOptions,vendorOptions);
            logger.info("Xmag 结束调用商品创建Service by Xmag ,serviceProductMap:" + new Gson().toJson(serviceProductMap));

            //修改商品
            if(serviceProductMap != null && serviceProductMap.get("status").equals(StatusType.PRODUCT_ALREADY_EXISTS)) {
                logger.info("Xmag 开始调用商品修改Service by Xmag ,productOptions:" + new Gson().toJson(productOptions) + " , vendorOptions:" + new Gson().toJson(vendorOptions));
                serviceProductMap = iProductService.updateProduct(productOptions,vendorOptions);
                logger.info("Xmag 结束调用商品修改Service by Xmag ,serviceProductMap:" + new Gson().toJson(serviceProductMap));
            }
            
            serviceProductMap.put("product_code",productOptions.getCode());
            serviceProductMap.put("color_code",productOptions.getColorCode());
            serviceProductMap.put("brand_id",productOptions.getBrandCode());

            return  serviceProductMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" error message : " + e.getMessage());
            mapUtils.putData("status", StatusType.FAILURE).putData("info","XmagSynProductMapping error message : " + e.getMessage());
        }
        logger.info(" end XmagSynProductMapping.handleMappingAndExecute();");
        return mapUtils.getMap();
    }

    /**
     * 数据解析封装
     * @author 123
     * @param product
     * @param vendorOptions
     * @return
     * @throws Exception
     */
    public ProductEDSManagement.ProductOptions handleMappingData(Map<String,Object> product, ProductEDSManagement.VendorOptions vendorOptions) throws Exception{
        ProductEDSManagement.ProductOptions productOptions = productEDSManagement.getProductOptions();
        //数据解析
        if (null != product && 0 < product.size()){
        	if (checkValue(product.get("product_id"))){
        		String boutiqueId = product.get("product_id").toString();
        		productOptions.setCode(boutiqueId);
        	}
        	if (checkValue(product.get("product_name"))){
        		String supplierId = product.get("product_name").toString();
        		productOptions.setBrandCode(supplierId);
        	}
        	if (checkValue(product.get("producer_id"))){
        		String brand = product.get("producer_id").toString();
        		productOptions.setBrandName(brand);
        	}
        	if (checkValue(product.get("season"))){
        		String seasonCode = product.get("season").toString();
        		productOptions.setSeasonCode(seasonCode);
        	}
        	if (checkValue(product.get("description"))){
        		String productDescription = product.get("description").toString();
        		productOptions.setDesc(productDescription);
        	}
        	String type = product.get("type")==null?" ":product.get("type").toString(); //查询二级
        	String category = product.get("category")==null?" ":product.get("type").toString(); //查询三级
        	
        	//根据传过来的类目   获取映射的类目信息
            Map<String, Object> categoryMap = new HashMap<String, Object>();
            categoryMap.put("vendor_id", vendorOptions.getVendorId());
            //字符转小写
            categoryMap.put("boutique_second_category", type.toLowerCase());
            categoryMap.put("boutique_third_category", category.toLowerCase());
            logger.info("categoryService param " + new Gson().toJson(categoryMap));
        	List<Map<String, Object>> apiCategoryMap = categoryService.getCategoryInfoByCondition(categoryMap);
        	 logger.info("categoryService result " + new Gson().toJson(apiCategoryMap));
        	if(null != apiCategoryMap && 0 < apiCategoryMap.size()) {
                productOptions.setCategoryId(apiCategoryMap.get(0).get("category_id").toString());
            }
        	
        	if (checkValue(product.get("product_material"))){
        		String composition = product.get("product_material").toString();
        		productOptions.setComposition(composition);
        	}
        	if (checkValue(product.get("product_MadeIn").toString())){
        		String madein = product.get("product_MadeIn").toString();
        		productOptions.setMadeIn(madein);
        	}
        	if (checkValue(product.get("CarryOver"))){
        		String carryover = product.get("CarryOver").toString();
        		productOptions.setCarryOver(carryover);
        	}
        	if (checkValue(product.get("supply_price"))){
        		String retailPrice = product.get("supply_price").toString();
        		productOptions.setRetailPrice(retailPrice);
        	}
        	
        	List<SkuOptions> skus = new ArrayList<SkuOptions>();
        	ProductEDSManagement management = new ProductEDSManagement();
        	//遍历子集
        	if (checkValue(product.get("items"))){
	        	Map<String, Object> itemMap = (Map<String, Object>) product.get("items");
	        	List<Map<String, Object>> items = (List<Map<String, Object>>) itemMap.get("item");
	        	//获取imgurl
	        	String pictures = "";
	        	Set<String> pic = new HashSet<>();
	        	if (null != items && 0!= items.size()){
	        		SkuOptions sku = management.getSkuOptions();
		        	for (int i = 0; i < items.size(); i++) {
		        		sku.setSize(items.get(i).get("item_size").toString());
		        		sku.setBarcodes(items.get(i).get("barcode").toString());
		        		sku.setStock(items.get(i).get("stock").toString());
		        		productOptions.setColorCode(items.get(i).get("color").toString());
		        		pic.add(items.get(i).get("pictures").toString());
		        		skus.add(sku);
					}
		        	int index = 1;
		        	for (String img : pic) {
		        		if (pic.size() > index)
		        			pictures = img+",";
		        		else
		        			pictures = pictures+img;
		        		index++;
					}
		        	productOptions.setCoverImg(pictures);
	        	}
        	}
        	productOptions.setSkus(skus);
        }
        logger.info(" productOptions filippo : " + new Gson().toJson(productOptions));
        return productOptions;
    }
    
    private boolean checkValue(Object val) {
    	if(null != val && StringUtils.isNotBlank(val.toString())){
    		return true;
    	}
    	return false;
	}
    
    
}
