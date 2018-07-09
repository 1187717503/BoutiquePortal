package com.intramirror.product.api.entity.promotion;

import java.util.List;

/**
 * Created on 2018/1/4.
 * @author Shang
 */
public class ImportDataEntity {
    String brandName;
    List<CategoryEntity> categorys;

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public List<CategoryEntity> getCategorys() {
        return categorys;
    }

    public void setCategorys(List<CategoryEntity> categorys) {
        this.categorys = categorys;
    }

    @Override
    public String toString() {
        return "ImportDataEntity{" +
                "brandName='" + brandName + '\'' +
                ", categorys=" + categorys +
                '}';
    }
}
