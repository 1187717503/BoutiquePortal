package com.intramirror.product.api.model;

public class CategoryWithBLOBs extends Category {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column category.remark
     *
     * @mbggenerated
     */
    private String remark;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column category.cover_img
     *
     * @mbggenerated
     */
    private String coverImg;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column category.remark
     *
     * @return the value of category.remark
     *
     * @mbggenerated
     */
    public String getRemark() {
        return remark;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column category.remark
     *
     * @param remark the value for category.remark
     *
     * @mbggenerated
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column category.cover_img
     *
     * @return the value of category.cover_img
     *
     * @mbggenerated
     */
    public String getCoverImg() {
        return coverImg;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column category.cover_img
     *
     * @param coverImg the value for category.cover_img
     *
     * @mbggenerated
     */
    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg == null ? null : coverImg.trim();
    }
}