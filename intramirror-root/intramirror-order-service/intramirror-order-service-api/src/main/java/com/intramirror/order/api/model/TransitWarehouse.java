package com.intramirror.order.api.model;

import java.util.Date;

public class TransitWarehouse {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column transit_warehouse.id
     *
     * @mbg.generated
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column transit_warehouse.warehouse_name
     *
     * @mbg.generated
     */
    private String warehouseName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column transit_warehouse.contact_personName
     *
     * @mbg.generated
     */
    private String contactPersonname;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column transit_warehouse.contact_companyName
     *
     * @mbg.generated
     */
    private String contactCompanyname;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column transit_warehouse.contact_phoneNumber
     *
     * @mbg.generated
     */
    private String contactPhonenumber;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column transit_warehouse.contact_emailAddress
     *
     * @mbg.generated
     */
    private String contactEmailaddress;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column transit_warehouse.address_streetLines
     *
     * @mbg.generated
     */
    private String addressStreetlines;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column transit_warehouse.address_streetLines2
     *
     * @mbg.generated
     */
    private String addressStreetlines2;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column transit_warehouse.address_streetLines3
     *
     * @mbg.generated
     */
    private String addressStreetlines3;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column transit_warehouse.address_city
     *
     * @mbg.generated
     */
    private String addressCity;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column transit_warehouse.address_postalCode
     *
     * @mbg.generated
     */
    private String addressPostalcode;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column transit_warehouse.address_countryCode
     *
     * @mbg.generated
     */
    private String addressCountrycode;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column transit_warehouse.registered_person
     *
     * @mbg.generated
     */
    private String registeredPerson;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column transit_warehouse.country
     *
     * @mbg.generated
     */
    private String country;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column transit_warehouse.registered_phoneNumber
     *
     * @mbg.generated
     */
    private String registeredPhonenumber;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column transit_warehouse.registered_email
     *
     * @mbg.generated
     */
    private String registeredEmail;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column transit_warehouse.registered_address
     *
     * @mbg.generated
     */
    private String registeredAddress;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column transit_warehouse.registered_address2
     *
     * @mbg.generated
     */
    private String registeredAddress2;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column transit_warehouse.registered_address3
     *
     * @mbg.generated
     */
    private String registeredAddress3;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column transit_warehouse.status
     *
     * @mbg.generated
     */
    private Integer status;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column transit_warehouse.version
     *
     * @mbg.generated
     */
    private Integer version;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column transit_warehouse.create_time
     *
     * @mbg.generated
     */
    private Date createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column transit_warehouse.update_time
     *
     * @mbg.generated
     */
    private Date updateTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column transit_warehouse.is_deleted
     *
     * @mbg.generated
     */
    private Integer isDeleted;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column transit_warehouse.id
     *
     * @return the value of transit_warehouse.id
     *
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column transit_warehouse.id
     *
     * @param id the value for transit_warehouse.id
     *
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column transit_warehouse.warehouse_name
     *
     * @return the value of transit_warehouse.warehouse_name
     *
     * @mbg.generated
     */
    public String getWarehouseName() {
        return warehouseName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column transit_warehouse.warehouse_name
     *
     * @param warehouseName the value for transit_warehouse.warehouse_name
     *
     * @mbg.generated
     */
    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName == null ? null : warehouseName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column transit_warehouse.contact_personName
     *
     * @return the value of transit_warehouse.contact_personName
     *
     * @mbg.generated
     */
    public String getContactPersonname() {
        return contactPersonname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column transit_warehouse.contact_personName
     *
     * @param contactPersonname the value for transit_warehouse.contact_personName
     *
     * @mbg.generated
     */
    public void setContactPersonname(String contactPersonname) {
        this.contactPersonname = contactPersonname == null ? null : contactPersonname.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column transit_warehouse.contact_companyName
     *
     * @return the value of transit_warehouse.contact_companyName
     *
     * @mbg.generated
     */
    public String getContactCompanyname() {
        return contactCompanyname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column transit_warehouse.contact_companyName
     *
     * @param contactCompanyname the value for transit_warehouse.contact_companyName
     *
     * @mbg.generated
     */
    public void setContactCompanyname(String contactCompanyname) {
        this.contactCompanyname = contactCompanyname == null ? null : contactCompanyname.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column transit_warehouse.contact_phoneNumber
     *
     * @return the value of transit_warehouse.contact_phoneNumber
     *
     * @mbg.generated
     */
    public String getContactPhonenumber() {
        return contactPhonenumber;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column transit_warehouse.contact_phoneNumber
     *
     * @param contactPhonenumber the value for transit_warehouse.contact_phoneNumber
     *
     * @mbg.generated
     */
    public void setContactPhonenumber(String contactPhonenumber) {
        this.contactPhonenumber = contactPhonenumber == null ? null : contactPhonenumber.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column transit_warehouse.contact_emailAddress
     *
     * @return the value of transit_warehouse.contact_emailAddress
     *
     * @mbg.generated
     */
    public String getContactEmailaddress() {
        return contactEmailaddress;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column transit_warehouse.contact_emailAddress
     *
     * @param contactEmailaddress the value for transit_warehouse.contact_emailAddress
     *
     * @mbg.generated
     */
    public void setContactEmailaddress(String contactEmailaddress) {
        this.contactEmailaddress = contactEmailaddress == null ? null : contactEmailaddress.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column transit_warehouse.address_streetLines
     *
     * @return the value of transit_warehouse.address_streetLines
     *
     * @mbg.generated
     */
    public String getAddressStreetlines() {
        return addressStreetlines;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column transit_warehouse.address_streetLines
     *
     * @param addressStreetlines the value for transit_warehouse.address_streetLines
     *
     * @mbg.generated
     */
    public void setAddressStreetlines(String addressStreetlines) {
        this.addressStreetlines = addressStreetlines == null ? null : addressStreetlines.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column transit_warehouse.address_streetLines2
     *
     * @return the value of transit_warehouse.address_streetLines2
     *
     * @mbg.generated
     */
    public String getAddressStreetlines2() {
        return addressStreetlines2;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column transit_warehouse.address_streetLines2
     *
     * @param addressStreetlines2 the value for transit_warehouse.address_streetLines2
     *
     * @mbg.generated
     */
    public void setAddressStreetlines2(String addressStreetlines2) {
        this.addressStreetlines2 = addressStreetlines2 == null ? null : addressStreetlines2.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column transit_warehouse.address_streetLines3
     *
     * @return the value of transit_warehouse.address_streetLines3
     *
     * @mbg.generated
     */
    public String getAddressStreetlines3() {
        return addressStreetlines3;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column transit_warehouse.address_streetLines3
     *
     * @param addressStreetlines3 the value for transit_warehouse.address_streetLines3
     *
     * @mbg.generated
     */
    public void setAddressStreetlines3(String addressStreetlines3) {
        this.addressStreetlines3 = addressStreetlines3 == null ? null : addressStreetlines3.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column transit_warehouse.address_city
     *
     * @return the value of transit_warehouse.address_city
     *
     * @mbg.generated
     */
    public String getAddressCity() {
        return addressCity;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column transit_warehouse.address_city
     *
     * @param addressCity the value for transit_warehouse.address_city
     *
     * @mbg.generated
     */
    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity == null ? null : addressCity.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column transit_warehouse.address_postalCode
     *
     * @return the value of transit_warehouse.address_postalCode
     *
     * @mbg.generated
     */
    public String getAddressPostalcode() {
        return addressPostalcode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column transit_warehouse.address_postalCode
     *
     * @param addressPostalcode the value for transit_warehouse.address_postalCode
     *
     * @mbg.generated
     */
    public void setAddressPostalcode(String addressPostalcode) {
        this.addressPostalcode = addressPostalcode == null ? null : addressPostalcode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column transit_warehouse.address_countryCode
     *
     * @return the value of transit_warehouse.address_countryCode
     *
     * @mbg.generated
     */
    public String getAddressCountrycode() {
        return addressCountrycode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column transit_warehouse.address_countryCode
     *
     * @param addressCountrycode the value for transit_warehouse.address_countryCode
     *
     * @mbg.generated
     */
    public void setAddressCountrycode(String addressCountrycode) {
        this.addressCountrycode = addressCountrycode == null ? null : addressCountrycode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column transit_warehouse.registered_person
     *
     * @return the value of transit_warehouse.registered_person
     *
     * @mbg.generated
     */
    public String getRegisteredPerson() {
        return registeredPerson;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column transit_warehouse.registered_person
     *
     * @param registeredPerson the value for transit_warehouse.registered_person
     *
     * @mbg.generated
     */
    public void setRegisteredPerson(String registeredPerson) {
        this.registeredPerson = registeredPerson == null ? null : registeredPerson.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column transit_warehouse.country
     *
     * @return the value of transit_warehouse.country
     *
     * @mbg.generated
     */
    public String getCountry() {
        return country;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column transit_warehouse.country
     *
     * @param country the value for transit_warehouse.country
     *
     * @mbg.generated
     */
    public void setCountry(String country) {
        this.country = country == null ? null : country.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column transit_warehouse.registered_phoneNumber
     *
     * @return the value of transit_warehouse.registered_phoneNumber
     *
     * @mbg.generated
     */
    public String getRegisteredPhonenumber() {
        return registeredPhonenumber;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column transit_warehouse.registered_phoneNumber
     *
     * @param registeredPhonenumber the value for transit_warehouse.registered_phoneNumber
     *
     * @mbg.generated
     */
    public void setRegisteredPhonenumber(String registeredPhonenumber) {
        this.registeredPhonenumber = registeredPhonenumber == null ? null : registeredPhonenumber.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column transit_warehouse.registered_email
     *
     * @return the value of transit_warehouse.registered_email
     *
     * @mbg.generated
     */
    public String getRegisteredEmail() {
        return registeredEmail;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column transit_warehouse.registered_email
     *
     * @param registeredEmail the value for transit_warehouse.registered_email
     *
     * @mbg.generated
     */
    public void setRegisteredEmail(String registeredEmail) {
        this.registeredEmail = registeredEmail == null ? null : registeredEmail.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column transit_warehouse.registered_address
     *
     * @return the value of transit_warehouse.registered_address
     *
     * @mbg.generated
     */
    public String getRegisteredAddress() {
        return registeredAddress;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column transit_warehouse.registered_address
     *
     * @param registeredAddress the value for transit_warehouse.registered_address
     *
     * @mbg.generated
     */
    public void setRegisteredAddress(String registeredAddress) {
        this.registeredAddress = registeredAddress == null ? null : registeredAddress.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column transit_warehouse.registered_address2
     *
     * @return the value of transit_warehouse.registered_address2
     *
     * @mbg.generated
     */
    public String getRegisteredAddress2() {
        return registeredAddress2;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column transit_warehouse.registered_address2
     *
     * @param registeredAddress2 the value for transit_warehouse.registered_address2
     *
     * @mbg.generated
     */
    public void setRegisteredAddress2(String registeredAddress2) {
        this.registeredAddress2 = registeredAddress2 == null ? null : registeredAddress2.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column transit_warehouse.registered_address3
     *
     * @return the value of transit_warehouse.registered_address3
     *
     * @mbg.generated
     */
    public String getRegisteredAddress3() {
        return registeredAddress3;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column transit_warehouse.registered_address3
     *
     * @param registeredAddress3 the value for transit_warehouse.registered_address3
     *
     * @mbg.generated
     */
    public void setRegisteredAddress3(String registeredAddress3) {
        this.registeredAddress3 = registeredAddress3 == null ? null : registeredAddress3.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column transit_warehouse.status
     *
     * @return the value of transit_warehouse.status
     *
     * @mbg.generated
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column transit_warehouse.status
     *
     * @param status the value for transit_warehouse.status
     *
     * @mbg.generated
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column transit_warehouse.version
     *
     * @return the value of transit_warehouse.version
     *
     * @mbg.generated
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column transit_warehouse.version
     *
     * @param version the value for transit_warehouse.version
     *
     * @mbg.generated
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column transit_warehouse.create_time
     *
     * @return the value of transit_warehouse.create_time
     *
     * @mbg.generated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column transit_warehouse.create_time
     *
     * @param createTime the value for transit_warehouse.create_time
     *
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column transit_warehouse.update_time
     *
     * @return the value of transit_warehouse.update_time
     *
     * @mbg.generated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column transit_warehouse.update_time
     *
     * @param updateTime the value for transit_warehouse.update_time
     *
     * @mbg.generated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column transit_warehouse.is_deleted
     *
     * @return the value of transit_warehouse.is_deleted
     *
     * @mbg.generated
     */
    public Integer getIsDeleted() {
        return isDeleted;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column transit_warehouse.is_deleted
     *
     * @param isDeleted the value for transit_warehouse.is_deleted
     *
     * @mbg.generated
     */
    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
}