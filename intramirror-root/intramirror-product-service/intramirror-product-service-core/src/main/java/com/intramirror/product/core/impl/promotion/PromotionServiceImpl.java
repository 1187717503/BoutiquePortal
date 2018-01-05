package com.intramirror.product.core.impl.promotion;

import com.intramirror.product.api.model.PromotionIncludeRule;
import com.intramirror.product.api.service.promotion.IPromotionService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.PromotionIncludeRuleMapper;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created on 2018/1/4.
 * @author 123
 */
@Service(value = "promotionService")

public class PromotionServiceImpl extends BaseDao implements IPromotionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PromotionServiceImpl.class);

    private PromotionIncludeRuleMapper promotionIncludeRuleMapper;

    @Override
    public List<Map<String, Object>> listActivePromotion() {
        return promotionIncludeRuleMapper.listActivePromotion();
    }

    @Override

    public List<Map<String, Object>> listExcludeRulePromotion(String bannerId) {
        return promotionIncludeRuleMapper.listExcludeRulePromotion(bannerId);
    }

    @Override
    public List<Map<String, Object>> listIncluedRulePromotion(String bannerId) {
        return promotionIncludeRuleMapper.listIncluedRulePromotion(bannerId);
    }

    @Override
    public void init() {
        promotionIncludeRuleMapper = this.getSqlSession().getMapper(PromotionIncludeRuleMapper.class);
    }

    public Long savePromotionIncludeRule(PromotionIncludeRule rule) {
        LOGGER.info("Start to save promotion include rule.");
        promotionIncludeRuleMapper.insertIncludeRule(rule);
        return rule.getPromotionIncludeRuleId();

    }
}
