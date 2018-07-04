package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.BoutiqueException;
import java.math.BigDecimal;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface BoutiqueExceptionMapper {

    int deleteByPrimaryKey(Long id);

    int insert(BoutiqueException record);

    int insertSelective(BoutiqueException record);

    BoutiqueException selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BoutiqueException record);

    int updateByPrimaryKey(BoutiqueException record);

    Map<String, Object> selectBoutiqueExceptionByProductIdAndType(@Param("productId") Long productId, @Param("type") Integer type);

    int updateSeasonByProductId(@Param("productId") Long productId, @Param("seasonCode") String seasonCode);

    int updatePriceByProductId(@Param("productId") Long productId, @Param("retailPrice") BigDecimal retailPrice);

    int deleteBoutiqueExceptionByProductIdAndType(@Param("productId") Long productId, @Param("type") Integer type);

    int countBoutiqueExceptionByProductId(Long productId);
}