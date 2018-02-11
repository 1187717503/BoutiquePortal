package com.intramirror.product.core.impl.promotion;

import com.alibaba.fastjson.JSONObject;
import com.intramirror.product.api.entity.promotion.BrandEntity;
import com.intramirror.product.api.entity.promotion.BrandSort;
import com.intramirror.product.api.entity.promotion.CategoryEntity;
import com.intramirror.product.api.entity.promotion.CategorySort;
import com.intramirror.product.api.entity.promotion.SortPromotion;
import com.intramirror.product.api.entity.promotion.VendorSort;
import com.intramirror.product.api.enums.PromotionRuleType;
import com.intramirror.product.api.enums.SortColumn;
import com.intramirror.product.api.exception.BusinessException;
import com.intramirror.product.api.model.Category;
import com.intramirror.product.api.model.Promotion;
import com.intramirror.product.api.model.PromotionExclude;
import com.intramirror.product.api.model.PromotionInclude;
import com.intramirror.product.api.model.PromotionRule;
import com.intramirror.product.api.model.PromotionRuleDetail;
import com.intramirror.product.api.service.promotion.IPromotionService;
import com.intramirror.product.core.mapper.CategoryMapper;
import com.intramirror.product.core.mapper.PromotionMapper;
import com.intramirror.product.core.mapper.PromotionRuleMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created on 2018/1/4.
 * @author 123
 */
@Service(value = "promotionService")
public class PromotionServiceImpl implements IPromotionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PromotionServiceImpl.class);

    @Autowired
    PromotionRuleMapper promotionRuleMapper;

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    PromotionMapper promotionMapper;

    @Override
    public List<Map<String, Object>> listPromotionByBanner(Long bannerId) {
        return promotionRuleMapper.listPromotionByBanner(bannerId);
    }

    @Override
    public List<Map<String, Object>> listExcludeRulePromotion(Long promotionId) {
        return promotionRuleMapper.listExcludeRulePromotion(promotionId);
    }

    @Override
    public List<Map<String, Object>> listIncludeRulePromotion(Long promotionId) {
        return promotionRuleMapper.listIncludeRulePromotion(promotionId);
    }

    @Transactional
    @Override
    public PromotionRule processPromotionRule(PromotionRule rule, PromotionRuleType ruleType) {
        LOGGER.info("Start to save promotion include rule.");
        if (ruleType == PromotionRuleType.INCLUDE_RULE) {
            promotionRuleMapper.insertIncludeRule(rule);
        } else {
            promotionRuleMapper.insertExcludeRule(rule);
        }
        insertRuleDetailByRule(rule, ruleType);

        return rule;
    }

    @Transactional
    @Override
    public Boolean removePromotionRule(Long ruleId, PromotionRuleType ruleType) {
        Boolean flag = false;
        if (ruleType == PromotionRuleType.INCLUDE_RULE) {
            if (promotionRuleMapper.removeIncludeRule(ruleId) > 0) {
                flag = promotionRuleMapper.removeIncludeRuleDetail(ruleId) > 0;
            }

        } else {
            if (promotionRuleMapper.removeExcludeRule(ruleId) > 0) {
                flag = promotionRuleMapper.removeExcludeRuleDetail(ruleId) > 0;
            }
        }

        return flag;
    }

    @Transactional
    @Override
    public Boolean updatePromotionRule(PromotionRuleType ruleType, PromotionRule rule) {
        Boolean flag;
        if (ruleType == PromotionRuleType.INCLUDE_RULE) {
            flag = promotionRuleMapper.removeIncludeRuleDetail(rule.getRuleId()) > 0;
            flag = (flag & promotionRuleMapper.updateIncludeRule(rule) > 0);

        } else {
            flag = promotionRuleMapper.removeExcludeRuleDetail(rule.getRuleId()) > 0;
            flag = (flag & promotionRuleMapper.updateExcludeRule(rule) > 0);
        }
        return flag & (insertRuleDetailByRule(rule, ruleType).size() > 0);

    }

    @Override
    public List<Map<String, Object>> listSortColumn(Long promotionId) {
        return promotionRuleMapper.listSortColumn(promotionId);
    }

    @Transactional
    @Override
    public Boolean updateSortPromotion(List<SortPromotion> listSort) throws BusinessException {
        for (SortPromotion sortPromotion : listSort) {
            if (promotionRuleMapper.updateSortColumn(sortPromotion) <= 0) {
                throw new BusinessException("update sort failed.");
            }
        }
        return true;
    }

    @Override
    public List<Map<String, Object>> listSortItemByColumn(Long promotionId, SortColumn sortColumn) {
        List<Map<String, Object>> result = null;
        switch (sortColumn) {
        case BRAND:
            result = promotionRuleMapper.getBrandSortItem(promotionId);
            break;
        case VENDOR:
            result = promotionRuleMapper.getVendorSortItem(promotionId);
            break;
        case CATEGORY:
            result = promotionRuleMapper.getCategorySortItem(promotionId);
            break;
        case DISCOUNT:
        case SEASON:
            SortPromotion sortPromotion = new SortPromotion();
            sortPromotion.setColumnName(sortColumn.getValue());
            sortPromotion.setPromotionId(promotionId);
            result = promotionRuleMapper.getSimpleSort(sortPromotion);
            break;
        }
        return result;
    }

    @Transactional
    @Override
    public Boolean updateItemsSort(Long promotionId, SortColumn sortColumn, List<Map<String, Object>> items) throws BusinessException {
        Boolean result = false;
        switch (sortColumn) {
        case BRAND:
            result = updateBrandSortItems(promotionId, items);
            break;
        case VENDOR:
            result = updateVendSortItems(promotionId, items);
            break;
        case CATEGORY:
            result = updateCategorySortItems(promotionId, items);
            break;
        case DISCOUNT:
        case SEASON:
            result = updateSimpleSort(promotionId, sortColumn, items);
            break;
        }
        return result;
    }

    private Boolean updateCategorySortItems(Long promotionId, List<Map<String, Object>> items) throws BusinessException {
        try {
            promotionRuleMapper.removeCategorySortItems(promotionId);
            for (Map<String, Object> item : items) {

                List<Category> subCategoryList = categoryMapper.listSubCategoryByCategoryId(Long.parseLong(item.get("categoryId").toString()));
                for (Category category : subCategoryList) {
                    CategorySort categorySort = new CategorySort();
                    categorySort.setPromotionId(promotionId);
                    categorySort.setCategoryId(category.getCategoryId());
                    categorySort.setSort(Integer.parseInt(item.get("sort").toString()));
                    categorySort.setChineseName(category.getChineseName());
                    categorySort.setParentId(category.getParentId());
                    categorySort.setShowCodeInt(category.getShowCodeInt());
                    categorySort.setName(category.getName());
                    categorySort.setLevel(category.getLevel().intValue());
                    LOGGER.info("Category id {}, sort {}.", category.getCategoryId(), item.get("sort").toString());
                    promotionRuleMapper.insertCategorySort(categorySort);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("update category sort failed.");
        }
        return true;
    }

    private Boolean updateVendSortItems(Long promotionId, List<Map<String, Object>> items) throws BusinessException {
        try {
            promotionRuleMapper.removeVendorSortItems(promotionId);
            for (Map<String, Object> item : items) {
                VendorSort vendorSort = new VendorSort();
                vendorSort.setVendorId(Long.parseLong(item.get("vendorId").toString()));
                vendorSort.setSort(Integer.parseInt(item.get("sort").toString()));
                vendorSort.setPromotionId(promotionId);
                vendorSort.setName(item.get("name").toString());
                promotionRuleMapper.insertVendSort(vendorSort);
            }
        } catch (Exception e) {
            throw new BusinessException("update vendor sort failed.");
        }
        return true;
    }

    private Boolean updateBrandSortItems(Long promotionId, List<Map<String, Object>> items) throws BusinessException {
        try {
            promotionRuleMapper.removeBrandSortItems(promotionId);
            for (Map<String, Object> item : items) {
                BrandSort brandSort = new BrandSort();
                brandSort.setBrandId(Long.parseLong(item.get("brandId").toString()));
                brandSort.setSort(Integer.parseInt(item.get("sort").toString()));
                brandSort.setPromotionId(promotionId);
                brandSort.setName(item.get("name").toString());
                promotionRuleMapper.insertBrandSort(brandSort);
            }

        } catch (Exception e) {

            throw new BusinessException("update brand sort failed.");
        }
        return true;
    }

    private Boolean updateSimpleSort(Long promotionId, SortColumn sortColumn, List<Map<String, Object>> items) {
        SortPromotion sortPromotion = new SortPromotion();
        sortPromotion.setPromotionId(promotionId);
        sortPromotion.setColumnName(sortColumn.getValue());
        sortPromotion.setSeqType(Integer.parseInt(items.get(0).get("seqType").toString()));
        return promotionRuleMapper.updateSimpleSort(sortPromotion) > 0;
    }

    private void removeSubCategoryInList(List<CategoryEntity> list, Long parentId, int level) {
        for (CategoryEntity category : list) {
            if (category.getDel()) {
                continue;
            }
            if (category.getParentId().longValue() == parentId.longValue()) {
                if (level < 3) {
                    removeSubCategoryInList(list, category.getCategoryId(), level + 1);
                }
                category.setDel(true);
            }
        }
    }

    private List<PromotionRuleDetail> insertRuleDetailByRule(PromotionRule rule, PromotionRuleType ruleType) {
        List<BrandEntity> listBrand = JSONObject.parseArray(rule.getBrands(), BrandEntity.class);
        List<CategoryEntity> listCategory = JSONObject.parseArray(rule.getCategorys(), CategoryEntity.class);

        List<CategoryEntity> listRuleCategory = new ArrayList<>();

        Category param = new Category();
        param.setEnabled(true);
        List<Category> listDBCategory = categoryMapper.listAllCategoryByConditions(param);
        Boolean allCategory = false;
        CategoryEntity allCategoryEntity = null;
        for (CategoryEntity category : listCategory) {
            if (category.getCategoryId() == -1L) {
                allCategory = true;
                allCategoryEntity = category;
                break;
            }

            for (Category dbCategory : listDBCategory) {
                if (dbCategory.getCategoryId().longValue() == category.getCategoryId().longValue()) {
                    category.setLevel(dbCategory.getLevel());
                    category.setParentId(dbCategory.getParentId());
                    category.setDel(false);
                }
            }
        }

        if (allCategory) {
            listRuleCategory.add(allCategoryEntity);
        } else {

            for (CategoryEntity category : listCategory) {
                if (category.getLevel() == 1 || category.getLevel() == 2) {
                    category.setDel(true);
                }
            }

            //            for (CategoryEntity category : listCategory) {
            //                if (category.getLevel() == 1) {
            //                    removeSubCategoryInList(listCategory, category.getCategoryId(), 1);
            //                }
            //            }
            //
            //            for (CategoryEntity category : listCategory) {
            //                if (category.getDel()) {
            //                    continue;
            //                }
            //
            //                if (category.getLevel() == 2) {
            //                    removeSubCategoryInList(listCategory, category.getCategoryId(), 2);
            //                }
            //            }
            //
            //            for (CategoryEntity category : listCategory) {
            //                if (category.getDel()) {
            //                    continue;
            //                }
            //
            //                if (category.getLevel() == 3) {
            //                    removeSubCategoryInList(listCategory, category.getCategoryId(), 3);
            //                }
            //            }

            listRuleCategory = listCategory;
        }

        List<PromotionRuleDetail> listPromotionRuleDetail = new ArrayList<>();
        for (BrandEntity brand : listBrand) {
            for (CategoryEntity category : listRuleCategory) {
                if (category.getDel() != null && category.getDel()) {
                    continue;
                }

                if (ruleType == PromotionRuleType.INCLUDE_RULE) {
                    PromotionInclude ruleDetail = new PromotionInclude();
                    ruleDetail.setBrandId(brand.getBrandId());
                    ruleDetail.setCategoryId(category.getCategoryId());
                    ruleDetail.setPromotionId(rule.getPromotionId());
                    ruleDetail.setPromotionIncludeRuleId(rule.getRuleId());
                    ruleDetail.setSeasonCode(rule.getSeasonCode());
                    ruleDetail.setVendorId(rule.getVendorId());
                    promotionRuleMapper.insertIncludeRuleDetail(ruleDetail);
                    listPromotionRuleDetail.add(ruleDetail);
                } else {
                    PromotionExclude ruleDetail = new PromotionExclude();
                    ruleDetail.setBrandId(brand.getBrandId());
                    ruleDetail.setCategoryId(category.getCategoryId());
                    ruleDetail.setPromotionId(rule.getPromotionId());
                    ruleDetail.setPromotionExcludeRuleId(rule.getRuleId());
                    ruleDetail.setSeasonCode(rule.getSeasonCode());
                    ruleDetail.setVendorId(rule.getVendorId());
                    promotionRuleMapper.insertExcludeRuleDetail(ruleDetail);
                    listPromotionRuleDetail.add(ruleDetail);
                }
            }
        }
        return listPromotionRuleDetail;
    }

    @Override
    public List<Map<String, Object>> listBannerPos() {
        return promotionRuleMapper.listBannerPos();
    }

    @Override
    public List<Map<String, Object>> listPromotionByBannerIds(List<Long> bannerIds) {
        return promotionRuleMapper.listPromotionByBannerIds(bannerIds);
    }

    @Override
    public Integer saveImgForBanner(Promotion promotion) {
        return promotionMapper.updateByPrimaryKeySelective(promotion);
    }

    @Override
    public Promotion getPromotion(Long promotionId) {
        return promotionMapper.selectByPrimaryKey(promotionId);
    }
}
