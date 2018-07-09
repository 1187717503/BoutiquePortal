package com.intramirror.web.mapping.api;

import com.intramirror.web.mapping.vo.StockOption;

import java.util.Map;

/**
 * Created by dingyifan on 2017/9/14.
 */
public interface IStockMapping {
    StockOption mapping(Map<String,Object> bodyDataMap);
}
