package com.intramirror.web.controller.product;

import com.intramirror.common.parameter.StatusType;
import com.intramirror.core.common.response.Response;
import com.intramirror.product.api.model.SearchCondition;
import com.intramirror.product.api.service.ISkuStoreService;
import com.intramirror.product.api.service.ITagService;
import com.intramirror.product.api.service.ProductPropertyService;
import com.intramirror.product.api.service.content.ContentManagementService;
import com.intramirror.product.api.service.merchandise.ProductManagementService;
import com.intramirror.product.api.service.promotion.IPromotionService;
import com.intramirror.utils.transform.JsonTransformUtil;
import com.intramirror.web.common.Constants;
import com.intramirror.web.controller.cache.CategoryCache;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
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
    private ContentManagementService contentManagementService;

    @Autowired
    private CategoryCache categoryCache;

    @Autowired
    private ProductPropertyService productPropertyService;

    @Autowired
    private ISkuStoreService iSkuStoreService;

    @Autowired
    private IPromotionService promotionService;

    @GetMapping(value = "/state/count")
    public Response listAllProductStateCount(
            // @formatter:off
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "tagType", required = false) Integer tagType,
            @RequestParam(value = "isExcludeCampaign",required = false) Integer isExcludeCampaign,
            @RequestParam(value = "limitTimeTagId", required = false) Long limitTimeTagId,
            SearchCondition searchParams
            // @formatter:on
    ) {

        SearchCondition searchCondition = initCondition(searchParams, null, categoryId, tagType, null, null, isExcludeCampaign, limitTimeTagId);
        LOGGER.info("Search condition : {}", JsonTransformUtil.toJson(searchCondition));
        Map<StateEnum, Long> productStateCountMap = initiateCountMap();
        if (isEmptyTag(searchCondition)) {
            return Response.status(StatusType.SUCCESS).data(productStateCountMap);
        }
        List<Map<String, Object>> countList = productManagementService.listAllProductCountGounpByState(searchCondition);
        for (Map<String, Object> item : countList) {
            StateEnum stateEnum = StateMachineCoreRule.map2StateEnum(item);
            if (stateEnum == null) {
                LOGGER.error("Unknown product state : {}", item);
                continue;
            }
            productStateCountMap.put(stateEnum, Long.parseLong(item.get("count").toString()));
        }
        mergeOldState(productStateCountMap, searchCondition);
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

    private void mergeOldState(Map<StateEnum, Long> productStateCountMap, SearchCondition searchCondition) {
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

        /*Integer boutiqueExceptionType = searchCondition.getBoutiqueExceptionType();
        if (boutiqueExceptionType == null || boutiqueExceptionType == Constants.boutique_exception_type_all) {
            return;
        }

        Integer countException = productManagementService.countBoutiqueException(boutiqueExceptionType);
        productStateCountMap.put(StateEnum.SHOP_PROCESSING, countException.longValue());*/
    }

    private void countALLState(Map<StateEnum, Long> productStateCountMap) {
        Long sum = 0L;
        for (Map.Entry<StateEnum, Long> entry : productStateCountMap.entrySet()) {
            sum += entry.getValue();
        }
        productStateCountMap.put(StateEnum.ALL, sum);
    }

    @GetMapping(value = "/list/{state}")
    public Response listProductByFilter(
            // @formatter:off
            SearchCondition searchParams,
            @PathVariable(value = "state") String state,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "pageSize",required = false) Integer pageSize,
            @RequestParam(value = "pageNo",required = false) Integer pageNo,
            @RequestParam(value = "tagType",required = false) Integer tagType,
            @RequestParam(value = "isExcludeCampaign",required = false) Integer isExcludeCampaign,
            @RequestParam(value = "limitTimeTagId", required = false) Long limitTimeTagId
            // @formatter:on
    ) {
        SearchCondition searchCondition = initCondition(searchParams, state, categoryId, tagType, pageSize, pageNo, isExcludeCampaign, limitTimeTagId);
        LOGGER.info("Search condition : {}", JsonTransformUtil.toJson(searchCondition));

        if (isEmptyTag(searchCondition)) {
            return Response.success();
        }

        List<Map<String, Object>> productList;
        productList = productManagementService.listProductService(searchCondition);
        if (productList.size() > 0) {
            appendInfo(productList, getStatusEnum(state), searchCondition);
        }
        List<Map<String, Object>> data = handleBoutqiqueException(productList, searchCondition);
        return Response.status(StatusType.SUCCESS).data(data);
    }

    private List<Map<String, Object>> handleBoutqiqueException(List<Map<String, Object>> productList, SearchCondition searchCondition) {
        int shopProductStatus = searchCondition.getShopProductStatus().intValue();
        int productStatus = searchCondition.getProductStatus().intValue();
        Integer boutiqueExceptionType = searchCondition.getBoutiqueExceptionType();
        if ((productStatus == 4 || productStatus == 2) && shopProductStatus == 2 && CollectionUtils.isNotEmpty(productList)) {

            List<Map<String, Object>> boutiqueExceptionList = productManagementService.listProductException(productList);
            for (Map<String, Object> productMap : productList) {
                int productId = Integer.parseInt(productMap.get("product_id").toString());

                for (Map<String, Object> boutiqueExceptionMap : boutiqueExceptionList) {
                    int exceptionProductId = Integer.parseInt(boutiqueExceptionMap.get("product_id").toString());
                    int type = Integer.parseInt(boutiqueExceptionMap.get("type").toString());
                    if (productId == exceptionProductId && type == Constants.boutique_exception_type_price) {
                        if (productMap.get("priceChange") == null) {
                            productMap.put("priceChange", boutiqueExceptionMap);
                        }
                    } else if (productId == exceptionProductId && type == Constants.boutique_exception_type_season) {
                        if (productMap.get("seasonChange") == null) {
                            productMap.put("seasonChange", boutiqueExceptionMap);
                        }
                    }
                }
            }

            if (boutiqueExceptionType != null && boutiqueExceptionType != Constants.boutique_exception_type_all) {
                List<Map<String, Object>> exceptionProductList = new ArrayList<>();
                for (Map<String, Object> productMap : productList) {
                    if (boutiqueExceptionType.intValue() == 1 && productMap.get("priceChange") != null) {
                        exceptionProductList.add(productMap);
                    } else if (boutiqueExceptionType.intValue() == 2 && productMap.get("seasonChange") != null) {
                        exceptionProductList.add(productMap);
                    }
                }
                productList = exceptionProductList;
            }
        }
        return productList;
    }

    private SearchCondition initCondition(SearchCondition searchParams, String state, Long categoryId, Integer tagType, Integer pageSize, Integer pageNo, Integer isExcludeCampaign, Long limitTimeTagId) {
        SearchCondition searchCondition = searchParams;
        searchCondition.setLimitTimeTagId(limitTimeTagId);
        searchCondition.setBoutiqueId(escapeLikeParams(searchParams.getBoutiqueId()));
        searchCondition.setCategoryId(categoryId == null ? null : categoryCache.getAllChildCategory(categoryId));
        searchCondition.setDesignerId(escapeLikeParams(searchParams.getDesignerId()));
        if (state != null) {
            searchCondition.setProductStatus(getStatusEnum(state).getProductStatus());
            searchCondition.setShopProductStatus(getStatusEnum(state).getShopProductStatus());
        }
        List<Integer> types = searchParams.getTagTypes();
        if (CollectionUtils.isEmpty(types)) {
            types = new ArrayList<>();
            types.add(1);
            types.add(5);
        }
        if (tagType != null) {
            types.add(tagType);
        }
        searchCondition.setTagTypes(types);
        List<Long> tagIds = searchParams.getTagIds();
        if (CollectionUtils.isEmpty(tagIds)) {
            tagIds = new ArrayList<>();
        }
        if (searchCondition.getTagId() != null && searchCondition.getTagId() > 0) {
            tagIds.add(searchCondition.getTagId());
        }
        searchCondition.setTagIds(tagIds);
        searchCondition.setStart((pageNo == null || pageNo < 0) ? 0 : (pageNo - 1) * pageSize);
        searchCondition.setCount((pageSize == null || pageSize < 0) ? 25 : pageSize);
        if (searchCondition.getTagId() != null) {
            List<Long> productList = null;
            if (searchCondition.getTagId() == -2 || searchCondition.getTagId() == 1) {
                productList = contentManagementService.listAllTagProductIds(types);
            } else if (tagIds.size() > 0) {
                productList = contentManagementService.listTagProductIds(tagIds);
            }
            if (productList != null && productList.size() > 0) {
                searchCondition.setProductIds(productList);
            }

        }

        //2018-07-16 Jain limit time过滤活动商品
        if(isExcludeCampaign != null && !isExcludeCampaign.equals(0)) {
            List<Long> listPrd = new ArrayList<>();
            //当前生效的
            Map<String, Integer> params = new HashMap<>();
            params.put("status", 0);
            List<Map<String, Object>> listActive = promotionService.selectPromotionByStatus(params);
            if (listActive != null && listActive.size() > 0) {
                for (Map<String, Object> map : listActive) {
                    listPrd.add(Long.parseLong(map.get("promotionId").toString()));
                }
            }

            //pending的
            params.put("status", 1);
            List<Map<String, Object>> listPending = promotionService.selectPromotionByStatus(params);
            if (listPending != null && listPending.size() > 0) {
                for (Map<String, Object> map : listPending) {
                    listPrd.add(Long.parseLong(map.get("promotionId").toString()));
                }
            }

            if (listPrd.size() > 0) {
                searchCondition.setPromotionIds(listPrd);
                LOGGER.info("listPrd size: " + listPrd.size());
            }
        }

        return searchCondition;
    }

    private boolean isEmptyTag(SearchCondition searchCondition) {
        if (searchCondition.getTagId() == null || searchCondition.getTagId() < 0) {
            return false;
        }
        if (searchCondition.getProductIds() == null || searchCondition.getProductIds().size() == 0) {
            return true;
        }
        return false;
    }

    private void appendInfo(List<Map<String, Object>> productList, StateEnum stateEnum, SearchCondition searchCondition) {
        setCategoryPath(productList);
        List<Map<String, Object>> skuStoreList = iSkuStoreService.listSkuStoreByProductList(productList);
        List<Map<String, Object>> tagsList = null;
        if (searchCondition.getTagId() != null || searchCondition.getTagTypes().size() > 0) {
            Map<String, Object> param = new HashMap<>();
            List<Long> pIds = new ArrayList<>();
            for (Map<String, Object> maps : productList) {
                pIds.add(Long.valueOf(maps.get("product_id").toString()));
            }
            param.put("pIds", pIds);
            param.put("types", searchCondition.getTagTypes());
            tagsList = contentManagementService.listTagsByProductIdsAndType(param);
        }

        for (Map<String, Object> product : productList) {
            setSkuInfo(product, skuStoreList);
            if (searchCondition.getTagId() != null || searchCondition.getTagTypes().size() > 0) {
                setTags(product, tagsList);
            }
            setState(product);

            getSpuModify(product);
            //如果关联spu有图片，则展示spu的图片
            //            if (product.get("spu_cover_img") != null && StringUtils.isNotBlank(product.get("spu_cover_img").toString())) {
            //                product.put("cover_img", product.get("spu_cover_img"));
            //            }
        }
    }

    //判断spu是否修改过
    private void getSpuModify(Map<String, Object> product) {
        if ((product.get("desc_modify") != null && (Boolean) product.get("desc_modify")) || (product.get("img_modified") != null && (Boolean) product.get(
                "img_modified")) || (product.get("vendor_id") != null && product.get("vendor_id").toString().equals("-1"))) {
            product.put("spuModified", "1");
        } else {
            product.put("spuModified", "0");
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

    private void setTags(Map<String, Object> product, List<Map<String, Object>> tagList) {
        List<Map<String, Object>> productTagList = new LinkedList<>();
        for (Map<String, Object> tag : tagList) {
            if (product.get("product_id").equals(tag.get("product_id"))) {
                tag.remove("product_id");
                productTagList.add(tag);
            }
        }
        product.put("tagList", productTagList);
    }

    private void setCategoryPath(List<Map<String, Object>> productList) {
        for (Map<String, Object> product : productList) {
            product.put("category", categoryCache.getAbsolutelyCategoryPath(Long.parseLong(product.get("category_id").toString())));
        }
    }

    private void setState(Map<String, Object> product) {
        StateEnum stateEnum = StateMachineCoreRule.map2StateEnum(product);
        if (stateEnum == StateEnum.OLD_PROCESSING) {
            stateEnum = StateEnum.PROCESSING;
        } else if (stateEnum == StateEnum.OLD_SHOP_PROCESSING) {
            stateEnum = StateEnum.SHOP_PROCESSING;
        }
        product.put("status", stateEnum);
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

        return input.replace("\\", "\\\\").replace("_", "\\_").trim();
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
