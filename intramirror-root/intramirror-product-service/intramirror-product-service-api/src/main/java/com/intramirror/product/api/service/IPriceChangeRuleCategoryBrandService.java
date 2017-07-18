package com.intramirror.product.api.service;

import com.intramirror.product.api.model.PriceChangeRuleCategoryBrand;

public interface IPriceChangeRuleCategoryBrandService {
	
	/**
	 * 添加
	 * @param priceChangeRuleCategoryBrand
	 * @return
	 */
	Long createPriceChangeRuleCategoryBrand(PriceChangeRuleCategoryBrand priceChangeRuleCategoryBrand);
	
	
	/**
	 * 删除
	 * @param priceChangeRuleCategoryBrandId
	 * @return
	 */
	int  deletePriceChangeRuleCategoryBrand(Long priceChangeRuleCategoryBrandId);
	
}
