package com.intramirror.web.common.request;

import com.intramirror.product.api.entity.promotion.BrandEntity;
import com.intramirror.product.api.entity.promotion.CategoryEntity;
import java.util.List;

/**
 * Created on 2018/1/5.
 * @author 123
 */
public class PromotionRuleEntity {
    List<BrandEntity> brands;
    List<CategoryEntity> categorys;
    Long promotionId;
    Long vendorId;
    String seasonCode;

    public List<BrandEntity> getBrands() {
        return brands;
    }

    public void setBrands(List<BrandEntity> brands) {
        this.brands = brands;
    }

    public List<CategoryEntity> getCategorys() {
        return categorys;
    }

    public void setCategorys(List<CategoryEntity> categorys) {
        this.categorys = categorys;
    }

    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public String getSeasonCode() {
        return seasonCode;
    }

    public void setSeasonCode(String seasonCode) {
        this.seasonCode = seasonCode;
    }

    @Override
    public String toString() {
        return "PromotionRuleEntity{" + "brands=" + brands + ", categorys=" + categorys + ", promotionId=" + promotionId + ", vendorId=" + vendorId
                + ", seasonCode='" + seasonCode + '\'' + '}';
    }
}
