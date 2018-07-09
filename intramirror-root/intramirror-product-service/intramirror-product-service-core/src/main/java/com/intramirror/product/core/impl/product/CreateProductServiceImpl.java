package com.intramirror.product.core.impl.product;

import com.google.gson.Gson;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.product.api.service.IProductService;
import com.intramirror.product.api.service.product.ICreateProductService;
import com.intramirror.product.api.vo.product.ProductOptions;
import com.intramirror.product.api.vo.sku.SkuOptions;
import com.intramirror.product.api.vo.vendor.VendorOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by dingyifan on 2017/8/2.
 */
public class CreateProductServiceImpl implements ICreateProductService{

	private static Logger logger = LoggerFactory.getLogger(CreateProductServiceImpl.class);

	@Autowired
	private IProductService iProductService;

	@Override
	public ResultMessage CreateProduct(ProductOptions productOptions) {
		ResultMessage resultMessage = ResultMessage.getInstance();
		logger.info(" start createProduct ; productOptions : {}", productOptions);

		// check basics param
		ResultMessage checkMessage = productOptions.checkBasicsParams();
		if(checkMessage.isERROR())
			return checkMessage;

		List<SkuOptions> skus = productOptions.getSkus();
		VendorOptions vendorOptions = productOptions.getVendorOptions();


		logger.info(" end create Product ;resultMessage : {}",new Gson().toJson(resultMessage));
		resultMessage.setData(productOptions);
		return resultMessage;
	}

}
