package com.intramirror.product.api.model;

import java.util.Date;

public class Block {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column block.block_id
     *
     * @mbggenerated
     */
    private Long blockId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column block.block_name
     *
     * @mbggenerated
     */
    private String blockName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column block.status
     *
     * @mbggenerated
     */
    private Byte status;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column block.title
     *
     * @mbggenerated
     */
    private String title;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column block.create_at
     *
     * @mbggenerated
     */
    private Date createAt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column block.modified_at
     *
     * @mbggenerated
     */
    private Date modifiedAt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column block.bg_color
     *
     * @mbggenerated
     */
    private String bgColor;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column block.sort_order
     *
     * @mbggenerated
     */
    private Integer sortOrder;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column block.subtitle
     *
     * @mbggenerated
     */
    private String subtitle;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column block.title_english
     *
     * @mbggenerated
     */
    private String titleEnglish;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column block.enabled
     *
     * @mbggenerated
     */
    private Boolean enabled;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column block.content
     *
     * @mbggenerated
     */
    private byte[] content;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column block.block_id
     *
     * @return the value of block.block_id
     *
     * @mbggenerated
     */
    public Long getBlockId() {
        return blockId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column block.block_id
     *
     * @param blockId the value for block.block_id
     *
     * @mbggenerated
     */
    public void setBlockId(Long blockId) {
        this.blockId = blockId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column block.block_name
     *
     * @return the value of block.block_name
     *
     * @mbggenerated
     */
    public String getBlockName() {
        return blockName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column block.block_name
     *
     * @param blockName the value for block.block_name
     *
     * @mbggenerated
     */
    public void setBlockName(String blockName) {
        this.blockName = blockName == null ? null : blockName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column block.status
     *
     * @return the value of block.status
     *
     * @mbggenerated
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column block.status
     *
     * @param status the value for block.status
     *
     * @mbggenerated
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column block.title
     *
     * @return the value of block.title
     *
     * @mbggenerated
     */
    public String getTitle() {
        return title;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column block.title
     *
     * @param title the value for block.title
     *
     * @mbggenerated
     */
    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column block.create_at
     *
     * @return the value of block.create_at
     *
     * @mbggenerated
     */
    public Date getCreateAt() {
        return createAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column block.create_at
     *
     * @param createAt the value for block.create_at
     *
     * @mbggenerated
     */
    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column block.modified_at
     *
     * @return the value of block.modified_at
     *
     * @mbggenerated
     */
    public Date getModifiedAt() {
        return modifiedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column block.modified_at
     *
     * @param modifiedAt the value for block.modified_at
     *
     * @mbggenerated
     */
    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column block.bg_color
     *
     * @return the value of block.bg_color
     *
     * @mbggenerated
     */
    public String getBgColor() {
        return bgColor;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column block.bg_color
     *
     * @param bgColor the value for block.bg_color
     *
     * @mbggenerated
     */
    public void setBgColor(String bgColor) {
        this.bgColor = bgColor == null ? null : bgColor.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column block.sort_order
     *
     * @return the value of block.sort_order
     *
     * @mbggenerated
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column block.sort_order
     *
     * @param sortOrder the value for block.sort_order
     *
     * @mbggenerated
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column block.subtitle
     *
     * @return the value of block.subtitle
     *
     * @mbggenerated
     */
    public String getSubtitle() {
        return subtitle;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column block.subtitle
     *
     * @param subtitle the value for block.subtitle
     *
     * @mbggenerated
     */
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle == null ? null : subtitle.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column block.title_english
     *
     * @return the value of block.title_english
     *
     * @mbggenerated
     */
    public String getTitleEnglish() {
        return titleEnglish;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column block.title_english
     *
     * @param titleEnglish the value for block.title_english
     *
     * @mbggenerated
     */
    public void setTitleEnglish(String titleEnglish) {
        this.titleEnglish = titleEnglish == null ? null : titleEnglish.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column block.enabled
     *
     * @return the value of block.enabled
     *
     * @mbggenerated
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column block.enabled
     *
     * @param enabled the value for block.enabled
     *
     * @mbggenerated
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column block.content
     *
     * @return the value of block.content
     *
     * @mbggenerated
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column block.content
     *
     * @param content the value for block.content
     *
     * @mbggenerated
     */
    public void setContent(byte[] content) {
        this.content = content;
    }
}