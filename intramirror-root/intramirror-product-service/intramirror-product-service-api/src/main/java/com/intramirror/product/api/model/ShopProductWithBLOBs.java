package com.intramirror.product.api.model;

public class ShopProductWithBLOBs extends ShopProduct {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shop_product.coverpic
     *
     * @mbggenerated
     */
    private String coverpic;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shop_product.introduction
     *
     * @mbggenerated
     */
    private String introduction;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shop_product.coverpic
     *
     * @return the value of shop_product.coverpic
     *
     * @mbggenerated
     */
    public String getCoverpic() {
        return coverpic;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shop_product.coverpic
     *
     * @param coverpic the value for shop_product.coverpic
     *
     * @mbggenerated
     */
    public void setCoverpic(String coverpic) {
        this.coverpic = coverpic == null ? null : coverpic.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shop_product.introduction
     *
     * @return the value of shop_product.introduction
     *
     * @mbggenerated
     */
    public String getIntroduction() {
        return introduction;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shop_product.introduction
     *
     * @param introduction the value for shop_product.introduction
     *
     * @mbggenerated
     */
    public void setIntroduction(String introduction) {
        this.introduction = introduction == null ? null : introduction.trim();
    }
}