package com.intramirror.web.controller.product;

import com.intramirror.product.api.model.SearchCondition;
import com.intramirror.product.api.service.ISkuStoreService;
import com.intramirror.product.api.service.ProductPropertyService;
import com.intramirror.product.api.service.SkuService;
import com.intramirror.product.api.service.product.IListProductService;
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
    private IListProductService iListProductService;

    @Autowired
    private CategoryCache categoryCache;

    @Autowired
    private ProductPropertyService productPropertyService;

    @Autowired
    private ISkuStoreService iSkuStoreService;

    @Autowired
    private SkuService skuService;

    @GetMapping(value = "/list/{status}")
    // @formatter:off
    public Response listProductByFilter(@PathVariable(value = "status") String status,
            @RequestParam(value = "boutique", required = false) String boutique,
            @RequestParam(value = "boutiqueid", required = false) String boutiqueid,
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "SeasonServiceImpl", required = false) String season,
            @RequestParam(value = "designerid", required = false) String designerid,
            @RequestParam(value = "colorcode", required = false) String colorcode,
            @RequestParam(value = "image", required = false) String image,
            @RequestParam(value = "modelimage", required = false) String modelimage,
            @RequestParam(value = "streetimage", required = false) String streetimage,
            @RequestParam(value = "stock", required = false) String stock,
            @RequestParam(value = "pagesize",required = false) Integer pageSize,
            @RequestParam(value = "pageno",required = false) Integer pageNo) {
    // @formatter:on
        SearchCondition searchCondition = new SearchCondition();
        searchCondition.setBoutique(boutique);
        searchCondition.setBoutiqueid(boutiqueid);
        searchCondition.setBrand(brand);
        searchCondition.setCategory(category);
        searchCondition.setDesignerid(designerid);
        searchCondition.setColorcode(colorcode);
        searchCondition.setImage(image);
        searchCondition.setModelimage(modelimage);
        searchCondition.setSeason(season);
        searchCondition.setStatus(getStatusEnum(status).getProductStatus());
        searchCondition.setShopStatus(getStatusEnum(status).getShopProductStatus());
        searchCondition.setStock(stock);
        searchCondition.setStreetimage(streetimage);
        searchCondition.setStart((pageNo == null || pageNo < 0) ? 0 : (pageNo - 1) * pageSize);
        searchCondition.setCount((pageSize == null || pageSize < 0) ? 25 : pageSize);
        LOGGER.info("{}", searchCondition);
        LOGGER.info("status: {}, shop status: {}", getStatusEnum(status).getProductStatus(), getStatusEnum(status).getShopProductStatus());
        List<Map<String, Object>> productList = null;

        productList = iListProductService.listProductService(searchCondition);
        appendInfo(productList);
        return Response.status(StatusCode.SUCCESS).data(productList);
    }

    private void appendInfo(List<Map<String, Object>> productList) {
        setCategoryPath(productList);
        //        List<Map<String, Object>> stocklist = iSkuStoreService.listStockByProductList(productList);
        List<Map<String, Object>> skuStoreList = iSkuStoreService.listSkuStoreByProductList(productList);
        List<Map<String, Object>> priceList = skuService.listPriceByProductList(productList);
        for (Map<String, Object> product : productList) {
            //            setStock(product, stocklist);
            setSkuInfo(product, skuStoreList);
            setPrice(product, priceList);
        }
    }

    private void setStock(Map<String, Object> product, List<Map<String, Object>> stocklist) {
        for (Map<String, Object> productStock : stocklist) {
            if (product.get("product_id").equals(productStock.get("product_id"))) {
                product.put("total_stock", productStock.get("store"));
                break;
            }
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

    private Map<String, Object> list2Map(List<Map<String, Object>> productList) {
        Map<String, Object> map = new HashMap<>();
        for (Map<String, Object> product : productList) {
            map.put(product.get("product_id").toString(), product);
        }
        return map;
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
        //Map<String, Object> productMap = list2Map(productList);
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

}
