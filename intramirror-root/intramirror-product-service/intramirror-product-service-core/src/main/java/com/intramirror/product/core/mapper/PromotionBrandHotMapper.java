package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.PromotionBrandHot;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PromotionBrandHotMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_promotion_brand_hot
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long promotionBrandHotId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_promotion_brand_hot
     * @mbggenerated
     */
    int insert(PromotionBrandHot record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_promotion_brand_hot
     * @mbggenerated
     */
    int insertSelective(PromotionBrandHot record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_promotion_brand_hot
     * @mbggenerated
     */
    PromotionBrandHot selectByPrimaryKey(Long promotionBrandHotId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_promotion_brand_hot
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(PromotionBrandHot record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_promotion_brand_hot
     * @mbggenerated
     */
    int updateByPrimaryKey(PromotionBrandHot record);

    List<PromotionBrandHot> getPromotionBrandHot(Long promotionId);

    int deleteAll(Long promotionId);

    int insertList(List<PromotionBrandHot> listPromotionBrandHot);

}