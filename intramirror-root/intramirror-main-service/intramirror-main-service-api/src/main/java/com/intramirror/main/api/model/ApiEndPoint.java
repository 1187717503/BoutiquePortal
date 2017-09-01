package com.intramirror.main.api.model;

public class ApiEndPoint {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column api_end_point.api_end_point_id
     *
     * @mbggenerated
     */
    private Long apiEndPointId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column api_end_point.url
     *
     * @mbggenerated
     */
    private String url;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column api_end_point.name
     *
     * @mbggenerated
     */
    private String name;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column api_end_point.enabled
     *
     * @mbggenerated
     */
    private Boolean enabled;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column api_end_point.api_end_point_id
     *
     * @return the value of api_end_point.api_end_point_id
     *
     * @mbggenerated
     */
    public Long getApiEndPointId() {
        return apiEndPointId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column api_end_point.api_end_point_id
     *
     * @param apiEndPointId the value for api_end_point.api_end_point_id
     *
     * @mbggenerated
     */
    public void setApiEndPointId(Long apiEndPointId) {
        this.apiEndPointId = apiEndPointId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column api_end_point.url
     *
     * @return the value of api_end_point.url
     *
     * @mbggenerated
     */
    public String getUrl() {
        return url;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column api_end_point.url
     *
     * @param url the value for api_end_point.url
     *
     * @mbggenerated
     */
    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column api_end_point.name
     *
     * @return the value of api_end_point.name
     *
     * @mbggenerated
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column api_end_point.name
     *
     * @param name the value for api_end_point.name
     *
     * @mbggenerated
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column api_end_point.enabled
     *
     * @return the value of api_end_point.enabled
     *
     * @mbggenerated
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column api_end_point.enabled
     *
     * @param enabled the value for api_end_point.enabled
     *
     * @mbggenerated
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}