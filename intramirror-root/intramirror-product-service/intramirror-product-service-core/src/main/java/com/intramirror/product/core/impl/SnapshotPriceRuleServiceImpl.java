package com.intramirror.product.core.impl;

import com.intramirror.product.api.model.SnapshotPriceDetail;
import com.intramirror.product.api.model.SnapshotPriceRule;
import com.intramirror.product.api.service.ISnapshotPriceRuleService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.SnapshotPriceDetailMapper;
import com.intramirror.product.core.mapper.SnapshotPriceRuleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by 123 on 2018/6/1.
 */
@Service
public class SnapshotPriceRuleServiceImpl extends BaseDao implements ISnapshotPriceRuleService {

    private static Logger logger = LoggerFactory.getLogger(SnapshotPriceRuleServiceImpl.class);

    private SnapshotPriceRuleMapper snapshotPriceRuleMapper;
    private SnapshotPriceDetailMapper snapshotPriceDetailMapper;

    public void init() {
        snapshotPriceRuleMapper = this.getSqlSession().getMapper(SnapshotPriceRuleMapper.class);
        snapshotPriceDetailMapper = this.getSqlSession().getMapper(SnapshotPriceDetailMapper.class);
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

    @Override
    public int deleteDetailsBySnapshotPriceRuleId(SnapshotPriceDetail record) {
        return snapshotPriceDetailMapper.deleteBySnapshotPriceRuleId(record);
    }

    @Override
    public List<SnapshotPriceRule> getSnapshotPriceRuleByPriceChangeRuleIds(Map<String, Object> params) {
        return snapshotPriceRuleMapper.getSnapshotPriceRuleByPriceChangeRuleIds(params);
    }
}
