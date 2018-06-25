package com.intramirror.web.common.request;

import com.intramirror.product.api.entity.promotion.BrandEntity;
import com.intramirror.product.api.entity.promotion.CategoryEntity;
import com.intramirror.product.api.entity.promotion.ImportDataEntity;
import java.util.List;

/**
 * Created on 2018/1/5.
 * @author 123
 */
public class PromotionRuleEntity {
    List<BrandEntity> brands;
    List<CategoryEntity> categorys;
    List<ImportDataEntity> importData;
    Long promotionId;
    Long vendorId;
    String seasonCode;
    Long ruleId;
    List<String> seasonCodes;

    public List<ImportDataEntity> getImportData() {
        return importData;
    }

    public void setImportData(List<ImportDataEntity> importData) {
        this.importData = importData;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

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

    public List<String> getSeasonCodes() {
        return seasonCodes;
    }

    public void setSeasonCodes(List<String> seasonCodes) {
        this.seasonCodes = seasonCodes;
    }

    @Override
    public String toString() {
        return "PromotionRuleEntity{" +
                "brands=" + brands +
                ", categorys=" + categorys +
                ", importData=" + importData +
                ", promotionId=" + promotionId +
                ", vendorId=" + vendorId +
                ", seasonCode='" + seasonCode + '\'' +
                ", ruleId=" + ruleId +
                '}';
    }
}
