package com.intramirror.web.mapping.impl.coltori;

import com.alibaba.fastjson15.JSONArray;
import com.google.gson.Gson;
import com.intramirror.common.help.StringUtils;
import com.intramirror.product.api.service.category.ICategoryService;
import com.intramirror.web.mapping.api.IProductMapping;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pk.shoplus.model.ProductEDSManagement;

@Service(value = "coltoriProductMapping")
public class ColtoriProductMapping implements IProductMapping {

    private static final Logger logger = Logger.getLogger(ColtoriProductMapping.class);

    @Autowired
    private ICategoryService categoryService;

    @Override
    public ProductEDSManagement.ProductOptions mapping(Map<String, Object> bodyDataMap) {
        ProductEDSManagement.ProductOptions productOptions = new ProductEDSManagement.ProductOptions();
        try {
            String key = bodyDataMap.get("key").toString();
            JSONObject productObj = (JSONObject) bodyDataMap.get("product");
            productOptions.setName(productObj.getJSONObject("name").get("en") == null?"":productObj.getJSONObject("name").getString("en"))
                    .setCode(key)
                    .setSeasonCode(productObj.getString("season_id"))
                    .setBrandCode(productObj.getJSONArray("other_ids").getString(1))
                    .setCarryOver("")
                    .setBrandName(productObj.getString("brand_id").trim())
                    .setColorCode(productObj.get("variation_id") == null?"":productObj.getString("variation_id"))
                    .setColorDesc("")
                    .setDesc(productObj.getJSONObject("description").get("en") == null?"":productObj.getJSONObject("description").getString("en"))
                    .setComposition(productObj.get("notes") == null?"":productObj.getString("notes"))
                    .setMadeIn("")
                    .setSizeFit("")
                    .setWeight("")
                    .setLength("")
                    .setWidth("")
                    .setHeigit("")
                    .setSalePrice(productObj.getString("retail_price"))
                    .setLast_check(new Date());

            // category
            String category_l1 = productObj.getString("family_id");
            String category_l2 = productObj.getString("group_id");
            String category_l3 = productObj.getString("subgroup_id");
            String category_id = productObj.getString("category_id");
            productOptions.setCategory1(category_l1);
            productOptions.setCategory2(category_l2);
            productOptions.setCategory3(category_l3);

            if(StringUtils.isBlank(category_l1) || "null".equals(category_l1)) {
                productObj.put("family_id","");
            }
            if(StringUtils.isBlank(category_l2) || "null".equals(category_l2)) {
                productObj.put("group_id","");
            }
            if(StringUtils.isBlank(category_l3) || "null".equals(category_l3)) {
                productObj.put("subgroup_id","");
            }

            if(StringUtils.isBlank(category_id) || "null".equals(category_id)) {
                productObj.put("category_id","");
            } else {
                productOptions.setCategory3(category_id);
            }

            // images
            String images = productObj.getString("images");
            if(StringUtils.isNotBlank(images)) {
                List<String> imageList = JSONArray.parseArray(images, String.class);
                Collections.sort(imageList, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        try {
                            int i1 = Integer.parseInt(o1.substring(o1.lastIndexOf("-")+1,o1.lastIndexOf(".jpg")));
                            int i2 = Integer.parseInt(o2.substring(o2.lastIndexOf("-")+1,o2.lastIndexOf(".jpg")));

                            if(i1>i2){
                                return 1;
                            }
                        } catch (Exception e) {

                        }
                        return -1;
                    }
                });

                productOptions.setCoverImg(new Gson().toJson(imageList));
                productOptions.setDescImg(new Gson().toJson(imageList));
            }

            // season_code
            String product_code = productOptions.getCode();
            String season_code = productOptions.getSeasonCode();
            if(StringUtils.isNotBlank(product_code)
                    && StringUtils.isNotBlank(season_code)) {
                if(!product_code.substring(0,3).equals(season_code.substring(0,3))) {
                    productOptions.setSeasonCode("CarryOver");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("coltori product mapping error message : " + e);
        }
        return productOptions;
    }

}
