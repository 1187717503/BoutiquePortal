package com.intramirror.product.api.service;

import com.intramirror.product.api.model.SnapshotPriceDetail;
import com.intramirror.product.api.model.SnapshotPriceRule;

import java.util.List;
import java.util.Map;

/**
 * Created by 123 on 2018/6/1.
 */
public interface ISnapshotPriceRuleService {
    int insertSelective(SnapshotPriceRule record);
    int updateSaveAtByPriceChangeRuleId(SnapshotPriceRule record);
    int updateByPrimaryKeySelective(SnapshotPriceRule record);
    int deleteByPriceChangeRuleId(SnapshotPriceRule record);
    int deleteDetailsBySnapshotPriceRuleId(SnapshotPriceDetail record);
    List<SnapshotPriceRule> getSnapshotPriceRuleByPriceChangeRuleIds(Map<String, Object> params);
}
