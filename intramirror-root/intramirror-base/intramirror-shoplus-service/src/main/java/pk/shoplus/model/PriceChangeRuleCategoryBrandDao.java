package pk.shoplus.model;

import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

/**
 * price_change_rule_category_brand 实体类
 * Thu Dec 01 18:07:18 CST 2016
 */
public class PriceChangeRuleCategoryBrandDao {
    /**
     * 1:price_change_rule_category_brand_id
     */
    public Long price_change_rule_category_brand_id;
    /**
     * 2:price_change_rule_id
     */
    public Long price_change_rule_id;
    /**
     * 3:category_id
     */
    public Long category_id;

    /**
     * 4:brand_id
     */
    public Long brand_id;

    /**
     * 5:discount_percentage
     */
    public Long discount_percentage;


    /**
     * 6:exception_flag
     */
    public Integer exception_flag;


    /**
     * 7:level
     */
    public Integer level;

    public String brandName;

    public String categoryNameOne;

    public String categoryNameTwo;

    public String categoryNameThree;
    
    public Long categoryIdOne;

    public Long categoryIdTwo;

    public Long categoryIdThree;

    public String categoryAllBrandNameOne;

    public String categoryAllBrandNameTwo;
    

    public Long getPrice_change_rule_category_brand_id() {
        return price_change_rule_category_brand_id;
    }

    public Long getPrice_change_rule_id() {
        return price_change_rule_id;
    }

    public Long getCategory_id() {
        return category_id;
    }

    public Long getBrand_id() {
        return brand_id;
    }

    public Long getDiscount_percentage() {
        return discount_percentage;
    }

    public Integer getException_flag() {
        return exception_flag;
    }

    public Integer getLevel() {
        return level;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getCategoryNameOne() {
        return categoryNameOne;
    }

    public String getCategoryNameTwo() {
        return categoryNameTwo;
    }

    public String getCategoryNameThree() {
        return categoryNameThree;
    }

    public String getCategoryAllBrandNameOne() {
        return categoryAllBrandNameOne;
    }

    public String getCategoryAllBrandNameTwo() {
        return categoryAllBrandNameTwo;
    }

	public Long getCategoryIdOne() {
		return categoryIdOne;
	}

	public void setCategoryIdOne(Long categoryIdOne) {
		this.categoryIdOne = categoryIdOne;
	}

	public Long getCategoryIdTwo() {
		return categoryIdTwo;
	}

	public void setCategoryIdTwo(Long categoryIdTwo) {
		this.categoryIdTwo = categoryIdTwo;
	}

	public Long getCategoryIdThree() {
		return categoryIdThree;
	}

	public void setCategoryIdThree(Long categoryIdThree) {
		this.categoryIdThree = categoryIdThree;
	}

	public void setPrice_change_rule_category_brand_id(Long price_change_rule_category_brand_id) {
		this.price_change_rule_category_brand_id = price_change_rule_category_brand_id;
	}

	public void setPrice_change_rule_id(Long price_change_rule_id) {
		this.price_change_rule_id = price_change_rule_id;
	}

	public void setCategory_id(Long category_id) {
		this.category_id = category_id;
	}

	public void setBrand_id(Long brand_id) {
		this.brand_id = brand_id;
	}

	public void setDiscount_percentage(Long discount_percentage) {
		this.discount_percentage = discount_percentage;
	}

	public void setException_flag(Integer exception_flag) {
		this.exception_flag = exception_flag;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public void setCategoryNameOne(String categoryNameOne) {
		this.categoryNameOne = categoryNameOne;
	}

	public void setCategoryNameTwo(String categoryNameTwo) {
		this.categoryNameTwo = categoryNameTwo;
	}

	public void setCategoryNameThree(String categoryNameThree) {
		this.categoryNameThree = categoryNameThree;
	}

	public void setCategoryAllBrandNameOne(String categoryAllBrandNameOne) {
		this.categoryAllBrandNameOne = categoryAllBrandNameOne;
	}

	public void setCategoryAllBrandNameTwo(String categoryAllBrandNameTwo) {
		this.categoryAllBrandNameTwo = categoryAllBrandNameTwo;
	}
    
}