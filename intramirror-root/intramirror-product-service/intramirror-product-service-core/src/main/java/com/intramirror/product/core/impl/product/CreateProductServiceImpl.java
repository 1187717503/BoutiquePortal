package com.intramirror.product.core.impl.product;

import com.google.gson.Gson;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.product.api.service.product.ICreateProductService;
import com.intramirror.product.api.vo.product.ProductOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by dingyifan on 2017/8/2.
 */
public class CreateProductServiceImpl implements ICreateProductService{

	private static Logger logger = LoggerFactory.getLogger(CreateProductServiceImpl.class);

	@Override
	public ResultMessage CreateProduct(ProductOptions productOptions) {
		ResultMessage resultMessage = ResultMessage.getInstance();
		logger.info(" start createProduct ; productOptions : {}", productOptions);

		logger.info(" end create Product ;resultMessage : {}",new Gson().toJson(resultMessage));
		resultMessage.setData(productOptions);
		return resultMessage;
	}
}
