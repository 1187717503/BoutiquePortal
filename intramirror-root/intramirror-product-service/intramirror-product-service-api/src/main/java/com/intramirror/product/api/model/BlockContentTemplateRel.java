package com.intramirror.product.api.model;

import java.util.Date;

public class BlockContentTemplateRel {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column block_content_template_rel.block_content_template_rel_id
     *
     * @mbggenerated
     */
    private Long blockContentTemplateRelId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column block_content_template_rel.block_id
     *
     * @mbggenerated
     */
    private Long blockId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column block_content_template_rel.content_template_id
     *
     * @mbggenerated
     */
    private Long contentTemplateId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column block_content_template_rel.sort_num
     *
     * @mbggenerated
     */
    private Integer sortNum;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column block_content_template_rel.created_at
     *
     * @mbggenerated
     */
    private Date createdAt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column block_content_template_rel.enabled
     *
     * @mbggenerated
     */
    private Boolean enabled;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column block_content_template_rel.params
     *
     * @mbggenerated
     */
    private String params;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column block_content_template_rel.block_content_template_rel_id
     *
     * @return the value of block_content_template_rel.block_content_template_rel_id
     *
     * @mbggenerated
     */
    public Long getBlockContentTemplateRelId() {
        return blockContentTemplateRelId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column block_content_template_rel.block_content_template_rel_id
     *
     * @param blockContentTemplateRelId the value for block_content_template_rel.block_content_template_rel_id
     *
     * @mbggenerated
     */
    public void setBlockContentTemplateRelId(Long blockContentTemplateRelId) {
        this.blockContentTemplateRelId = blockContentTemplateRelId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column block_content_template_rel.block_id
     *
     * @return the value of block_content_template_rel.block_id
     *
     * @mbggenerated
     */
    public Long getBlockId() {
        return blockId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column block_content_template_rel.block_id
     *
     * @param blockId the value for block_content_template_rel.block_id
     *
     * @mbggenerated
     */
    public void setBlockId(Long blockId) {
        this.blockId = blockId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column block_content_template_rel.content_template_id
     *
     * @return the value of block_content_template_rel.content_template_id
     *
     * @mbggenerated
     */
    public Long getContentTemplateId() {
        return contentTemplateId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column block_content_template_rel.content_template_id
     *
     * @param contentTemplateId the value for block_content_template_rel.content_template_id
     *
     * @mbggenerated
     */
    public void setContentTemplateId(Long contentTemplateId) {
        this.contentTemplateId = contentTemplateId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column block_content_template_rel.sort_num
     *
     * @return the value of block_content_template_rel.sort_num
     *
     * @mbggenerated
     */
    public Integer getSortNum() {
        return sortNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column block_content_template_rel.sort_num
     *
     * @param sortNum the value for block_content_template_rel.sort_num
     *
     * @mbggenerated
     */
    public void setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column block_content_template_rel.created_at
     *
     * @return the value of block_content_template_rel.created_at
     *
     * @mbggenerated
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column block_content_template_rel.created_at
     *
     * @param createdAt the value for block_content_template_rel.created_at
     *
     * @mbggenerated
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column block_content_template_rel.enabled
     *
     * @return the value of block_content_template_rel.enabled
     *
     * @mbggenerated
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column block_content_template_rel.enabled
     *
     * @param enabled the value for block_content_template_rel.enabled
     *
     * @mbggenerated
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column block_content_template_rel.params
     *
     * @return the value of block_content_template_rel.params
     *
     * @mbggenerated
     */
    public String getParams() {
        return params;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column block_content_template_rel.params
     *
     * @param params the value for block_content_template_rel.params
     *
     * @mbggenerated
     */
    public void setParams(String params) {
        this.params = params == null ? null : params.trim();
    }
}