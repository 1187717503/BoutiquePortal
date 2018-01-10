package com.intramirror.web.mapping.impl.shopify;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intramirror.common.help.StringUtils;
import com.intramirror.web.mapping.api.IProductMapping;
import com.intramirror.web.mapping.impl.eds.EdsUpdateByProductMapping;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import pk.shoplus.model.ProductEDSManagement;

/**
 * Created by dingyifan on 2017/12/26.
 */
@Service
public class ShopifyProductMapping implements IProductMapping {

    private final static Logger logger = Logger.getLogger(EdsUpdateByProductMapping.class);

    public static ProductEDSManagement productEDSManagement = new ProductEDSManagement();

    public static List<String> secondLevels = new ArrayList<>();
    public static List<String> threeLevels = new ArrayList<>();
    public static List<String> seasons = new ArrayList<>();

    static {
        secondLevels.add("LADIES CLOTHING");
        secondLevels.add("LADIES SHOES");
        secondLevels.add("LADIES BAGS");
        secondLevels.add("LADIES ACCESSORIES");
        secondLevels.add("MEN CLOTHING");
        secondLevels.add("MEN SHOES");
        secondLevels.add("MEN BAGS");
        secondLevels.add("MEN ACCESSORIES");

        threeLevels.add("Beachwear");
        threeLevels.add("Coats");
        threeLevels.add("Denim");
        threeLevels.add("Dresses");
        threeLevels.add("Jackets");
        threeLevels.add("Knitwear");
        threeLevels.add("Skirts");
        threeLevels.add("Sportswear");
        threeLevels.add("Suits");
        threeLevels.add("T-Shirts");
        threeLevels.add("Tops");
        threeLevels.add("Trousers");
        threeLevels.add("Underwear & Socks");
        threeLevels.add("Shirts");
        threeLevels.add("Sweatshirts & Hoodies");
        threeLevels.add("Ballerina Shoes");
        threeLevels.add("Boots");
        threeLevels.add("Laced Shoes");
        threeLevels.add("Loafers");
        threeLevels.add("Others Shoes");
        threeLevels.add("Pumps");
        threeLevels.add("Sandals");
        threeLevels.add("Sneakers");
        threeLevels.add("Backpacks");
        threeLevels.add("Clutch");
        threeLevels.add("Luggage");
        threeLevels.add("Satchel & Cross Body Bags");
        threeLevels.add("Shoulder Bags");
        threeLevels.add("Tote Bags");
        threeLevels.add("Belts");
        threeLevels.add("Bijoux");
        threeLevels.add("Gloves");
        threeLevels.add("Hats");
        threeLevels.add("Keyrings & Chains");
        threeLevels.add("Others Accessories");
        threeLevels.add("Scarves");
        threeLevels.add("Sunglasses");
        threeLevels.add("Ties");
        threeLevels.add("Wallets");
        threeLevels.add("Coats");
        threeLevels.add("Denim");
        threeLevels.add("Jackets");
        threeLevels.add("Shirts");
        threeLevels.add("Suits");
        threeLevels.add("Sweatshirts & Hoodies");
        threeLevels.add("Knitwear");
        threeLevels.add("Trousers");
        threeLevels.add("T-Shirts");
        threeLevels.add("Underwear & Socks");
        threeLevels.add("Boots");
        threeLevels.add("Laced Shoes");
        threeLevels.add("Loafers");
        threeLevels.add("Other Shoes");
        threeLevels.add("Sandals");
        threeLevels.add("Sneakers");
        threeLevels.add("Backpacks");
        threeLevels.add("Clutch");
        threeLevels.add("Luggage");
        threeLevels.add("Satchel & Cross Body Bags");
        threeLevels.add("Shoulder Bags");
        threeLevels.add("Tote Bags");
        threeLevels.add("Belts");
        threeLevels.add("Bijoux");
        threeLevels.add("Gloves");
        threeLevels.add("Hats");
        threeLevels.add("Keyrings & Chains");
        threeLevels.add("Others Accessories");
        threeLevels.add("Scarves");
        threeLevels.add("Sunglasses");
        threeLevels.add("Ties");
        threeLevels.add("Wallets");

        seasons.add("E40");
        seasons.add("I50");
        seasons.add("E60");
    }

    @Override
    public ProductEDSManagement.ProductOptions mapping(Map<String, Object> bodyDataMap) {
        ProductEDSManagement.ProductOptions productOptions = productEDSManagement.getProductOptions();
        try {

            JSONObject product = (JSONObject) bodyDataMap.get("product");
            productOptions.setName("")
                    .setCode(product.getString("id"))
                    .setBrandName(product.getString("vendor"))
                    .setDesc(product.getString("title"))
                    .setMadeIn("")
                    .setComposition("")
                    .setCategory_name("")
                    .setSizeFit("")
                    .setLength("")
                    .setHeigit("")
                    .setFullUpdateProductFlag("")
                    .setLast_check(new Date());

            // mapping sku
            JSONArray skus = product.getJSONArray("variants");
            for(int i = 0,len=skus.size();i<len;i++){
                JSONObject sku = skus.getJSONObject(i);
                String size = sku.getString("option1");
                String sku_code = sku.getString("barcode");
                String stock = sku.getString("inventory_quantity");
                String boutique_sku_id = sku.getString("id");

                String weight = sku.getString("grams");
                String color_description = sku.getString("option2");
                String BrandID = sku.getString("sku");
                String price = sku.getString("price");
                String compare_at_price = sku.getString("compare_at_price");

                if(StringUtils.isNotBlank(weight)) {
                    productOptions.setWeight(weight);
                }
                if(StringUtils.isNotBlank(color_description)) {
                    productOptions.setColorDesc(color_description);
                    productOptions.setColorCode(color_description);
                }
                if(StringUtils.isNotBlank(BrandID)) {
                    productOptions.setBrandCode(BrandID);
                }
                /*if(StringUtils.isNotBlank(price)) {
                    productOptions.setSalePrice(price);
                }*/

                ProductEDSManagement.SkuOptions skuOptions = productEDSManagement.getSkuOptions();
                skuOptions.setSize(size);
                skuOptions.setBoutique_sku_id(boutique_sku_id);
                skuOptions.setBarcodes(sku_code);
                skuOptions.setStock(stock);
                productOptions.getSkus().add(skuOptions);

                /* update by 2018/01/09*/

                String salePrice = productOptions.getSalePrice();
                String changePrice = "";
                if(StringUtils.isBlank(compare_at_price)) {
                    productOptions.setSalePrice(price);
                    changePrice = productOptions.getSalePrice();
                } else {
                    BigDecimal cPrice = new BigDecimal(compare_at_price);
                    BigDecimal pPrice = new BigDecimal(price);

                    int flag = cPrice.compareTo(pPrice);
                    if(flag == 1 || flag == 0) {
                        productOptions.setSalePrice(compare_at_price);
                        changePrice = productOptions.getSalePrice();
                        logger.info("ShopifyProductMapping,mapping,compare_at_price大于或等于price,bodyDataMap:"+JSONObject.toJSONString(bodyDataMap));
                    } else {
                        productOptions.setSalePrice(price);
                        changePrice = productOptions.getSalePrice();
                        logger.info("ShopifyProductMapping,mapping,compare_at_price小于price,bodyDataMap:"+JSONObject.toJSONString(bodyDataMap));
                    }
                }

                if(StringUtils.isNotBlank(salePrice) && StringUtils.isNotBlank(changePrice)) {
                    if(!salePrice.equals(changePrice)) {
                        logger.info("ShopifyProductMapping,mapping,sku价格不一致,bodyDataMap:"+JSONObject.toJSONString(bodyDataMap)+",salePrice:"+salePrice+",changePrice:"+changePrice);
                    }
                }

                /* update by 2018/01/09*/
            }

            // mapping category
            String tags = product.getString("tags");
            if(StringUtils.isNotBlank(tags)) {
                String[] tagArr = tags.split(",");
                String category1 = "";
                String category2 = "";
                String category3 = "";
                String seasonCode = "";

                for(String tag : tagArr) {
                    tag = StringUtils.trim(tag);

                    if(StringUtils.isBlank(category2)) {
                        for(String c2 : secondLevels) {
                            if(c2.equalsIgnoreCase(tag)) {
                                category2 = tag;

                                if(c2.contains("LADIES")) {
                                    category1 = "LADIES";
                                }

                                if(c2.contains("MEN")) {
                                    category1 = "MEN";
                                }
                                break;
                            }
                        }
                    }

                    if(StringUtils.isBlank(category3)) {
                        for(String c3 : threeLevels) {
                            if(c3.equalsIgnoreCase(tag)) {
                                category3 = tag;
                                break;
                            }
                        }
                    }

                    if(StringUtils.isBlank(seasonCode)) {
                        for(String season_code : seasons) {
                            if(season_code.equalsIgnoreCase(tag)) {
                                seasonCode = tag;
                            }
                        }
                    }
                }

                productOptions.setCategory1(category1)
                        .setCategory2(category2)
                        .setCategory3(category3)
                        .setSeasonCode(seasonCode);

            }

            // mapping image
            List<String> coverImg = new ArrayList<>();
            JSONArray images = product.getJSONArray("images");
            for(int i = 0,len=images.size();i<len;i++) {
                JSONObject image = images.getJSONObject(i);
                coverImg.add(image.getString("src"));
            }
            String img = JSONObject.toJSONString(coverImg);
            productOptions.setDescImg(img)
            .setCoverImg(img);
            productOptions.setModifyPrice("1");

            /*productOptions.setSeasonCode("17FW");
            productOptions.setCategory1("MEN");
            productOptions.setCategory2("Clothing");
            productOptions.setCategory3("Beachwear");
            productOptions.setBrandName("Gucci");*/
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("ShopifyProductMapping,mapping,errorMessage:"+e);
        }
        return productOptions;
    }

    /*public static void main(String[] args) {
        BigDecimal b1 = new BigDecimal(2);
        BigDecimal b2 = new BigDecimal(2);

        System.out.println(b1.compareTo(b2));
    }*/
}
