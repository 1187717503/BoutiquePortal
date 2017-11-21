package com.intramirror.web.controller.content;

import com.intramirror.common.parameter.StatusType;
import com.intramirror.product.api.model.Tag;
import com.intramirror.product.api.service.BlockService;
import com.intramirror.product.api.service.ISkuStoreService;
import com.intramirror.product.api.service.ITagService;
import com.intramirror.product.api.service.content.ContentManagementService;
import com.intramirror.product.api.service.merchandise.ProductManagementService;
import com.intramirror.web.Exception.ErrorResponse;
import com.intramirror.web.Exception.ValidateException;
import com.intramirror.web.common.response.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2017/11/17.
 *
 * @author YouFeng.Zhu
 */
@RestController
@RequestMapping("/content")
public class ContentMgntController {
    private final static Logger LOGGER = LoggerFactory.getLogger(ContentMgntController.class);

    @Autowired
    private BlockService blockService;

    @Autowired
    private ITagService iTagService;

    @Autowired
    private ContentManagementService contentManagementService;

    @Autowired
    private ProductManagementService productManagementService;

    @Autowired
    private ISkuStoreService skuStoreService;

    @GetMapping(value = "/block/detail", produces = "application/json")
    public Response getContentDetail(@RequestParam(value = "blockId") Long blockId) {
        return Response.status(StatusType.SUCCESS).data(blockService.getBlockById(blockId));
    }

    @GetMapping(value = "/block/list", produces = "application/json")
    public Response getContentDetail() {
        return Response.status(StatusType.SUCCESS).data(blockService.listSimpleBlock());
    }

    @GetMapping(value = "/tag/products", produces = "application/json")
    public Response listProductByTag(@RequestParam(value = "tagId") Long tagId) {
        List<Map<String, Object>> productList = contentManagementService.listTagProductInfo(tagId);
        if (productList.size() > 0) {
            appendInfo(productList);
        }
        return Response.status(StatusType.SUCCESS).data(productList);
    }



    @PostMapping(value = "/savetagproductrel", consumes = "application/json")
    public Response saveTagProductRel(@RequestBody Map<String, Object> body) {
        Long tagId = body.get("tagId") == null ? null : Long.parseLong(body.get("tagId").toString());
        Long sortNum = body.get("sortNum") == null ? 999 : Long.parseLong(body.get("sortNum").toString());
        List<String> productIdList = (List<String>) body.get("productIdList");

        if (productIdList.size() <= 0 || null == tagId) {
            throw new ValidateException(new ErrorResponse("Parameter could not be null!"));
        }

        Map<String, Object> map = new HashMap<>();
        map.put("productIdList", productIdList);
        map.put("tagId", tagId);
        map.put("sort_num", sortNum);
        iTagService.saveTagProductRel(map);
        return Response.success();
    }

    @PostMapping(value = "/tag/list", consumes = "application/json")
    public Response listTags() {
        List<Tag> listTags = iTagService.getTags();
        return Response.status(StatusType.SUCCESS).data(listTags);
    }

    private void appendInfo(List<Map<String, Object>> productList) {
        List<Long> idList = extractProductIdList(productList);
        List<Map<String, Object>> storeList = skuStoreService.listTotalStockByProductIds(idList);
        Map<Long, Long> productStock = mapProductStock(storeList);
        List<Map<String, Object>> priceList = productManagementService.listPriceByProductList(productList);
        for (Map<String, Object> product : productList) {
            product.put("totalStock", productStock.get(Long.parseLong(product.get("product_id").toString())));
            setPriceDiscount(product, priceList);
        }

    }

    private void setPriceDiscount(Map<String, Object> product, List<Map<String, Object>> priceList) {
        for (Map<String, Object> price : priceList) {
            if (product.get("product_id").equals(price.get("product_id"))) {
                product.put("im_price", price.get("im_price"));
                Double discount = 0D;
                Double retailPrice = Double.parseDouble(price.get("retail_price").toString());
                if (retailPrice > 0 && price.get("im_price") != null) {
                    Double boutique_price = Double.parseDouble(price.get("im_price").toString());
                    BigDecimal b = new BigDecimal(1 - boutique_price / retailPrice);
                    discount = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                }
                product.put("im_discount", discount);
                break;
            }
        }
    }

    private List<Long> extractProductIdList(List<Map<String, Object>> productList) {
        List<Long> idList = new ArrayList<>();
        for (Map<String, Object> product : productList) {
            idList.add(Long.parseLong(product.get("product_id").toString()));
        }
        return idList;
    }

    private Map<Long, Long> mapProductStock(List<Map<String, Object>> storeList) {
        Map<Long, Long> productStockMap = new HashMap<>();
        for (Map<String, Object> store : storeList) {
            productStockMap.put(Long.parseLong(store.get("product_id").toString()), Long.parseLong(store.get("total_stock").toString()));
        }
        return productStockMap;
    }

}
