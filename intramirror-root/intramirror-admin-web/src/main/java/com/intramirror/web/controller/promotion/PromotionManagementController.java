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
import com.intramirror.product.api.model.ProductWithBLOBs;
import com.intramirror.product.api.model.Promotion;
import com.intramirror.product.api.model.PromotionBrandHot;
import com.intramirror.product.api.model.PromotionExcludeProduct;
import com.intramirror.product.api.model.PromotionRule;
import com.intramirror.product.api.service.IProductService;
import com.intramirror.product.api.service.brand.IBrandService;
import com.intramirror.product.api.service.category.ICategoryService;
import com.intramirror.product.api.service.promotion.IPromotionExcludeProductService;
import com.intramirror.product.api.service.promotion.IPromotionService;
import static com.intramirror.utils.transform.JsonTransformUtil.toJson;
import static com.intramirror.web.common.request.ConstantsEntity.EXCLUDE;
import static com.intramirror.web.common.request.ConstantsEntity.INCLUDE;
import static com.intramirror.web.common.request.ConstantsEntity.INCLUDE_IMPORT;
import com.intramirror.web.common.request.PromotionRuleEntity;
import com.intramirror.web.controller.cache.CategoryCache;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            List<PromotionRule> listRule = transformImportRule(body);
            promotionService.processImportPromotionRule(listRule);
            return Response.status(StatusType.SUCCESS).data(listRule);

        } else {
            return Response.status(StatusType.PARAM_NOT_POSITIVE).build();
        }

        PromotionRule promotionRule = transformPromotionRule(body);
        promotionRule = promotionService.processPromotionRule(promotionRule, type);

        return Response.status(StatusType.SUCCESS).data(promotionRule);

    }

    private List<PromotionRule> transformImportRule(PromotionRuleEntity body) throws Exception {
        List<ImportDataEntity> listImportData = body.getImportData();
        List<PromotionRule> listRule = new ArrayList<>();

        PromotionRuleEntity pre = new PromotionRuleEntity();
        pre.setPromotionId(body.getPromotionId());
        pre.setRuleId(body.getRuleId());
        pre.setSeasonCode(body.getSeasonCode());
        pre.setVendorId(body.getVendorId());

        for (ImportDataEntity idata : listImportData) {
            BrandEntity be = new BrandEntity();
            List<BrandEntity> listBrand = new ArrayList<BrandEntity>();
            List<CategoryEntity> listCategory = new ArrayList<CategoryEntity>();

            List<Map<String, Object>> listBrandTmp = productBrandServiceImpl.getBrandByName(idata.getBrandName());
            if (listBrandTmp.size() == 1) {
                Map<String, Object> brandMap = listBrandTmp.get(0);
                be.setBrandId(Long.parseLong(brandMap.get("brand_id").toString()));
                be.setName((String) brandMap.get("english_name"));
                listBrand.add(be);
            }
            pre.setBrands(listBrand);
            LOGGER.info("==Jian add brandEntity [{}].", listBrand);

            List<CategoryEntity> listCategoryTmp = idata.getCategorys();
            LOGGER.info("==Jian listCategoryTmp.size() is [{}].", listCategoryTmp.size());
            for (CategoryEntity ce : listCategoryTmp) {
                List<Map<String, Object>> listCategoryTmp1 = productCategoryServiceImpl.getSubCidByPid(ce.getCategoryId());
                listCategory.add(ce);
                LOGGER.info("==Jian add categoryEntity [{}].", ce);
                if (listCategoryTmp1.size() >= 1) {
                    for (Map<String, Object> it : listCategoryTmp1) {
                        CategoryEntity ceTmp = new CategoryEntity();
                        ceTmp.setCategoryId(Long.parseLong(it.get("category_id").toString()));
                        listCategory.add(ceTmp);
                    }
                }
            }
            pre.setCategorys(listCategory);
            LOGGER.info("==Jian add categoryEntity [{}].", listCategory);

            PromotionRule promotionRule = transformPromotionRule(pre);
            listRule.add(promotionRule);
        }
        return listRule;
    }

    @DeleteMapping(value = "/promotion/{ruleType}/{ruleId}")
    public Response removePromotionRule(@PathVariable(value = "ruleType") String ruleType, @PathVariable("ruleId") Long ruleId) {
        LOGGER.info("Start to remove rule with type {} and ruleId {}.", ruleType, ruleId);

        PromotionRuleType type;
        if (INCLUDE.equals(ruleType)) {
            type = PromotionRuleType.INCLUDE_RULE;
        } else if (EXCLUDE.equals(ruleType)) {
            type = PromotionRuleType.EXCLUDE_RULE;
        } else {
            return Response.status(StatusType.PARAM_NOT_POSITIVE).build();
        }

        Boolean result = promotionService.removePromotionRule(ruleId, type);
        return Response.status(StatusType.SUCCESS).data(result);
    }

    @PutMapping(value = "/promotion/product/exclude", consumes = "application/json")
    public Response savePromotionExcludeProduct(@RequestBody PromotionExcludeProduct promotionExcludeProduct) {
        LOGGER.info("Add promotion exclude product by promotionExcludeProduct: {}", toJson(promotionExcludeProduct));

        ProductWithBLOBs productWithBLOBs = productService.selectByPrimaryKey(promotionExcludeProduct.getProductId());
        if (productWithBLOBs == null || !productWithBLOBs.getEnabled()) {
            return Response.status(StatusType.FAILURE).data("product id not found.");
        }

        List<Map<String, Object>> promotionExcludeProducts = promotionExcludeProductService.selectByParameter(promotionExcludeProduct);
        if (promotionExcludeProducts.size() > 0) {
            return Response.status(StatusType.FAILURE).data("product id already existed.");
        }

        promotionExcludeProductService.insertPromotionExcludeProduct(promotionExcludeProduct);
        promotionExcludeProduct.setName(productWithBLOBs.getName());
        return Response.status(StatusType.SUCCESS).data(promotionExcludeProduct);
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
        promotionRule.setSeasonCode(body.getSeasonCode());

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
