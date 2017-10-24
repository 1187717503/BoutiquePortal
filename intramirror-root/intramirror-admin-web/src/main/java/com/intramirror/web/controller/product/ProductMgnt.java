package com.intramirror.web.controller.product;

import com.intramirror.product.api.model.SearchCondition;
import com.intramirror.product.api.service.ISkuStoreService;
import com.intramirror.product.api.service.ProductPropertyService;
import com.intramirror.product.api.service.product.IListProductService;
import com.intramirror.web.controller.cache.CategoryCache;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2017/10/20.
 * @author YouFeng.Zhu
 */
@RestController
@RequestMapping("/product")
public class ProductMgnt {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProductMgnt.class);

    @Autowired
    @Qualifier("listProductService")
    private IListProductService iListProductService;

    @Autowired
    private CategoryCache categoryCache;

    @Autowired
    private ProductPropertyService productPropertyService;

    @Autowired
    private ISkuStoreService iSkuStoreService;

    @GetMapping(value = "/list/{status}")
    // @formatter:off
    public List<Map<String, Object>> listProductByFilter(@PathVariable(value = "status") String status,
            @RequestParam(value = "boutique", required = false) String boutique,
            @RequestParam(value = "boutiqueid", required = false) String boutiqueid,
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "season", required = false) String season,
            @RequestParam(value = "designerid_colorcode", required = false) String designerid_colorcode,
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
        searchCondition.setDesignerid_colorcode(designerid_colorcode);
        searchCondition.setImage(image);
        searchCondition.setModelimage(modelimage);
        searchCondition.setSeason(season);
        searchCondition.setStatus(status);
        searchCondition.setStock(stock);
        searchCondition.setStreetimage(streetimage);
        searchCondition.setStart((pageNo == null || pageNo < 0) ? 0 : (pageNo - 1) * pageSize);
        searchCondition.setCount((pageSize == null || pageSize < 0) ? 25 : pageSize);
        LOGGER.info("{}", searchCondition);
        List<Map<String, Object>> productList = null;
        try {
            productList = iListProductService.listProductService(searchCondition);
            setCategoryPath(productList);
            setBrandIdAndColorCode(productList);
            setSkuInfo(productList);
        } catch (Exception e) {
            LOGGER.error("Unexcepted exception: \n", e);
        }
        return productList;
    }

    private void setCategoryPath(List<Map<String, Object>> productList) {
        for (Map<String, Object> product : productList) {
            product.put("category", categoryCache.getAbsolutelyCategoryPath(Long.parseLong(product.get("category_id").toString())));
        }
    }

    private void setSkuInfo(List<Map<String, Object>> productList) {
        for (Map<String, Object> product : productList) {
            product.put("skuDetail", iSkuStoreService.listSkuStoreByProductId(Long.parseLong(product.get("product_id").toString())));
        }
    }

    private void setBrandIdAndColorCode(List<Map<String, Object>> productList) {
        for (Map<String, Object> product : productList) {
            product.putAll(productPropertyService.getProductPropertyValueByProductId(Long.parseLong(product.get("product_id").toString())));
        }
    }

    @RequestMapping(value = "/operate/{action}", method = RequestMethod.PUT)
    public Object operateProduct(@PathVariable(value = "action") String action) {
        return null;
    }

}
