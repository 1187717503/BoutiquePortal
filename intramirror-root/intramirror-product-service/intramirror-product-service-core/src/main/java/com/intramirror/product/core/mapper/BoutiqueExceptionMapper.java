package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.BoutiqueException;
import java.math.BigDecimal;
import java.util.Map;

public interface BoutiqueExceptionMapper {

    int deleteByPrimaryKey(Long id);

    int insert(BoutiqueException record);

    int insertSelective(BoutiqueException record);

    BoutiqueException selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BoutiqueException record);

    int updateByPrimaryKey(BoutiqueException record);

    Map<String, Object> selectSeasonChange(Long productId);

    Map<String, Object> selectPriceChange(Long productId);

    int updateSeasonByProductId(Long productId, String seasonCode);

    int updatePriceByProductId(Long productId, BigDecimal retailPrice);
}