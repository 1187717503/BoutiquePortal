package com.intramirror.web.controller.product;

import com.intramirror.common.parameter.StatusType;
import com.intramirror.product.api.model.ContentAdditionalCondition;
import com.intramirror.product.api.model.SearchCondition;
import com.intramirror.product.api.service.ISkuStoreService;
import com.intramirror.product.api.service.ProductPropertyService;
import com.intramirror.product.api.service.merchandise.ProductManagementService;
import com.intramirror.web.common.response.Response;
import com.intramirror.web.controller.cache.CategoryCache;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2017/10/20.
 *
 * @author YouFeng.Zhu
 */
@RestController
@RequestMapping("/product/fetch")
public class ProductMgntController {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProductMgntController.class);

    @Autowired
    private ProductManagementService productManagementService;

    @Autowired
    private CategoryCache categoryCache;

    @Autowired
    private ProductPropertyService productPropertyService;

    @Autowired
    private ISkuStoreService iSkuStoreService;

    @GetMapping(value = "/state/count")
    public Response listAllProductStateCount(
            // @formatter:off
            @RequestParam(value = "vendorId", required = false) String[] vendorId,
            @RequestParam(value = "boutiqueId", required = false) String boutiqueId,
            @RequestParam(value = "brandId", required = false) String brandId,
            @RequestParam(value = "categoryId", required = false) String categoryId,
            @RequestParam(value = "season", required = false) String season,
            @RequestParam(value = "designerId", required = false) String designerId,
            @RequestParam(value = "colorCode", required = false) String colorCode,
            @RequestParam(value = "image", required = false) String image,
            @RequestParam(value = "modelImage", required = false) String modelImage,
            @RequestParam(value = "streetImage", required = false) String streetImage,
            @RequestParam(value = "stock", required = false) String stock,
            @RequestParam(value = "exception",required = false) String exception
            // @formatter:on
    ) {

        SearchCondition searchCondition = new SearchCondition();
        searchCondition.setVendorId(vendorId);
        searchCondition.setBoutiqueId(escapeLikeParams(boutiqueId));
        searchCondition.setBrandId(brandId);
        searchCondition.setCategoryId(categoryId == null ? null : categoryCache.getAllChildCategory(Long.parseLong(categoryId)));
        searchCondition.setDesignerId(escapeLikeParams(designerId));
        searchCondition.setColorCode(colorCode);
        searchCondition.setImage(image);
        searchCondition.setModelImage(modelImage);
        searchCondition.setSeason(season);
        searchCondition.setStock(stock);
        searchCondition.setStreetImage(streetImage);
        searchCondition.setException(exception);
        List<Map<String, Object>> countList = productManagementService.listAllProductCountGounpByState(searchCondition);
        Map<StateEnum, Long> productStateCountMap = initiateCountMap();
        for (Map<String, Object> item : countList) {
            StateEnum stateEnum = StateMachineCoreRule.map2StateEnum(item);
            if (stateEnum == null) {
                LOGGER.error("Unknown product state : {}", item);
                continue;
            }
            productStateCountMap.put(stateEnum, Long.parseLong(item.get("count").toString()));
        }
        mergeOldState(productStateCountMap);
        countALLState(productStateCountMap);
        return Response.status(StatusType.SUCCESS).data(productStateCountMap);
    }

    private Map<StateEnum, Long> initiateCountMap() {
        Map<StateEnum, Long> productStateCountMap = new HashMap<>();
        for (StateEnum stateEnum : StateEnum.values()) {
            productStateCountMap.put(stateEnum, 0L);
        }
        return productStateCountMap;
    }

    private void mergeOldState(Map<StateEnum, Long> productStateCountMap) {
        Long countOldProcessing = productStateCountMap.get(StateEnum.OLD_PROCESSING);
        Long countProcessing = productStateCountMap.get(StateEnum.PROCESSING);
        Long countOldShopProcessing = productStateCountMap.get(StateEnum.OLD_SHOP_PROCESSING);
        Long countShopProcessing = productStateCountMap.get(StateEnum.SHOP_PROCESSING);
        countOldProcessing = countOldProcessing == null ? 0 : countOldProcessing;
        countProcessing = countProcessing == null ? 0 : countProcessing;
        countOldShopProcessing = countOldShopProcessing == null ? 0 : countOldShopProcessing;
        countShopProcessing = countShopProcessing == null ? 0 : countShopProcessing;
        productStateCountMap.put(StateEnum.PROCESSING, countProcessing + countOldProcessing);
        productStateCountMap.put(StateEnum.SHOP_PROCESSING, countOldShopProcessing + countShopProcessing);
        productStateCountMap.remove(StateEnum.OLD_PROCESSING);
        productStateCountMap.remove(StateEnum.OLD_SHOP_PROCESSING);
    }

    private void countALLState(Map<StateEnum, Long> productStateCountMap) {
        Long sum = 0L;
        for (Map.Entry<StateEnum, Long> entry : productStateCountMap.entrySet()) {
            sum += entry.getValue();
        }
        productStateCountMap.put(StateEnum.ALL, sum);
    }

    @GetMapping(value = "/list/{status}")
    // @formatter:off
    public Response listProductByFilter(@PathVariable(value = "status") String status,
            @RequestParam(value = "vendorId", required = false) String[] vendorId,
            @RequestParam(value = "boutiqueId", required = false) String boutiqueId,
            @RequestParam(value = "brandId", required = false) String brandId,
            @RequestParam(value = "categoryId", required = false) String categoryId,
            @RequestParam(value = "season", required = false) String season,
            @RequestParam(value = "designerId", required = false) String designerId,
            @RequestParam(value = "colorCode", required = false) String colorCode,
            @RequestParam(value = "image", required = false) String image,
            @RequestParam(value = "modelImage", required = false) String modelImage,
            @RequestParam(value = "streetImage", required = false) String streetImage,
            @RequestParam(value = "stock", required = false) String stock,
            @RequestParam(value = "pageSize",required = false) Integer pageSize,
            @RequestParam(value = "pageNo",required = false) Integer pageNo,
            @RequestParam(value = "orderBy",required = false) String orderBy,
            @RequestParam(value = "desc",required = false) String desc,
            @RequestParam(value = "exception",required = false) String exception,
            //Additional
            @RequestParam(value = "minBoutiqueDiscount",required = false) Float minBoutiqueDiscount,
            @RequestParam(value = "maxBoutiqueDiscount",required = false) Float maxBoutiqueDiscount,
            @RequestParam(value = "minIMDiscount",required = false) Float minIMDiscount,
            @RequestParam(value = "maxIMDiscount",required = false) Float maxIMDiscount,
            @RequestParam(value = "minStock",required = false) Long minStock,
            @RequestParam(value = "maxStock",required = false) Long maxStock,
            @RequestParam(value = "saleAtFrom",required = false) Long saleAtFrom,
            @RequestParam(value = "saleAtTo",required = false) Long saleAtTo,
            @RequestParam(value = "tag",required = false) String tag) {
    // @formatter:on
        SearchCondition searchCondition = initCondition(status, vendorId, boutiqueId, brandId, categoryId, season, designerId, colorCode, image, modelImage,
                streetImage, stock, pageSize, pageNo, orderBy, desc, exception, minBoutiqueDiscount, maxBoutiqueDiscount, minIMDiscount, maxIMDiscount,
                minStock, maxStock, saleAtFrom, saleAtTo, tag);
        LOGGER.info("{}", searchCondition);
        LOGGER.info("status: {}, shop status: {}", getStatusEnum(status).getProductStatus(), getStatusEnum(status).getShopProductStatus());
        List<Map<String, Object>> productList;
        productList = productManagementService.listProductService(searchCondition);
        if (productList.size() > 0) {
            appendInfo(productList, getStatusEnum(status));
        }
        return Response.status(StatusType.SUCCESS).data(productList);
    }

    private SearchCondition initCondition(
            // @formatter:off
             String status,
             String[] vendorId,
             String boutiqueId,
             String brandId,
             String categoryId,
             String season,
             String designerId,
             String colorCode,
             String image,
             String modelImage,
             String streetImage,
             String stock,
             Integer pageSize,
             Integer pageNo,
             String orderBy,
             String desc,
             String exception,
            //Additional
             Float minBoutiqueDiscount,
             Float maxBoutiqueDiscount,
             Float minIMDiscount,
             Float maxIMDiscount,
             Long minStock,
             Long maxStock,
             Long saleAtFrom,
             Long saleAtTo,
             String tag
    // @formatter:on
    ) {
        SearchCondition searchCondition = new SearchCondition();
        searchCondition.setVendorId(vendorId);
        searchCondition.setBoutiqueId(escapeLikeParams(boutiqueId));
        searchCondition.setBrandId(brandId);
        searchCondition.setCategoryId(categoryId == null ? null : categoryCache.getAllChildCategory(Long.parseLong(categoryId)));
        searchCondition.setDesignerId(escapeLikeParams(designerId));
        searchCondition.setColorCode(colorCode);
        searchCondition.setImage(image);
        searchCondition.setModelImage(modelImage);
        searchCondition.setSeason(season);
        searchCondition.setStatus(getStatusEnum(status).getProductStatus());
        searchCondition.setShopStatus(getStatusEnum(status).getShopProductStatus());
        searchCondition.setStock(stock);
        searchCondition.setStreetImage(streetImage);
        searchCondition.setDesc(desc);
        searchCondition.setStart((pageNo == null || pageNo < 0) ? 0 : (pageNo - 1) * pageSize);
        searchCondition.setCount((pageSize == null || pageSize < 0) ? 25 : pageSize);
        searchCondition.setOrderBy(orderBy);
        searchCondition.setException(exception);
        ContentAdditionalCondition contentAdditionalCondition = new ContentAdditionalCondition();
        searchCondition.setAddition(contentAdditionalCondition);

        if (minBoutiqueDiscount != null && maxBoutiqueDiscount != null) {
            contentAdditionalCondition.setMinBoutiqueDiscount(minBoutiqueDiscount);
            contentAdditionalCondition.setMaxBoutiqueDiscount(maxBoutiqueDiscount);
        }
        if (minIMDiscount != null && maxIMDiscount != null) {
            contentAdditionalCondition.setMinIMDiscount(minIMDiscount);
            contentAdditionalCondition.setMaxIMDiscount(maxIMDiscount);
        }
        if (minStock != null && maxStock != null) {
            contentAdditionalCondition.setMinStock(minStock);
            contentAdditionalCondition.setMaxStock(maxStock);
        }
        if (saleAtFrom != null && saleAtTo != null) {
            contentAdditionalCondition.setSaleAtFrom(saleAtFrom);
            contentAdditionalCondition.setSaleAtTo(saleAtTo);
        }
        if (tag != null) {
            contentAdditionalCondition.setTag(tag);
        }

        return searchCondition;
    }

    private void appendInfo(List<Map<String, Object>> productList, StateEnum stateEnum) {
        setCategoryPath(productList);
        List<Map<String, Object>> skuStoreList = iSkuStoreService.listSkuStoreByProductList(productList);
        List<Map<String, Object>> priceList = productManagementService.listPriceByProductList(productList);
        for (Map<String, Object> price : priceList) {
            calculateDiscount(price);
        }
        for (Map<String, Object> product : productList) {
            setSkuInfo(product, skuStoreList);
            setPrice(product, priceList);
            if (stateEnum == StateEnum.ALL) {
                setStatus(product);
            }
        }
    }

    private void calculateDiscount(Map<String, Object> price) {
        if (price.get("retail_price") == null) {
            return;
        }

        Double retailPrice = Double.parseDouble(price.get("retail_price").toString());
        if (retailPrice <= 0) {
            return;
        }
        if (price.get("boutique_price") != null) {
            Double boutique_price = Double.parseDouble(price.get("boutique_price").toString());
            BigDecimal b = new BigDecimal(1 - boutique_price / retailPrice * 1.22);
            Double discount = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            price.put("boutique_discount", discount);
        }
        if (price.get("im_price") != null) {
            Double boutique_price = Double.parseDouble(price.get("im_price").toString());
            BigDecimal b = new BigDecimal(1 - boutique_price / retailPrice);
            Double discount = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            price.put("im_discount", discount);
        }
        if (price.get("sale_price") != null) {
            Double boutique_price = Double.parseDouble(price.get("sale_price").toString());
            BigDecimal b = new BigDecimal(1 - boutique_price / retailPrice);
            Double discount = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            price.put("sale_discount", discount);
        }
    }

    private StateEnum getStatusEnum(String status) {
        return ProductStateOperationMap.getStatus(status);
    }

    private void setCategoryPath(List<Map<String, Object>> productList) {
        for (Map<String, Object> product : productList) {
            product.put("category", categoryCache.getAbsolutelyCategoryPath(Long.parseLong(product.get("category_id").toString())));
        }
    }

    private void setStatus(Map<String, Object> product) {
        product.put("status", StateMachineCoreRule.map2StateEnum(product));
    }

    private void setPrice(Map<String, Object> product, List<Map<String, Object>> priceList) {
        for (Map<String, Object> price : priceList) {
            if (product.get("product_id").equals(price.get("product_id"))) {
                product.putAll(price);
                break;
            }
        }
    }

    private void setSkuInfo(Map<String, Object> product, List<Map<String, Object>> skuStoreList) {
        List<Map<String, Object>> productSkuStoreList = new LinkedList<>();
        for (Map<String, Object> skuStore : skuStoreList) {
            if (product.get("product_id").equals(skuStore.get("product_id"))) {
                productSkuStoreList.add(skuStore);
            }
        }
        if (product.get("totalStore") == null) {
            product.put("totalStore", sumStock(productSkuStoreList));
        }
        product.put("skuDetail", productSkuStoreList);
    }

    private Long sumStock(List<Map<String, Object>> skuStoreList) {
        Long total = 0L;
        for (Map<String, Object> skuStore : skuStoreList) {
            Long store = Long.parseLong(skuStore.get("store").toString());
            total += (store < 0 ? 0 : store);
        }
        return total;
    }

    private String escapeLikeParams(String input) {
        if (input == null) {
            return input;
        }
        return input.replace("_", "\\_");
    }

    private void setBrandIdAndColorCode(List<Map<String, Object>> productList) {
        for (Map<String, Object> product : productList) {
            product.putAll(productPropertyService.getProductPropertyValueByProductId(Long.parseLong(product.get("product_id").toString())));
        }
    }

    private Map<String, Object> list2Map(List<Map<String, Object>> productList) {
        Map<String, Object> map = new HashMap<>();
        for (Map<String, Object> product : productList) {
            map.put(product.get("product_id").toString(), product);
        }
        return map;
    }

    private void setStock(Map<String, Object> product, List<Map<String, Object>> stocklist) {
        for (Map<String, Object> productStock : stocklist) {
            if (product.get("product_id").equals(productStock.get("product_id"))) {
                product.put("total_stock", productStock.get("store"));
                break;
            }
        }
    }

}
