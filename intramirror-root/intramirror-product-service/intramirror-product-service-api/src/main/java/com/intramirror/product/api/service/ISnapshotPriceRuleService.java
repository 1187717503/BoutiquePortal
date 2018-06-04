package com.intramirror.product.api.service;

import com.intramirror.product.api.model.SnapshotPriceRule;

/**
 * Created by 123 on 2018/6/1.
 */
public interface ISnapshotPriceRuleService {
    int insertSelective(SnapshotPriceRule record);
    int updateSaveAtByPriceChangeRuleId(SnapshotPriceRule record);
    int updateByPrimaryKeySelective(SnapshotPriceRule record);
    int deleteByPriceChangeRuleId(SnapshotPriceRule record);
}
