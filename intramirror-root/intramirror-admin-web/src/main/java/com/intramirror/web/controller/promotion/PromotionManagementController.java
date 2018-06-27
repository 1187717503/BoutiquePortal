package com.intramirror.web.controller.promotion;

import com.alibaba.fastjson.JSONArray;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.core.common.exception.ValidateException;
import com.intramirror.core.common.response.ErrorResponse;
import com.intramirror.core.common.response.Response;
import com.intramirror.product.api.entity.promotion.BrandEntity;
import com.intramirror.product.api.entity.promotion.CategoryEntity;
import com.intramirror.product.api.entity.promotion.ImportDataEntity;
import com.intramirror.product.api.enums.PromotionRuleType;
import com.intramirror.product.api.model.*;
import com.intramirror.product.api.service.IProductService;
import com.intramirror.product.api.service.brand.IBrandService;
import com.intramirror.product.api.service.category.ICategoryService;
import com.intramirror.product.api.service.promotion.IPromotionExcludeProductService;
import com.intramirror.product.api.service.promotion.IPromotionService;
import com.intramirror.web.common.request.PromotionRuleEntity;
import com.intramirror.web.controller.cache.CategoryCache;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.intramirror.utils.transform.JsonTransformUtil.toJson;
import static com.intramirror.web.common.request.ConstantsEntity.*;

/**
 * Created on 2018/1/4.
 * @author 123
 */
@RestController
public class PromotionManagementController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PromotionManagementController.class);

    @Autowired
    IPromotionService promotionService;

    @Autowired
    CategoryCache categoryCache;

    @Autowired
    IPromotionExcludeProductService promotionExcludeProductService;

    @Autowired
    IProductService productService;

    @Autowired
    IBrandService productBrandServiceImpl;

    @Autowired
    ICategoryService productCategoryServiceImpl;

    static final String PR_INC_LIST = "ListIncPromotionRule";
    static final String PR_EXC_LIST = "ListExcPromotionRule";

    @GetMapping(value = "/banner/promotion")
    public Response getAllPromotion(@Param(value = "bannerId") Long bannerId) {
        List<Map<String, Object>> result = promotionService.listPromotionByBanner(bannerId);
        return Response.status(StatusType.SUCCESS).data(result);
    }

    @PostMapping(value = "/promotion/{ruleType}", consumes = "application/json")
    public Response savePromotionProductRule(@PathVariable(value = "ruleType") String ruleType, @RequestBody PromotionRuleEntity body) throws Exception {
        LOGGER.info("Save rule with type {}, {}.", ruleType, body);
        if (body.getPromotionId() == null) {
            return Response.status(StatusType.PARAM_NOT_POSITIVE).build();
        }

        PromotionRuleType type;
        if (INCLUDE.equals(ruleType)) {
            type = PromotionRuleType.INCLUDE_RULE;
        } else if (EXCLUDE.equals(ruleType)) {
            type = PromotionRuleType.EXCLUDE_RULE;
        } else if (INCLUDE_IMPORT.equals(ruleType)) {
            //2018-4-18 Jian
            Map<String, List<PromotionRuleEntity>> mapRule2Import = dealImportRule(body);

            //deal include rule
            List<PromotionRuleEntity> listPreInc = mapRule2Import.get(PR_INC_LIST);
            List<PromotionRule> listRuleInc = new ArrayList<>();
            if (listPreInc != null) {
                for (int i = 0; i < listPreInc.size(); i++) {
                    listRuleInc.add(transformPromotionRule(listPreInc.get(i)));
                }
                promotionService.processImportPromotionRule(0, listRuleInc);
            }

            //deal exclude rule
            List<PromotionRuleEntity> listPreExc = mapRule2Import.get(PR_EXC_LIST);
            List<PromotionRule> listRuleExc = new ArrayList<>();
            if (listPreExc != null) {
                for (int i = 0; i < listPreExc.size(); i++) {
                    listRuleExc.add(transformPromotionRule(listPreExc.get(i)));
                }
                promotionService.processImportPromotionRule(1, listRuleExc);
            }
            return Response.status(StatusType.SUCCESS).data(mapRule2Import);

        } else {
            return Response.status(StatusType.PARAM_NOT_POSITIVE).build();
        }

        PromotionRule promotionRule = transformPromotionRule(body);
        promotionRule = promotionService.processPromotionRule(promotionRule, type);

        return Response.status(StatusType.SUCCESS).data(promotionRule);

    }

    private Map<String, List<PromotionRuleEntity>> dealImportRule(PromotionRuleEntity body) {
        List<ImportDataEntity> listImportData = body.getImportData();
        Map<Long, String> defaultStatus = new HashMap<>();
        Map<String, List<PromotionRuleEntity>> prMap = new HashMap<>();

        List<PromotionRuleEntity> listPreInclude = new ArrayList<>();
        List<PromotionRuleEntity> listPreExclude = new ArrayList<>();

        for (ImportDataEntity idata : listImportData) {
            Boolean hasInc = false;
            Boolean hasExc = false;
            BrandEntity be = new BrandEntity();
            List<BrandEntity> listBrandInc = new ArrayList<>();
            List<BrandEntity> listBrandExc = new ArrayList<>();
            List<CategoryEntity> listCategoryInc = new ArrayList<>();
            List<CategoryEntity> listCategoryExc = new ArrayList<>();

            if (idata.getBrandName().equals("Default")) {
                List<CategoryEntity> listCategoryTmp = idata.getCategorys();
                for (CategoryEntity ce : listCategoryTmp) {
                    //记录每个default的类目对应的状态
                    defaultStatus.put(ce.getCategoryId(), ce.getStatus());

                    if (ce.getStatus().equals("Y")) {
                        //Include需考虑default配置all brand，Exclude不需要考虑配置All brand
                        if (listBrandInc.size() < 1) {
                            be.setName("ALL");
                            be.setBrandId(-1L);
                            listBrandInc.add(be);
                        }

                        //同时添加2，3级类目
                        listCategoryInc.add(ce);
                        setCategoryList(ce.getCategoryId(), listCategoryInc);
                        hasInc = true;
                    }
                }

                if (hasInc) {
                    listPreInclude.add(initPromotionRuleEntity(body, listBrandInc, listCategoryInc));
                }
            } else {

                List<CategoryEntity> listCategoryTmp = idata.getCategorys();
                for (CategoryEntity ce : listCategoryTmp) {
                    if (!defaultStatus.get(ce.getCategoryId()).equals(ce.getStatus())) {
                        if (ce.getStatus().equals("Y")) {
                            generateBrand2CategoryList(idata.getBrandName(), ce, listBrandInc, listCategoryInc);
                            hasInc = true;
                        } else {
                            generateBrand2CategoryList(idata.getBrandName(), ce, listBrandExc, listCategoryExc);
                            hasExc = true;
                        }
                    }
                }

                if (hasInc) {
                    listPreInclude.add(initPromotionRuleEntity(body, listBrandInc, listCategoryInc));
                }

                if (hasExc) {
                    listPreExclude.add(initPromotionRuleEntity(body, listBrandExc, listCategoryExc));
                }
            }
        }
        prMap.put(PR_INC_LIST, listPreInclude);
        prMap.put(PR_EXC_LIST, listPreExclude);
        return prMap;
    }

    private PromotionRuleEntity initPromotionRuleEntity(PromotionRuleEntity body, List<BrandEntity> listBrand, List<CategoryEntity> listCategory) {
        PromotionRuleEntity pre = new PromotionRuleEntity();
        pre.setPromotionId(body.getPromotionId());
        pre.setRuleId(body.getRuleId());
        pre.setSeasonCode(body.getSeasonCode());
        pre.setVendorId(body.getVendorId());
        pre.setBrands(listBrand);
        pre.setCategorys(listCategory);
        return pre;
    }

    private void generateBrand2CategoryList(String brandName, CategoryEntity ce, List<BrandEntity> listBrand, List<CategoryEntity> listCategory) {
        //记录include
        if (listBrand.size() < 1) {
            setBrandList(brandName, listBrand);
        }

        //同时添加2，3级类目
        listCategory.add(ce);
        setCategoryList(ce.getCategoryId(), listCategory);
    }

    private void setBrandList(String brandName, List<BrandEntity> listBrand) {
        BrandEntity be = new BrandEntity();
        List<Map<String, Object>> listBrandTmp = productBrandServiceImpl.getBrandByName(brandName);
        if (listBrandTmp.size() == 1) {
            Map<String, Object> brandMap = listBrandTmp.get(0);
            be.setBrandId(Long.parseLong(brandMap.get("brand_id").toString()));
            be.setName((String) brandMap.get("english_name"));
            listBrand.add(be);
        }
    }

    private void setCategoryList(Long categoryId, List<CategoryEntity> listCategory) {
        List<Map<String, Object>> listCategoryTmp = productCategoryServiceImpl.getSubCidByPid(categoryId);
        if (listCategoryTmp.size() >= 1) {
            for (Map<String, Object> it : listCategoryTmp) {
                CategoryEntity ce = new CategoryEntity();
                ce.setCategoryId(Long.parseLong(it.get("category_id").toString()));
                listCategory.add(ce);
            }
        }
    }

    @DeleteMapping(value = "/promotion/delete/vendor/{ruleType}")
    public Response removePromotionRule(@PathVariable(value = "ruleType") String ruleType, @RequestParam("ruleIds") List<Long> ruleIds) {
        LOGGER.info("Start to remove rule with type {} and ruleId {}.", ruleType, ruleIds);

        PromotionRuleType type;
        if (INCLUDE.equals(ruleType)) {
            type = PromotionRuleType.INCLUDE_RULE;
        } else if (EXCLUDE.equals(ruleType)) {
            type = PromotionRuleType.EXCLUDE_RULE;
        } else {
            return Response.status(StatusType.PARAM_NOT_POSITIVE).build();
        }

        if (ruleIds.size() < 1) {
            return Response.status(StatusType.FAILURE).data("vendor id not found.");
        }

        Boolean result = promotionService.removePromotionRule(ruleIds, type);
        return Response.status(StatusType.SUCCESS).data(result);
    }

    @PutMapping(value = "/promotion/product/exclude", consumes = "application/json")
    public Response savePromotionExcludeProduct(@RequestBody PromotionExcludeProduct promotionExcludeProduct) {
        LOGGER.info("Add promotion exclude product by promotionExcludeProduct: {}", toJson(promotionExcludeProduct));

        ProductWithBLOBs product = new ProductWithBLOBs();
        product.setProductId(promotionExcludeProduct.getProductId());
        product.setProductIds(promotionExcludeProduct.getProductIds());
        product.setProductCode(promotionExcludeProduct.getProductCode());
        product.setDesignerId(promotionExcludeProduct.getDesignerId());
        product.setColorCode(promotionExcludeProduct.getColorCode());
        product.setEnabled(true);
        List<ProductWithBLOBs> productWithBLOBsList = productService.getProductByParameter(product);
        if (CollectionUtils.isEmpty(productWithBLOBsList)) {
            return Response.status(StatusType.FAILURE).data("product not found.");
        }
        List<Long> productIds = new ArrayList<>();
        for (ProductWithBLOBs productWithBLOBs : productWithBLOBsList) {
            productIds.add(productWithBLOBs.getProductId());
        }

        Map<String, Object> params= new HashMap<>();
        params.put("productIds", productIds);
        params.put("promotionId", promotionExcludeProduct.getPromotionId());
        List<Long> existsProductIds = promotionExcludeProductService.getPromotionProductIdByParameter(params);

        productIds.removeAll(existsProductIds);
        if (productIds.size() > 0) {
            return Response.status(StatusType.FAILURE).data("product id already existed.");
        }
        promotionExcludeProduct.setProductIds(productIds);
        promotionExcludeProductService.insertPromotionExcludeProduct(promotionExcludeProduct);
//        promotionExcludeProduct.setName(productWithBLOBs.getName());
        return Response.status(StatusType.SUCCESS).build();
    }

    @DeleteMapping(value = "/promotion/product/exclude/{promotionExcludeProductId}")
    public Response removePromotionExcludeProduct(@PathVariable("promotionExcludeProductId") Long promotionExcludeProductId) {
        LOGGER.info("remove promotion exclude product by promotionExcludeProductId: {}", promotionExcludeProductId);

        Long row = promotionExcludeProductService.deletePromotionExcludeProduct(promotionExcludeProductId);

        return Response.status(StatusType.SUCCESS).data(row);
    }

    @GetMapping(value = "/promotion/product/exclude")
    public Response queryPromotionExcludeProduct(@RequestParam(value = "promotionId", required = true) Long promotionId) {
        LOGGER.info("Search promotion exclude product by promotionId: {}", promotionId);

        PromotionExcludeProduct promotionExcludeProduct = new PromotionExcludeProduct(promotionId);
        List<Map<String, Object>> promotionExcludeProducts = promotionExcludeProductService.selectByParameter(promotionExcludeProduct);
        return Response.status(StatusType.SUCCESS).data(promotionExcludeProducts);
    }

    @PutMapping(value = "/promotion/{ruleType}", consumes = "application/json")
    public Response setPromotionRule(@PathVariable(value = "ruleType") String ruleType, @RequestBody PromotionRuleEntity body) {
        LOGGER.info("Update rule with type {}, {}.", ruleType, body);
        if (body.getRuleId() == null) {
            return Response.status(StatusType.FAILURE).data("rule id is empty.");
        }

        PromotionRuleType type;
        if (INCLUDE.equals(ruleType)) {

            type = PromotionRuleType.INCLUDE_RULE;

        } else if (EXCLUDE.equals(ruleType)) {

            type = PromotionRuleType.EXCLUDE_RULE;
        } else {
            return Response.status(StatusType.PARAM_NOT_POSITIVE).build();
        }

        PromotionRule promotionRule = transformPromotionRule(body);

        return Response.status(StatusType.SUCCESS).data(promotionService.updatePromotionRule(type, promotionRule));
    }

    private PromotionRule transformPromotionRule(PromotionRuleEntity body) {
        PromotionRule promotionRule = new PromotionRule();
        promotionRule.setRuleId(body.getRuleId());
        promotionRule.setPromotionId(body.getPromotionId());
        promotionRule.setVendorId(body.getVendorId());
        promotionRule.setSeasonCodes(body.getSeasonCode());

        promotionRule.setBrands(JSONArray.toJSONString(body.getBrands()));
        for (CategoryEntity category : body.getCategorys()) {
            if (category.getCategoryId() == -1L) {
                category.setName("All Category");
            } else {
                category.setName(categoryCache.getAbsolutelyCategoryPath(category.getCategoryId()));
            }
        }

        promotionRule.setCategorys(JSONArray.toJSONString(body.getCategorys()));
        return promotionRule;
    }

    @GetMapping(value = "/banner/bannerPosList")
    public Response getAllBannerPos() {
        List<Map<String, Object>> listBannerPos = promotionService.listBannerPos();
        List<Long> bannerIds = new ArrayList<>();
        for (int i = 0; i < listBannerPos.size(); i++) {
            Map<String, Object> banner = listBannerPos.get(i);
            bannerIds.add((Long) banner.get("bannerId"));
        }

        List<Map<String, Object>> listPromotions = new ArrayList<>();
        if (bannerIds.size() > 0) {
            listPromotions = promotionService.listPromotionByBannerIds(bannerIds);
        }

        List<Map<String, Object>> listResult = new ArrayList<>();
        for (Map<String, Object> bannerPos : listBannerPos) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("bannerId", bannerPos.get("bannerId"));
            resultMap.put("bannerPosId", bannerPos.get("bannerPosId"));
            resultMap.put("bannerName", bannerPos.get("bannerName"));
            List<Map<String, Object>> listPro = new ArrayList<>();
            for (Map<String, Object> promotion : listPromotions) {
                if (promotion.get("bannerId") == bannerPos.get("bannerId")) {
                    listPro.add(promotion);
                }
            }
            resultMap.put("promotion", listPro);
            listResult.add(resultMap);
        }

        return Response.status(StatusType.SUCCESS).data(listResult);
    }

    @PutMapping(value = "/banner/updateImg", consumes = "application/json")
    public Response saveImgForBanner(@RequestBody Promotion promotion) {
        if (promotion == null || promotion.getPromotionId() == null) {
            throw new ValidateException(new ErrorResponse("Input promotion info could not be null!"));
        }
        return Response.status(StatusType.SUCCESS).data(promotionService.saveImgForBanner(promotion));
    }

    @GetMapping(value = "/banner/promotion/pid")
    public Response getPromotion(@RequestParam(value = "promotionId", required = true) Long promotionId) {
        return Response.status(StatusType.SUCCESS).data(promotionService.getPromotion(promotionId));
    }

    @GetMapping(value = "/banner/promotion/brandhot")
    public Response getPromotionBrandHot(@RequestParam(value = "promotionId", required = true) Long promotionId) {
        return Response.status(StatusType.SUCCESS).data(promotionService.getPromotionBrandHot(promotionId));
    }

    @PutMapping(value = "/banner/promotion/brandhot")
    public Response updatePromotionBrandHot(@RequestBody List<PromotionBrandHot> listPromotionBrandHot) {
        return Response.status(StatusType.SUCCESS).data(promotionService.updatePromotionBrandHot(listPromotionBrandHot));
    }

    @PutMapping(value = "/promotion/refresh/{promotionId}")
    public Response refreshSnapshotProduct(@PathVariable("promotionId") Long promotionId) {
        LOGGER.info("Promotion {} start to refresh snapshot product", promotionId);

        promotionService.refreshSnapshotProductByPromotion(promotionId);
        return Response.status(StatusType.SUCCESS).data("ok");
    }

    @GetMapping(value = "/promotion/addtoshop/{productId}")
    public Response addtoShop(@PathVariable("productId") Long productId) {
        promotionService.refreshSnapshotForAddProduct(productId);
        return Response.status(StatusType.SUCCESS).data("ok");
    }
}
