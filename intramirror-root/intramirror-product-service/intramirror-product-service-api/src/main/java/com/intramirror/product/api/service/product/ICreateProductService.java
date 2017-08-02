package com.intramirror.product.api.service.product;

import com.intramirror.common.help.ResultMessage;
import com.intramirror.product.api.vo.product.ProductOptions;

/**
 * Created by dingyifan on 2017/8/2.
 * 这个是创建商品相关的接口
 */
public interface ICreateProductService {

    /**
     * 调用此接口创建商品信息
     * @param productOptions
     * @return
     */
    ResultMessage CreateProduct(ProductOptions productOptions);
}
