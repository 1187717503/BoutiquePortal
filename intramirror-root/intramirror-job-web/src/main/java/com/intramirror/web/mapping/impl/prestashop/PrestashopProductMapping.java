package com.intramirror.web.mapping.impl.prestashop;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intramirror.web.mapping.api.IProductMapping;
import java.util.Date;
import java.util.Map;
import org.springframework.stereotype.Service;
import pk.shoplus.model.ProductEDSManagement;

@Service
public class PrestashopProductMapping implements IProductMapping {

    private static ProductEDSManagement productEDSManagement = new ProductEDSManagement();

    @Override
    public ProductEDSManagement.ProductOptions mapping(Map<String, Object> bodyDataMap) {
        ProductEDSManagement.ProductOptions productOptions = productEDSManagement.getProductOptions();
        try {
            // @formatter:off
            JSONObject body =  JSONObject.parseObject(bodyDataMap.get("Data").toString()) ;
            productOptions.setName(body.getString("name"))
                    .setCode(body.getString("id_product").trim())
                    .setSeasonCode(body.getString("season"))
                    .setBrandCode(body.getString("reference").trim())
                    .setCarryOver("")
                    .setBrandName(body.getString("manufacturer").trim())
                    .setColorCode(body.getString("manufacturer_color"))
                    .setColorDesc(body.getString("color"))
                    .setCategory_boutique_id("")
                    .setCategoryId("")
                    .setDesc(body.getString("description"))
                    .setComposition(body.getString("composition"))
                    .setMadeIn("made")
                    .setSizeFit("")
                    .setCoverImg(body.getJSONArray("images").toJSONString())
                    .setDescImg(body.getJSONArray("images").toJSONString())
                    .setWeight("")
                    .setLength("")
                    .setWidth("")
                    .setHeigit("")
                    .setSalePrice(body.getString("price"))
                    .setCategory1(body.getJSONArray("categories").getJSONObject(0).getString("name"))
                    .setCategory2(body.getJSONArray("categories").getJSONObject(1).getString("name"))
                    .setCategory3(body.getJSONArray("categories").getJSONObject(2).getString("name"))
                    .setLast_check(new Date());
            JSONArray sizes = body.getJSONArray("sizes");
            for(int i =0,len=sizes.size();i<len;i++){
                JSONObject size = sizes.getJSONObject(i);
                ProductEDSManagement.SkuOptions sku = productEDSManagement.getSkuOptions();
                sku.setSize(size.getString("size"));
                sku.setStock(size.getString("quantity"));
                productOptions.getSkus().add(sku);
            }
            // @formatter:off
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productOptions;
    }
}
