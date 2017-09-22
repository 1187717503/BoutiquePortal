package com.intramirror.web.mapping.impl.filippo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.sql2o.Connection;

import com.google.gson.Gson;
import com.intramirror.web.mapping.api.IProductMapping;

import difflib.DiffRow;
import pk.shoplus.DBConnector;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.service.MappingCategoryService;

@Service(value = "filippoSynProductMapping")
public class FilippoSynProductMapping implements IProductMapping {

    private final static Logger logger = Logger.getLogger(FilippoSynProductMapping.class);

    private static String propertyName = "ART_ID|VAR_ID|ART_FID|STG|BND_ID|BND_NAME|ART|ART_VAR|ART_FAB|ART_COL|SR_ID|SR_DES|GRP_ID|GRP_DES|SUB_GRP_ID|SUB_GRP_DES|ART_DES|COL_ID|COL_DES|REF|EUR|TG_ID|TG|QTY|MADEIN|WV|COMP|IMGTYP|IMG";

    private ProductEDSManagement productEDSManagement = new ProductEDSManagement();

	@Override
	public ProductEDSManagement.ProductOptions mapping(Map<String, Object> bodyDataMap) {
		logger.info(" start FilippoSynProductMapping.handleMappingAndExecute();");
		 ProductEDSManagement.ProductOptions productOptions = productEDSManagement.getProductOptions();
		try {
			ProductEDSManagement.VendorOptions vendorOption = new ProductEDSManagement().getVendorOptions();
			vendorOption.setVendorId(Long.parseLong(bodyDataMap.get("vendor_id").toString()));
			String propertyValue = bodyDataMap.get("product_data").toString();
			String full_update_product = bodyDataMap.get("full_update_product") == null ? "0" : bodyDataMap.get("full_update_product").toString();
			productOptions = this.handleMappingData(propertyValue,vendorOption);
			productOptions.setFullUpdateProductFlag(full_update_product);
		} catch (Exception e) {
			e.printStackTrace();
            logger.error("FilippoSynProductMapping error message : " + e.getMessage());
		}
		logger.info(" end FilippoSynProductMapping.handleMappingAndExecute();");
		return productOptions;
	}

	public ProductEDSManagement.ProductOptions handleMappingData(String propertyValue, ProductEDSManagement.VendorOptions vendorOptions) throws Exception{

        if(StringUtils.isNotBlank(propertyValue) && propertyValue.contains("newLine")) {
            DiffRow diffRow = new Gson().fromJson(propertyValue, DiffRow.class);
            propertyValue = diffRow.getNewLine();
        }

        ProductEDSManagement.ProductOptions productOptions = productEDSManagement.getProductOptions();
        Connection conn = null;
        try {
            String[] propertyNames = propertyName.split("\\u007C");
            String[] propertyValues = propertyValue.split("\\u007C");
            ProductEDSManagement.SkuOptions skuOptions = productEDSManagement.getSkuOptions();
            skuOptions.setBarcodes("#");
            String brandID = "";
            String firstCategory = "";
            String secondCategory = "";
            String threeCategory = "";
            for(int i = 0,iLen = propertyNames.length;i<iLen;i++) {
                String pn = propertyNames[i];
                if(StringUtils.isNotBlank(pn)) {
                    String pv = propertyValues[i];
                    pv = pv.replace("\"","");
                    pv = pv.replace("<br>","");
                    pv = pv.replace("??","");
                    pv = pv.replace("×","");
                    if(pn.equals("VAR_ID")) {productOptions.setCode(pv);}
                    else if(pn.equals("STG")) {productOptions.setSeasonCode(pv);}
                    else if(pn.equals("BND_NAME")){productOptions.setBrandName(pv);}
                    else if(pn.equals("ART")) {brandID = brandID + pv;}
                    else if(pn.equals("ART_VAR")) {brandID = brandID + pv;}
                    else if(pn.equals("ART_FAB")) {brandID = brandID + pv;}
                    else if(pn.equals("ART_COL")) {productOptions.setColorCode(pv);}
                    else if(pn.equals("SR_DES")) {firstCategory = pv;}
                    else if(pn.equals("GRP_DES")) {secondCategory = pv;}
                    else if(pn.equals("SUB_GRP_DES")) {threeCategory = pv;productOptions.setName(pv);}
                    else if(pn.equals("REF")) {productOptions.setSalePrice(pv);}
                    else if(pn.equals("TG_ID")) {skuOptions.setSizeid(pv);}
                    else if(pn.equals("TG")) {skuOptions.setSize(pv);}
                    else if(pn.equals("QTY")) {skuOptions.setStock(pv);}
                    else if(pn.equals("MADEIN")) {productOptions.setMadeIn(pv);}
                    else if(pn.equals("WV")) {}
                    else if(pn.equals("COMP")) {productOptions.setComposition(pv);}
                    else if(pn.equals("IMG")) {
                        List<String> images = new ArrayList<>();
                        String prefix = "http://p.filmar.eu:2060/?";
                        images.add(prefix + pv);
                        productOptions.setDescImg(new Gson().toJson(images));
                        productOptions.setCoverImg(new Gson().toJson(images));
                        productOptions.setImgByFilippo(pv);
                    }
                }
            }
            if(StringUtils.isNotBlank(brandID)) {productOptions.setBrandCode(brandID);}

            List<ProductEDSManagement.SkuOptions> skuOptionsList = new ArrayList<>();
            skuOptionsList.add(skuOptions);
            productOptions.setSkus(skuOptionsList);

            conn = DBConnector.sql2o.open();
            MappingCategoryService mappingCategoryService = new MappingCategoryService(conn);

            /*// -- test
            String sql = "insert into `intra-mirror-prd`.`api_category_map` ( `boutique_third_category`, `boutique_second_category`, `category_id`, `second_category`, `boutique_first_category`, `third_category`, `enabled`, `api_configuration_id`, `first_category`, `created_at`, `updated_at`) \n" +
                    "values ( '"+threeCategory+"', '"+secondCategory+"', '1518', 'Clothing', '"+firstCategory+"', 'Beachwear', b'1', '55', 'Men','2017-07-06','2017-07-06');\n";
            logger.info("filippo test : " + sql);
            FileUtil.writeFileAppendContent("/opt/data/test/api_category_map.sql",sql);
            // -- test*/

            List<Map<String, Object>> apiCategoryMap = mappingCategoryService.getMappingCategoryInfoByCondition(vendorOptions.getVendorId().toString(),firstCategory,secondCategory,threeCategory);
            if(apiCategoryMap != null && apiCategoryMap.size() > 0) {
                productOptions.setCategoryId(apiCategoryMap.get(0).get("category_id").toString());
            }
            logger.info(" productOptions filippo : " + new Gson().toJson(productOptions));
        } catch (Exception e) {
            if(conn != null) {conn.close();}
            e.printStackTrace();
            throw e;
        } finally {
            if(conn != null) {conn.close();}
        }
        return productOptions;
    }
    
}
