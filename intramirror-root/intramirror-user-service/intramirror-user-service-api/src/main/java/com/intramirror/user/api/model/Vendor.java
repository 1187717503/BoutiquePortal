package com.intramirror.user.api.model;

import java.math.BigDecimal;
import java.util.Date;

public class Vendor {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor.vendor_id
     * @mbggenerated
     */
    private Long vendorId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor.grade
     * @mbggenerated
     */
    private Byte grade;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor.vendor_name
     * @mbggenerated
     */
    private String vendorName;

    private String vendorCode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor.user_id
     * @mbggenerated
     */
    private Long userId;


    private Long parentId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor.vendor_applicant_id
     * @mbggenerated
     */
    private Long vendorApplicantId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor.identity_card
     * @mbggenerated
     */
    private String identityCard;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor.brand_type
     * @mbggenerated
     */
    private Byte brandType;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor.credit_card
     * @mbggenerated
     */
    private String creditCard;

    private String city;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor.status
     * @mbggenerated
     */
    private Byte status;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor.company_name
     * @mbggenerated
     */
    private String companyName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor.registered_person
     * @mbggenerated
     */
    private String registeredPerson;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor.electronic_identification_card
     * @mbggenerated
     */
    private String electronicIdentificationCard;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor.business_license_number
     * @mbggenerated
     */
    private String businessLicenseNumber;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor.business_license_duration
     * @mbggenerated
     */
    private Date businessLicenseDuration;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor.valid_end_at
     * @mbggenerated
     */
    private Date validEndAt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor.electronic_business_license
     * @mbggenerated
     */
    private String electronicBusinessLicense;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor.legal_management_scope
     * @mbggenerated
     */
    private String legalManagementScope;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor.business_license_location
     * @mbggenerated
     */
    private String businessLicenseLocation;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor.updated_at
     * @mbggenerated
     */
    private Date updatedAt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor.created_at
     * @mbggenerated
     */
    private Date createdAt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor.enabled
     * @mbggenerated
     */
    private Boolean enabled;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor.last_login
     * @mbggenerated
     */
    private Date lastLogin;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor.national_mark
     * @mbggenerated
     */
    private String nationalMark;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor.product_image_score
     * @mbggenerated
     */
    private Byte productImageScore;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor.vendor_score
     * @mbggenerated
     */
    private Byte vendorScore;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor.score
     * @mbggenerated
     */
    private BigDecimal score;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor.address_country_id
     * @mbggenerated
     */
    private Long addressCountryId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor.remark
     * @mbggenerated
     */
    private String remark;

    /**
     * vendor.skip_confirm
     */
    private Integer skipConfirm;

    private String erpSystem;

    private String contactPhone;

    private String postalCode;

    private String contactEmail;
    private String areaCode;

    private String contact;

    private String eddTitle;

    private String eddDesc;

    public Integer getSkipConfirm() {
        return skipConfirm;
    }

    public void setSkipConfirm(Integer skipConfirm) {
        this.skipConfirm = skipConfirm;
    }

    public String getEddTitle() {
        return eddTitle;
    }

    public void setEddTitle(String eddTitle) {
        this.eddTitle = eddTitle;
    }

    public String getEddDesc() {
        return eddDesc;
    }

    public void setEddDesc(String eddDesc) {
        this.eddDesc = eddDesc;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor.vendor_id
     * @return the value of vendor.vendor_id
     * @mbggenerated
     */
    public Long getVendorId() {
        return vendorId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor.vendor_id
     * @param vendorId
     *         the value for vendor.vendor_id
     * @mbggenerated
     */
    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor.grade
     * @return the value of vendor.grade
     * @mbggenerated
     */
    public Byte getGrade() {
        return grade;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor.grade
     * @param grade
     *         the value for vendor.grade
     * @mbggenerated
     */
    public void setGrade(Byte grade) {
        this.grade = grade;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor.vendor_name
     * @return the value of vendor.vendor_name
     * @mbggenerated
     */
    public String getVendorName() {
        return vendorName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor.vendor_name
     * @param vendorName
     *         the value for vendor.vendor_name
     * @mbggenerated
     */
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName == null ? null : vendorName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor.user_id
     * @return the value of vendor.user_id
     * @mbggenerated
     */
    public Long getUserId() {
        return userId;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor.user_id
     * @param userId
     *         the value for vendor.user_id
     * @mbggenerated
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor.vendor_applicant_id
     * @return the value of vendor.vendor_applicant_id
     * @mbggenerated
     */
    public Long getVendorApplicantId() {
        return vendorApplicantId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor.vendor_applicant_id
     * @param vendorApplicantId
     *         the value for vendor.vendor_applicant_id
     * @mbggenerated
     */
    public void setVendorApplicantId(Long vendorApplicantId) {
        this.vendorApplicantId = vendorApplicantId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor.identity_card
     * @return the value of vendor.identity_card
     * @mbggenerated
     */
    public String getIdentityCard() {
        return identityCard;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor.identity_card
     * @param identityCard
     *         the value for vendor.identity_card
     * @mbggenerated
     */
    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard == null ? null : identityCard.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor.brand_type
     * @return the value of vendor.brand_type
     * @mbggenerated
     */
    public Byte getBrandType() {
        return brandType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor.brand_type
     * @param brandType
     *         the value for vendor.brand_type
     * @mbggenerated
     */
    public void setBrandType(Byte brandType) {
        this.brandType = brandType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor.credit_card
     * @return the value of vendor.credit_card
     * @mbggenerated
     */
    public String getCreditCard() {
        return creditCard;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor.credit_card
     * @param creditCard
     *         the value for vendor.credit_card
     * @mbggenerated
     */
    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard == null ? null : creditCard.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor.status
     * @return the value of vendor.status
     * @mbggenerated
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor.status
     * @param status
     *         the value for vendor.status
     * @mbggenerated
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor.company_name
     * @return the value of vendor.company_name
     * @mbggenerated
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor.company_name
     * @param companyName
     *         the value for vendor.company_name
     * @mbggenerated
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName == null ? null : companyName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor.registered_person
     * @return the value of vendor.registered_person
     * @mbggenerated
     */
    public String getRegisteredPerson() {
        return registeredPerson;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor.registered_person
     * @param registeredPerson
     *         the value for vendor.registered_person
     * @mbggenerated
     */
    public void setRegisteredPerson(String registeredPerson) {
        this.registeredPerson = registeredPerson == null ? null : registeredPerson.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor.electronic_identification_card
     * @return the value of vendor.electronic_identification_card
     * @mbggenerated
     */
    public String getElectronicIdentificationCard() {
        return electronicIdentificationCard;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor.electronic_identification_card
     * @param electronicIdentificationCard
     *         the value for vendor.electronic_identification_card
     * @mbggenerated
     */
    public void setElectronicIdentificationCard(String electronicIdentificationCard) {
        this.electronicIdentificationCard = electronicIdentificationCard == null ? null : electronicIdentificationCard.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor.business_license_number
     * @return the value of vendor.business_license_number
     * @mbggenerated
     */
    public String getBusinessLicenseNumber() {
        return businessLicenseNumber;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor.business_license_number
     * @param businessLicenseNumber
     *         the value for vendor.business_license_number
     * @mbggenerated
     */
    public void setBusinessLicenseNumber(String businessLicenseNumber) {
        this.businessLicenseNumber = businessLicenseNumber == null ? null : businessLicenseNumber.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor.business_license_duration
     * @return the value of vendor.business_license_duration
     * @mbggenerated
     */
    public Date getBusinessLicenseDuration() {
        return businessLicenseDuration;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor.business_license_duration
     * @param businessLicenseDuration
     *         the value for vendor.business_license_duration
     * @mbggenerated
     */
    public void setBusinessLicenseDuration(Date businessLicenseDuration) {
        this.businessLicenseDuration = businessLicenseDuration;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor.valid_end_at
     * @return the value of vendor.valid_end_at
     * @mbggenerated
     */
    public Date getValidEndAt() {
        return validEndAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor.valid_end_at
     * @param validEndAt
     *         the value for vendor.valid_end_at
     * @mbggenerated
     */
    public void setValidEndAt(Date validEndAt) {
        this.validEndAt = validEndAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor.electronic_business_license
     * @return the value of vendor.electronic_business_license
     * @mbggenerated
     */
    public String getElectronicBusinessLicense() {
        return electronicBusinessLicense;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor.electronic_business_license
     * @param electronicBusinessLicense
     *         the value for vendor.electronic_business_license
     * @mbggenerated
     */
    public void setElectronicBusinessLicense(String electronicBusinessLicense) {
        this.electronicBusinessLicense = electronicBusinessLicense == null ? null : electronicBusinessLicense.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor.legal_management_scope
     * @return the value of vendor.legal_management_scope
     * @mbggenerated
     */
    public String getLegalManagementScope() {
        return legalManagementScope;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor.legal_management_scope
     * @param legalManagementScope
     *         the value for vendor.legal_management_scope
     * @mbggenerated
     */
    public void setLegalManagementScope(String legalManagementScope) {
        this.legalManagementScope = legalManagementScope == null ? null : legalManagementScope.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor.business_license_location
     * @return the value of vendor.business_license_location
     * @mbggenerated
     */
    public String getBusinessLicenseLocation() {
        return businessLicenseLocation;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor.business_license_location
     * @param businessLicenseLocation
     *         the value for vendor.business_license_location
     * @mbggenerated
     */
    public void setBusinessLicenseLocation(String businessLicenseLocation) {
        this.businessLicenseLocation = businessLicenseLocation == null ? null : businessLicenseLocation.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor.updated_at
     * @return the value of vendor.updated_at
     * @mbggenerated
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor.updated_at
     * @param updatedAt
     *         the value for vendor.updated_at
     * @mbggenerated
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor.created_at
     * @return the value of vendor.created_at
     * @mbggenerated
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor.created_at
     * @param createdAt
     *         the value for vendor.created_at
     * @mbggenerated
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor.enabled
     * @return the value of vendor.enabled
     * @mbggenerated
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor.enabled
     * @param enabled
     *         the value for vendor.enabled
     * @mbggenerated
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor.last_login
     * @return the value of vendor.last_login
     * @mbggenerated
     */
    public Date getLastLogin() {
        return lastLogin;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor.last_login
     * @param lastLogin
     *         the value for vendor.last_login
     * @mbggenerated
     */
    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor.national_mark
     * @return the value of vendor.national_mark
     * @mbggenerated
     */
    public String getNationalMark() {
        return nationalMark;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor.national_mark
     * @param nationalMark
     *         the value for vendor.national_mark
     * @mbggenerated
     */
    public void setNationalMark(String nationalMark) {
        this.nationalMark = nationalMark == null ? null : nationalMark.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor.product_image_score
     * @return the value of vendor.product_image_score
     * @mbggenerated
     */
    public Byte getProductImageScore() {
        return productImageScore;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor.product_image_score
     * @param productImageScore
     *         the value for vendor.product_image_score
     * @mbggenerated
     */
    public void setProductImageScore(Byte productImageScore) {
        this.productImageScore = productImageScore;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor.vendor_score
     * @return the value of vendor.vendor_score
     * @mbggenerated
     */
    public Byte getVendorScore() {
        return vendorScore;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor.vendor_score
     * @param vendorScore
     *         the value for vendor.vendor_score
     * @mbggenerated
     */
    public void setVendorScore(Byte vendorScore) {
        this.vendorScore = vendorScore;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor.score
     * @return the value of vendor.score
     * @mbggenerated
     */
    public BigDecimal getScore() {
        return score;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor.score
     * @param score
     *         the value for vendor.score
     * @mbggenerated
     */
    public void setScore(BigDecimal score) {
        this.score = score;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor.address_country_id
     * @return the value of vendor.address_country_id
     * @mbggenerated
     */
    public Long getAddressCountryId() {
        return addressCountryId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor.address_country_id
     * @param addressCountryId
     *         the value for vendor.address_country_id
     * @mbggenerated
     */
    public void setAddressCountryId(Long addressCountryId) {
        this.addressCountryId = addressCountryId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor.remark
     * @return the value of vendor.remark
     * @mbggenerated
     */
    public String getRemark() {
        return remark;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor.remark
     * @param remark
     *         the value for vendor.remark
     * @mbggenerated
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getErpSystem() {
        return erpSystem;
    }

    public void setErpSystem(String erpSystem) {
        this.erpSystem = erpSystem;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}