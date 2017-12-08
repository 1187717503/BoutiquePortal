package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.Tag;
import java.util.List;

public interface TagMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tag
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long tagId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tag
     * @mbggenerated
     */
    int insert(Tag record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tag
     * @mbggenerated
     */
    int insertSelective(Tag record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tag
     * @mbggenerated
     */
    Tag selectByPrimaryKey(Long tagId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tag
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(Tag record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tag
     * @mbggenerated
     */
    int updateByPrimaryKey(Tag record);

    List<Tag> getTags();

    List<Tag> getTagsByDate();
}