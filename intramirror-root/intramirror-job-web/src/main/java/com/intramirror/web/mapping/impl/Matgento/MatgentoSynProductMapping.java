package com.intramirror.web.mapping.impl.Matgento;

import com.alibaba.fastjson15.JSONObject;
import com.intramirror.common.help.StringUtils;
import com.intramirror.web.mapping.api.IProductMapping;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import pk.shoplus.model.ProductEDSManagement;

/**
 * Created by dingyifan on 2018/1/10.
 */
@Service(value = "matgentoSynProductMapping")
public class MatgentoSynProductMapping implements IProductMapping{

    private static final Logger logger = Logger.getLogger(MatgentoSynProductMapping.class);

    public static ProductEDSManagement productEDSManagement = new ProductEDSManagement();

    @Override
    public ProductEDSManagement.ProductOptions mapping(Map<String, Object> bodyDataMap) {
        ProductEDSManagement.ProductOptions productOptions = productEDSManagement.getProductOptions();
        try {
            JSONObject jsonObjectData =  JSONObject.parseObject(bodyDataMap.get("Data").toString()) ;
            productOptions.setName(jsonObjectData.getString("name"))
                    .setCode(jsonObjectData.getString("product_id").trim())
                    .setSeasonCode(jsonObjectData.getString("season"))
//                    .setBrandCode(jsonObjectData.getString("supplier_sku").trim())
//                    .setCarryOver("")
                    .setBrandName(jsonObjectData.getString("brand"))
                    .setColorCode("") // TODO
                    .setColorDesc(jsonObjectData.getString("color"))
//                    .setCategory_boutique_id("")
//                    .setCategoryId("")
                    .setDesc(jsonObjectData.getString("description"))
                    .setComposition(jsonObjectData.getString("composition"))
                    .setMadeIn(jsonObjectData.getString("madein"))
//                    .setSizeFit("")
//                    .setCoverImg(jsonObjectData.getString("cover_img"))
//                    .setDescImg(jsonObjectData.getString("description_img"))
//                    .setWeight(jsonObjectData.getString("weight"))
//                    .setLength(jsonObjectData.getString("length"))
//                    .setWidth(jsonObjectData.getString("width"))
//                    .setHeigit(jsonObjectData.getString("height"))
                    .setSalePrice(jsonObjectData.getString("price"))
                    .setCategory1(jsonObjectData.getString("department"))
                    .setCategory2(jsonObjectData.getString("category"))
                    .setCategory3(jsonObjectData.getString("subcategory"));

            // sku
            ProductEDSManagement.SkuOptions sku = productEDSManagement.getSkuOptions();
            sku.setSize(jsonObjectData.getString("size"));
            sku.setStock(jsonObjectData.getString("qty"));
            sku.setBarcodes(jsonObjectData.getString("stock_id"));

            String stock = bodyDataMap.get("stock") == null?"":bodyDataMap.get("stock").toString();
            if(stock.equals("0")) {
                sku.setStock("0");
            }
            productOptions.getSkus().add(sku);

            // image
            List<String> images = new ArrayList<>();
            for(int i = 0;i<20;i++){
                String imagePath = jsonObjectData.getString("pic"+i);
                if(StringUtils.isNotBlank(imagePath)) {
                    images.add(imagePath);
                }
            }
            productOptions.setCoverImg(JSONObject.toJSONString(images));
            productOptions.setDescImg(JSONObject.toJSONString(images));

            // brandID , colorCode
            String supplier_sku = jsonObjectData.getString("supplier_sku").trim();
            String[] suppliers = supplier_sku.split("-");
            String BrandID = suppliers[0]+suppliers[1];
            String colorCode = suppliers[2];
            productOptions.setBrandCode(BrandID);
            productOptions.setColorCode(colorCode);

            /*productOptions.setSeasonCode("17FW");
            productOptions.setCategory1("MEN");
            productOptions.setCategory2("Clothing");
            productOptions.setCategory3("Beachwear");
            productOptions.setBrandName("Gucci");
            productOptions.setColorCode("123");*/
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("MatgentoSynProductMapping,ErrorMessage:"+e);
        }
        return productOptions;
    }

}
