package com.intramirror.web.mapping.impl.eb;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intramirror.utils.transform.JsonTransformUtil;
import com.intramirror.web.mapping.api.IProductMapping;
import com.intramirror.web.mapping.impl.gibot.GibotProductMapping;
import java.util.Date;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.util.ExceptionUtils;

/**
 * Created on 2018/2/23.
 *
 * @author YouFeng.Zhu
 */
@Service(value = "ebProductMapping")
public class ebProductMapping implements IProductMapping {
    // logger
    private static final Logger LOGGER = Logger.getLogger(GibotProductMapping.class);

    @Override
    public ProductEDSManagement.ProductOptions mapping(Map<String, Object> bodyDataMap) {
        LOGGER.info("ebProductMapping,inputParams,bodyDataMap:" + JSONObject.toJSONString(bodyDataMap));
        ProductEDSManagement.ProductOptions productOptions = new ProductEDSManagement.ProductOptions();
        try {
            JSONObject jsonObjectData = JSONObject.parseObject(JsonTransformUtil.toJson(bodyDataMap.get("product")));
            String designerIdColorCode = jsonObjectData.getString("ProductID");
            String[] temp = designerIdColorCode.split("_");
            String designerId = temp[0];
            String colorCode = "";
            if (temp.length >= 2) {
                colorCode = temp[1];
            }
            productOptions
                    .setName(jsonObjectData.getString("Product_Name"))
                    .setCode(jsonObjectData.getString("SKU").trim())
                    .setSeasonCode(jsonObjectData.getString("CarryOver").equals("true") ? "Carryover" : jsonObjectData.getString("Season_Code"))

                    .setBrandCode(designerId)
                    .setCarryOver("") //TODO
                    .setBrandName(jsonObjectData.getString("Product_Brand").trim())

                    .setColorCode(colorCode)
                    .setColorDesc("Color")
                    .setCategory_boutique_id("")
                    .setCategoryId("")//TODO
                    .setDesc(jsonObjectData.getString("Description"))
                    .setComposition(jsonObjectData.getString("Product_Material"))
                    .setMadeIn(jsonObjectData.getString("Product_MADEin"))
                    .setSizeFit("")
                    .setCoverImg(jsonObjectData.getString("Picture"))
                    .setDescImg(jsonObjectData.getString("Picture"))
                    .setWeight("")
                    .setLength("")
                    .setWidth("")
                    .setHeigit("")
                    .setSalePrice(jsonObjectData.getString("Market_Price"))
                    .setCategory1(jsonObjectData.getString("Gender"))
                    .setCategory2(jsonObjectData.getString("group_name"))//todo
                    .setCategory3(jsonObjectData.getString("Category"))

//                    .setSeasonCode("18SS") //test
                    //                    .setBrandName("GUCCI") //test
                    //                    .setCategory1("Men")
                    //                    .setCategory2("Clothing")//todo
                    //                    .setCategory3("Winter coats")
                    .setLast_check(new Date());

            JSONArray skus = jsonObjectData.getJSONArray("Stock_Item");
            for (Object it : skus) {
                JSONObject item = (JSONObject) it;
                ProductEDSManagement.SkuOptions sku = new ProductEDSManagement.SkuOptions();
                sku.setBarcodes(item.getString("barcode"));
                sku.setSize(item.getString("Size"));
                sku.setStock(item.getString("Stock"));
                sku.setBoutique_sku_id(item.getString("SKU_item"));
                productOptions.getSkus().add(sku);
            }

            //            String carryOver = jsonObjectData.getString("carry_over");
            //            if (StringUtils.isNotBlank(carryOver) && StringUtils.trim(carryOver).equalsIgnoreCase("CO")) {
            //                productOptions.setSeasonCode("CO");
            //            }

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("ebProductMapping,errorMessage:" + ExceptionUtils.getExceptionDetail(e));
        }
        LOGGER.info("ebProductMapping,outputParams,productOptions:" + JSONObject.toJSONString(productOptions));
        return productOptions;
    }
}
