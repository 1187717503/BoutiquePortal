package com.intramirror.user.api.model;

import java.util.Date;

public class VendorPayment {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor_payment.vendor_payment_id
     *
     * @mbggenerated
     */
    private Long vendorPaymentId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor_payment.user_id
     *
     * @mbggenerated
     */
    private Long userId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor_payment.vendor_id
     *
     * @mbggenerated
     */
    private Long vendorId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor_payment.beneficiary_name
     *
     * @mbggenerated
     */
    private String beneficiaryName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor_payment.contact_person
     *
     * @mbggenerated
     */
    private String contactPerson;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor_payment.contact_email
     *
     * @mbggenerated
     */
    private String contactEmail;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor_payment.contact_phone
     *
     * @mbggenerated
     */
    private String contactPhone;

    private  String areaCode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor_payment.beneficiary_address
     *
     * @mbggenerated
     */
    private String beneficiaryAddress;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor_payment.city1
     *
     * @mbggenerated
     */
    private String city1;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor_payment.postal_code1
     *
     * @mbggenerated
     */
    private String postalCode1;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor_payment.country1
     *
     * @mbggenerated
     */
    private Long country1;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor_payment.beneficiary_bank_name
     *
     * @mbggenerated
     */
    private String beneficiaryBankName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor_payment.account_num
     *
     * @mbggenerated
     */
    private String accountNum;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor_payment.beneficiary_bank_address
     *
     * @mbggenerated
     */
    private String beneficiaryBankAddress;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor_payment.city2
     *
     * @mbggenerated
     */
    private String city2;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor_payment.postal_cod2
     *
     * @mbggenerated
     */
    private String postalCod2;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor_payment.country2
     *
     * @mbggenerated
     */
    private Long country2;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor_payment.created_at
     *
     * @mbggenerated
     */
    private Date createdAt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vendor_payment.updated_at
     *
     * @mbggenerated
     */
    private Date updatedAt;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor_payment.vendor_payment_id
     *
     * @return the value of vendor_payment.vendor_payment_id
     *
     * @mbggenerated
     */
    public Long getVendorPaymentId() {
        return vendorPaymentId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor_payment.vendor_payment_id
     *
     * @param vendorPaymentId the value for vendor_payment.vendor_payment_id
     *
     * @mbggenerated
     */
    public void setVendorPaymentId(Long vendorPaymentId) {
        this.vendorPaymentId = vendorPaymentId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor_payment.user_id
     *
     * @return the value of vendor_payment.user_id
     *
     * @mbggenerated
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor_payment.user_id
     *
     * @param userId the value for vendor_payment.user_id
     *
     * @mbggenerated
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor_payment.vendor_id
     *
     * @return the value of vendor_payment.vendor_id
     *
     * @mbggenerated
     */
    public Long getVendorId() {
        return vendorId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor_payment.vendor_id
     *
     * @param vendorId the value for vendor_payment.vendor_id
     *
     * @mbggenerated
     */
    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor_payment.beneficiary_name
     *
     * @return the value of vendor_payment.beneficiary_name
     *
     * @mbggenerated
     */
    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor_payment.beneficiary_name
     *
     * @param beneficiaryName the value for vendor_payment.beneficiary_name
     *
     * @mbggenerated
     */
    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName == null ? null : beneficiaryName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor_payment.contact_person
     *
     * @return the value of vendor_payment.contact_person
     *
     * @mbggenerated
     */
    public String getContactPerson() {
        return contactPerson;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor_payment.contact_person
     *
     * @param contactPerson the value for vendor_payment.contact_person
     *
     * @mbggenerated
     */
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson == null ? null : contactPerson.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor_payment.contact_email
     *
     * @return the value of vendor_payment.contact_email
     *
     * @mbggenerated
     */
    public String getContactEmail() {
        return contactEmail;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor_payment.contact_email
     *
     * @param contactEmail the value for vendor_payment.contact_email
     *
     * @mbggenerated
     */
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail == null ? null : contactEmail.trim();
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor_payment.contact_phone
     *
     * @return the value of vendor_payment.contact_phone
     *
     * @mbggenerated
     */
    public String getContactPhone() {
        return contactPhone;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor_payment.contact_phone
     *
     * @param contactPhone the value for vendor_payment.contact_phone
     *
     * @mbggenerated
     */
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone == null ? null : contactPhone.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor_payment.beneficiary_address
     *
     * @return the value of vendor_payment.beneficiary_address
     *
     * @mbggenerated
     */
    public String getBeneficiaryAddress() {
        return beneficiaryAddress;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor_payment.beneficiary_address
     *
     * @param beneficiaryAddress the value for vendor_payment.beneficiary_address
     *
     * @mbggenerated
     */
    public void setBeneficiaryAddress(String beneficiaryAddress) {
        this.beneficiaryAddress = beneficiaryAddress == null ? null : beneficiaryAddress.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor_payment.city1
     *
     * @return the value of vendor_payment.city1
     *
     * @mbggenerated
     */
    public String getCity1() {
        return city1;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor_payment.city1
     *
     * @param city1 the value for vendor_payment.city1
     *
     * @mbggenerated
     */
    public void setCity1(String city1) {
        this.city1 = city1 == null ? null : city1.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor_payment.postal_code1
     *
     * @return the value of vendor_payment.postal_code1
     *
     * @mbggenerated
     */
    public String getPostalCode1() {
        return postalCode1;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor_payment.postal_code1
     *
     * @param postalCode1 the value for vendor_payment.postal_code1
     *
     * @mbggenerated
     */
    public void setPostalCode1(String postalCode1) {
        this.postalCode1 = postalCode1 == null ? null : postalCode1.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor_payment.country1
     *
     * @return the value of vendor_payment.country1
     *
     * @mbggenerated
     */
    public Long getCountry1() {
        return country1;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor_payment.country1
     *
     * @param country1 the value for vendor_payment.country1
     *
     * @mbggenerated
     */
    public void setCountry1(Long country1) {
        this.country1 = country1;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor_payment.beneficiary_bank_name
     *
     * @return the value of vendor_payment.beneficiary_bank_name
     *
     * @mbggenerated
     */
    public String getBeneficiaryBankName() {
        return beneficiaryBankName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor_payment.beneficiary_bank_name
     *
     * @param beneficiaryBankName the value for vendor_payment.beneficiary_bank_name
     *
     * @mbggenerated
     */
    public void setBeneficiaryBankName(String beneficiaryBankName) {
        this.beneficiaryBankName = beneficiaryBankName == null ? null : beneficiaryBankName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor_payment.account_num
     *
     * @return the value of vendor_payment.account_num
     *
     * @mbggenerated
     */
    public String getAccountNum() {
        return accountNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor_payment.account_num
     *
     * @param accountNum the value for vendor_payment.account_num
     *
     * @mbggenerated
     */
    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum == null ? null : accountNum.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor_payment.beneficiary_bank_address
     *
     * @return the value of vendor_payment.beneficiary_bank_address
     *
     * @mbggenerated
     */
    public String getBeneficiaryBankAddress() {
        return beneficiaryBankAddress;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor_payment.beneficiary_bank_address
     *
     * @param beneficiaryBankAddress the value for vendor_payment.beneficiary_bank_address
     *
     * @mbggenerated
     */
    public void setBeneficiaryBankAddress(String beneficiaryBankAddress) {
        this.beneficiaryBankAddress = beneficiaryBankAddress == null ? null : beneficiaryBankAddress.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor_payment.city2
     *
     * @return the value of vendor_payment.city2
     *
     * @mbggenerated
     */
    public String getCity2() {
        return city2;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor_payment.city2
     *
     * @param city2 the value for vendor_payment.city2
     *
     * @mbggenerated
     */
    public void setCity2(String city2) {
        this.city2 = city2 == null ? null : city2.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor_payment.postal_cod2
     *
     * @return the value of vendor_payment.postal_cod2
     *
     * @mbggenerated
     */
    public String getPostalCod2() {
        return postalCod2;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor_payment.postal_cod2
     *
     * @param postalCod2 the value for vendor_payment.postal_cod2
     *
     * @mbggenerated
     */
    public void setPostalCod2(String postalCod2) {
        this.postalCod2 = postalCod2 == null ? null : postalCod2.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor_payment.country2
     *
     * @return the value of vendor_payment.country2
     *
     * @mbggenerated
     */
    public Long getCountry2() {
        return country2;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor_payment.country2
     *
     * @param country2 the value for vendor_payment.country2
     *
     * @mbggenerated
     */
    public void setCountry2(Long country2) {
        this.country2 = country2 ;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor_payment.created_at
     *
     * @return the value of vendor_payment.created_at
     *
     * @mbggenerated
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor_payment.created_at
     *
     * @param createdAt the value for vendor_payment.created_at
     *
     * @mbggenerated
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vendor_payment.updated_at
     *
     * @return the value of vendor_payment.updated_at
     *
     * @mbggenerated
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vendor_payment.updated_at
     *
     * @param updatedAt the value for vendor_payment.updated_at
     *
     * @mbggenerated
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}