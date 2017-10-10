package com.intramirror.product.api.service.stock;

import com.intramirror.common.help.ResultMessage;

/**
 * Created by dingyifan on 2017/8/2.
 */
public interface IUpdateStockService {

    ResultMessage zeroClearing(Long vendorId);
}
