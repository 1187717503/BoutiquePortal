package com.intramirror.web.controller.product;

import com.intramirror.product.api.model.SearchCondition;
import com.intramirror.product.api.service.ISkuStoreService;
import com.intramirror.product.api.service.ProductPropertyService;
import com.intramirror.product.api.service.merchandise.ProductManagementService;
import com.intramirror.web.common.Response;
import com.intramirror.web.common.StatusCode;
import com.intramirror.web.controller.cache.CategoryCache;
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
            @RequestParam(value = "vendorId", required = false) String vendorId,
            @RequestParam(value = "boutiqueId", required = false) String boutiqueId,
            @RequestParam(value = "brandId", required = false) String brandId,
            @RequestParam(value = "categoryId", required = false) String categoryId,
            @RequestParam(value = "season", required = false) String season,
            @RequestParam(value = "designerId", required = false) String designerId,
            @RequestParam(value = "colorCode", required = false) String colorCode,
            @RequestParam(value = "image", required = false) String image,
            //TODO: modelImage filter doesn't work now
            @RequestParam(value = "modelImage", required = false) String modelImage,
            @RequestParam(value = "streetImage", required = false) String streetImage,
            //TODO: stock filter doesn't work now
            @RequestParam(value = "stock", required = false) String stock
            // @formatter:on
    ) {

        SearchCondition searchCondition = new SearchCondition();
        searchCondition.setVendorId(vendorId);
        searchCondition.setBoutiqueId(boutiqueId);
        searchCondition.setBrandId(brandId);
        searchCondition.setCategoryId(categoryCache.getAllChildCategory(Long.parseLong(categoryId)));
        searchCondition.setDesignerId(designerId);
        searchCondition.setColorCode(colorCode);
        searchCondition.setImage(image);
        searchCondition.setModelImage(modelImage);
        searchCondition.setSeason(season);
        searchCondition.setStock(stock);
        searchCondition.setStreetImage(streetImage);
        List<Map<String, Object>> countList = productManagementService.listAllProductCountGounpByState();
        Map<StateEnum, Long> productStateCountList = new HashMap<>();
        for (Map<String, Object> item : countList) {
            StateEnum stateEnum = StateMachineCoreRule.map2StateEnum(item);
            if (stateEnum == null) {
                LOGGER.error("Unknown product state : {}", item);
                continue;
            }
            productStateCountList.put(stateEnum, Long.parseLong(item.get("count").toString()));
        }
        return Response.status(StatusCode.SUCCESS).data(productStateCountList);
    }

    @GetMapping(value = "/list/{status}")
    // @formatter:off
    public Response listProductByFilter(@PathVariable(value = "status") String status,
            @RequestParam(value = "vendorId", required = false) String vendorId,
            @RequestParam(value = "boutiqueId", required = false) String boutiqueId,
            @RequestParam(value = "brandId", required = false) String brandId,
            @RequestParam(value = "categoryId", required = false) String categoryId,
            @RequestParam(value = "season", required = false) String season,
            @RequestParam(value = "designerId", required = false) String designerId,
            @RequestParam(value = "colorCode", required = false) String colorCode,
            @RequestParam(value = "image", required = false) String image,
            //TODO: modelImage filter doesn't work now
            @RequestParam(value = "modelImage", required = false) String modelImage,
            @RequestParam(value = "streetImage", required = false) String streetImage,
            //TODO: stock filter doesn't work now
            @RequestParam(value = "stock", required = false) String stock,
            @RequestParam(value = "pageSize",required = false) Integer pageSize,
            @RequestParam(value = "pageNo",required = false) Integer pageNo,
            @RequestParam(value = "orderBy",required = false) String orderBy,
            @RequestParam(value = "desc",required = false) String desc) {
    // @formatter:on
        SearchCondition searchCondition = new SearchCondition();
        searchCondition.setVendorId(vendorId);
        searchCondition.setBoutiqueId(boutiqueId);
        searchCondition.setBrandId(brandId);
        searchCondition.setCategoryId(categoryCache.getAllChildCategory(Long.parseLong(categoryId)));
        searchCondition.setDesignerId(designerId);
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
        LOGGER.info("{}", searchCondition);
        LOGGER.info("status: {}, shop status: {}", getStatusEnum(status).getProductStatus(), getStatusEnum(status).getShopProductStatus());
        List<Map<String, Object>> productList;
        productList = productManagementService.listProductService(searchCondition);
        if (productList.size() > 0) {
            appendInfo(productList);
        }
        return Response.status(StatusCode.SUCCESS).data(productList);
    }

    private void appendInfo(List<Map<String, Object>> productList) {
        setCategoryPath(productList);
        List<Map<String, Object>> skuStoreList = iSkuStoreService.listSkuStoreByProductList(productList);
        List<Map<String, Object>> priceList = productManagementService.listPriceByProductList(productList);
        for (Map<String, Object> product : productList) {
            setSkuInfo(product, skuStoreList);
            setPrice(product, priceList);
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
        product.put("totalStore", sumStock(productSkuStoreList));
        product.put("skuDetail", productSkuStoreList);
    }

    private Long sumStock(List<Map<String, Object>> skuStoreList) {
        Long total = 0L;
        for (Map<String, Object> skuStore : skuStoreList) {
            total += Long.parseLong(skuStore.get("store").toString());
        }
        return total;
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
