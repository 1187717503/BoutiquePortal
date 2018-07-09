package com.intramirror.product.core.impl.season;

import com.intramirror.product.api.service.season.SeasonService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.SeasonMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created on 2017/10/25.
 *
 * @author YouFeng.Zhu
 */
@Service
public class SeasonServiceImpl extends BaseDao implements SeasonService {

    private final static Logger LOGGER = LoggerFactory.getLogger(SeasonServiceImpl.class);

    private SeasonMapper seasonMapper;

    @Override
    public void init() {
        seasonMapper = this.getSqlSession().getMapper(SeasonMapper.class);
    }

    @Override
    public List<String> listAllSeasonCode() {
        return seasonMapper.listAllSeasonCode();
    }
}
