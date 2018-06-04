package com.intramirror.product.core.impl;

import com.intramirror.product.api.model.SnapshotPriceRule;
import com.intramirror.product.api.service.ISnapshotPriceRuleService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.SnapshotPriceRuleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by 123 on 2018/6/1.
 */
@Service
public class SnapshotPriceRuleServiceImpl extends BaseDao implements ISnapshotPriceRuleService {

    private static Logger logger = LoggerFactory.getLogger(SnapshotPriceRuleServiceImpl.class);

    private SnapshotPriceRuleMapper snapshotPriceRuleMapper;

    public void init() {
        snapshotPriceRuleMapper = this.getSqlSession().getMapper(SnapshotPriceRuleMapper.class);
    }

    @Override
    public int insertSelective(SnapshotPriceRule record) {
        return snapshotPriceRuleMapper.insertSelective(record);
    }

    @Override
    public int updateSaveAtByPriceChangeRuleId(SnapshotPriceRule record) {
        return snapshotPriceRuleMapper.updateSaveAtByPriceChangeRuleId(record);
    }

    @Override
    public int updateByPrimaryKeySelective(SnapshotPriceRule record) {
        return snapshotPriceRuleMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int deleteByPriceChangeRuleId(SnapshotPriceRule record) {
        return snapshotPriceRuleMapper.deleteByPriceChangeRuleId(record);
    }

}
