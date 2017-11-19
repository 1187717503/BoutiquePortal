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
                    pv = pv.replace("��", "");
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

            productOptions.setCategory1(firstCategory);
            productOptions.setCategory2(secondCategory);
            productOptions.setCategory3(threeCategory);
            logger.info(" productOptions filippo : " + new Gson().toJson(productOptions));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return productOptions;
    }
    
}
