package com.intramirror.web.mapping.impl;

import java.util.*;

import com.alibaba.fastjson15.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sql2o.Connection;

import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.product.api.service.category.ICategoryService;
import com.intramirror.web.mapping.api.IProductMapping;
import com.intramirror.web.util.SpringContextUtils;

import pk.shoplus.DBConnector;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.model.ProductEDSManagement.ProductOptions;
import pk.shoplus.model.ProductEDSManagement.SkuOptions;
import pk.shoplus.util.ExceptionUtils;

@Service(value = "xmagSynProductAllMapper")
public class XmagSynProductAllMapper implements IProductMapping {

	private static Logger logger = Logger.getLogger(XmagSynProductAllMapper.class);
	
	public static ProductEDSManagement productEDSManagement = new ProductEDSManagement();
	
	@Autowired
    private ICategoryService categoryService;
	
	@Override
	public ProductOptions mapping(Map<String, Object> bodyDataMap) {
		logger.info("XmagSynProductAllMapperMapping,inputParams,bodyDataMap:"+new Gson().toJson(bodyDataMap));
        Connection conn = null;
        ProductEDSManagement.ProductOptions productOptions = productEDSManagement.getProductOptions();
        try {
        	conn = DBConnector.sql2o.open();
        	//如果未注入进来,手动获取bean
            if(categoryService == null){
                categoryService = (ICategoryService)SpringContextUtils.getContext().getBean("productCategoryServiceImpl");
            }
        	logger.info("mqData : ===========>>>>>>" +bodyDataMap);
        	//封装vendorOptions 对象消息
            ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
            vendorOptions.setVendorId(Long.parseLong(bodyDataMap.get("vendor_id").toString()));
            //封装ProductOptions 对象消息 .substring(1, (mqDataMap.get("product").toString().length()-1)
            Map<String,Object> productMap = JSONObject.parseObject(bodyDataMap.get("product").toString());
            productOptions = this.handleMappingData(productMap,vendorOptions);
            productOptions.setLast_check(new Date());
            if(conn != null){conn.close();}
            logger.info("XmagSynProductAllMapperMapping,outParams,productOptions:"+new Gson().toJson(productOptions));
	    } catch (Exception e) {
            logger.info("XmagSynProductAllMapperMapping,errorMessage:"+ ExceptionUtils.getExceptionDetail(e)
            +",bodyDataMap:"+new Gson().toJson(bodyDataMap));
            if(conn != null){conn.close();}
            e.printStackTrace();
        } finally {
            if(conn != null){conn.close();}
        }
        logger.info(" end XmagSynProductMapping.handleMappingAndExecute();");
        return productOptions;
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
        		productOptions.setName(productDescription);
        	}

        	if(checkValue(product.get("product_Note"))){
        		String productNote = product.get("product_Note").toString();
        		productOptions.setDesc(productNote);
			}
        	
        	String type = product.get("type")==null?" ":product.get("type").toString(); //查询一级
        	String category = product.get("category")==null?" ":product.get("category").toString(); //查询三级
        	String SubCategory = product.get("SubCategory")==null?" ":product.get("SubCategory").toString(); //查询三级
        	//根据传过来的类目   获取映射的类目信息
            Map<String, Object> categoryMap = new HashMap<String, Object>();
            categoryMap.put("vendor_id", vendorOptions.getVendorId());
            //字符转小写
            categoryMap.put("boutique_second_category", category.toLowerCase());
            categoryMap.put("boutique_third_category", SubCategory);
            categoryMap.put("boutique_first_category", type.toLowerCase());
            logger.info("categoryService param " + new Gson().toJson(categoryMap));
			productOptions.setCategory_name(JSONObject.toJSONString(categoryMap));
			List<Map<String, Object>> apiCategoryMap = categoryService.getCategoryByCondition(categoryMap);
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

        		if(carryover.equals("CO")){
        		    productOptions.setSeasonCode("CO");
                }
        	}
        	if (checkValue(product.get("supply_price"))){
        		String retailPrice = product.get("supply_price").toString();
        		productOptions.setSalePrice(retailPrice);
        	}
        	
        	List<SkuOptions> skus = new ArrayList<SkuOptions>();
        	ProductEDSManagement management = new ProductEDSManagement();
        	//遍历子集
        	if (checkValue(product.get("items"))){
	        	Map<String, Object> itemMap = JSONObject.parseObject(product.get("items").toString());
	        	if (checkValue(itemMap.get("item"))){
					List<Map<String, Object>> items = (List<Map<String, Object>>) itemMap.get("item");
		        	//获取imgurl
		        	String pictures = "";
		        	Set<String> pic = new HashSet<>();
		        	if (null != items && 0< items.size()){

			        	for (int i = 0; i < items.size(); i++) {
							SkuOptions sku = management.getSkuOptions();
			        		sku.setSize(items.get(i).get("item_size").toString());
			        		sku.setBarcodes(items.get(i).get("barcode").toString());
			        		sku.setStock(items.get(i).get("stock").toString());
			        		productOptions.setColorCode(items.get(i).get("color").toString());
//
							try {
								JSONArray skuImages = JSONArray.parseArray(items.get(i).get("pictures").toString());
								for(Object image : skuImages) {
									pic.add(image.toString());
								}
							} catch (Exception e) {
								e.printStackTrace();
								logger.info("XmagSynProductAllMapperhandleMappingData,convertImage:"+JSONObject.toJSONString(items));
							}
			        		skus.add(sku);
						}
			        	productOptions.setCoverImg(JSONObject.toJSONString(pic));
		        	}
	        	}
        	}
        	productOptions.setSkus(skus);
        }
        logger.info(" productOptions filippo : " + new Gson().toJson(productOptions));
//		productOptions.setCategoryId("1646");
//		productOptions.setBrandName("Gucci");
//		productOptions.setSeasonCode("078");
        return productOptions;
    }
    
    private boolean checkValue(Object val) {
    	if(null != val && StringUtils.isNotBlank(val.toString())){
    		return true;
    	}
    	return false;
	}
}
