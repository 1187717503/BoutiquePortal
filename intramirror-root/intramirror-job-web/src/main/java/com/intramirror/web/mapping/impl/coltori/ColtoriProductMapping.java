package com.intramirror.web.mapping.impl.coltori;

import com.alibaba.fastjson15.JSONArray;
import com.google.gson.Gson;
import com.intramirror.common.help.ExceptionUtils;
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

/**
 * Created by dingyifan on 2017/11/1.
 */
@Service(value = "coltoriProductMapping")
public class ColtoriProductMapping implements IProductMapping {

    private static final Logger logger = Logger.getLogger(ColtoriProductMapping.class);

    @Autowired
    private ICategoryService categoryService;

    @Override
    public ProductEDSManagement.ProductOptions mapping(Map<String, Object> bodyDataMap) {
        logger.info("ColtoriProductMapping,inputParams,bodyDataMap:"+ JSONObject.fromObject(bodyDataMap));
        ProductEDSManagement.ProductOptions productOptions = new ProductEDSManagement.ProductOptions();
        try {
            String key = bodyDataMap.get("key").toString();
            JSONObject productObj = (JSONObject) bodyDataMap.get("product");
//            traveseJson(productObj);
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
//                    .setCoverImg(productObj.getString("images"))
//                    .setDescImg(productObj.getString("images"))
                    .setWeight("")
                    .setLength("")
                    .setWidth("")
                    .setHeigit("")
                    .setSalePrice(productObj.getString("retail_price"))
                    .setLast_check(new Date());

            String category_l1 = productObj.getString("family_id");
            String category_l2 = productObj.getString("group_id");
            String category_l3 = productObj.getString("subgroup_id");
            String category_id = productObj.getString("category_id");

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
            }

            /*Map<String, Object> categoryMap = new HashMap<String, Object>();
            categoryMap.put("vendor_id", bodyDataMap.get("vendor_id"));
            categoryMap.put("boutique_first_category", category_l1);
            categoryMap.put("boutique_second_category", category_l2);
            categoryMap.put("boutique_third_category", category_l3);
            productOptions.setCategory_name(com.alibaba.fastjson15.JSONObject.toJSONString(categoryMap));*/
            productOptions.setCategory1(category_l1);
            productOptions.setCategory2(category_l2);
            productOptions.setCategory3(category_l3);
            /*logger.info("ColtoriProductMapping,getCategoryByCondition,categoryMap:"+ com.alibaba.fastjson15.JSONObject.toJSONString(categoryMap));
            List<Map<String, Object>> apiCategoryMap = categoryService.getCategoryByCondition(categoryMap);
            logger.info("ColtoriProductMapping,getCategoryByCondition,apiCategoryMap:"+ com.alibaba.fastjson15.JSONObject.toJSONString(apiCategoryMap)+",categoryMap:"+ com.alibaba.fastjson15.JSONObject
                    .toJSONString(categoryMap));
            if(null != apiCategoryMap && 0 < apiCategoryMap.size()) {
                productOptions.setCategoryId(apiCategoryMap.get(0).get("category_id").toString());
            }*/

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

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("ColtoriProductMapping,errorMessage:"+ ExceptionUtils.getExceptionDetail(e)+",bodyDataMap:"+ JSONObject.fromObject(bodyDataMap));
        }
        logger.info("ColtoriProductMapping,outputParams,bodyDataMap:"+ JSONObject.fromObject(bodyDataMap));
        return productOptions;
    }

    public static void main(String[] args) {
        String str = "[\"http://sha-oss-static.oss-cn-shanghai.aliyuncs.com/upload/c99a5cd5-3415-4e98-b8ae-9c1933c2210a-100.jpg\",\"http://sha-oss-static.oss-cn-shanghai.aliyuncs.com/upload/c99a5cd5-3415-4e98-b8ae-9c1933c2210a-1.jpg\",\"http://cdn.orderlink.it/foto/181728BNBFLAS0SPU/BNBFLAS0SPUvxf-17.jpg\",\"http://cdn.orderlink.it/foto/181728BNBFLAS0SPU/BNBFLAS0SPUpth-15.jpg\",\"http://cdn.orderlink.it/foto/181728BNBFLAS0SPU/BNBFLAS0SPUsfn-19.jpg\"]";

        List<String> originList = JSONArray.parseArray(str, String.class);

        String o1 = "http://sha-oss-static.oss-cn-shanghai.aliyuncs.com/upload/c99a5cd5-3415-4e98-b8ae-9c1933c2210a-100.jpg";
        System.out.println(o1.substring(o1.lastIndexOf("-")+1,o1.lastIndexOf(".jpg")));

        Collections.sort(originList, new Comparator<String>() {
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

        for (int i = 0;i<originList.size();i++){
            System.out.println(originList.get(i));
        }
    }
}
