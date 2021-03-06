package com.intramirror.product.api.model;

public class ApiErrorType {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column api_error_type.api_error_type_id
     *
     * @mbggenerated
     */
    private Long apiErrorTypeId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column api_error_type.error_type_name
     *
     * @mbggenerated
     */
    private String errorTypeName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column api_error_type.error_type_desc
     *
     * @mbggenerated
     */
    private String errorTypeDesc;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column api_error_type.enabled
     *
     * @mbggenerated
     */
    private Boolean enabled;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column api_error_type.api_error_type_id
     *
     * @return the value of api_error_type.api_error_type_id
     *
     * @mbggenerated
     */
    public Long getApiErrorTypeId() {
        return apiErrorTypeId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column api_error_type.api_error_type_id
     *
     * @param apiErrorTypeId the value for api_error_type.api_error_type_id
     *
     * @mbggenerated
     */
    public void setApiErrorTypeId(Long apiErrorTypeId) {
        this.apiErrorTypeId = apiErrorTypeId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column api_error_type.error_type_name
     *
     * @return the value of api_error_type.error_type_name
     *
     * @mbggenerated
     */
    public String getErrorTypeName() {
        return errorTypeName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column api_error_type.error_type_name
     *
     * @param errorTypeName the value for api_error_type.error_type_name
     *
     * @mbggenerated
     */
    public void setErrorTypeName(String errorTypeName) {
        this.errorTypeName = errorTypeName == null ? null : errorTypeName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column api_error_type.error_type_desc
     *
     * @return the value of api_error_type.error_type_desc
     *
     * @mbggenerated
     */
    public String getErrorTypeDesc() {
        return errorTypeDesc;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column api_error_type.error_type_desc
     *
     * @param errorTypeDesc the value for api_error_type.error_type_desc
     *
     * @mbggenerated
     */
    public void setErrorTypeDesc(String errorTypeDesc) {
        this.errorTypeDesc = errorTypeDesc == null ? null : errorTypeDesc.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column api_error_type.enabled
     *
     * @return the value of api_error_type.enabled
     *
     * @mbggenerated
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column api_error_type.enabled
     *
     * @param enabled the value for api_error_type.enabled
     *
     * @mbggenerated
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}