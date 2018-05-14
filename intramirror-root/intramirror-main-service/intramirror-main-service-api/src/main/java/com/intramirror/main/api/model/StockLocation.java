package com.intramirror.main.api.model;

import java.util.Date;

public class StockLocation {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column stock_location.location_id
     *
     * @mbggenerated
     */
    private Long locationId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column stock_location.stock_location
     *
     * @mbggenerated
     */
    private String stockLocation;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column stock_location.ship_from_location_id
     *
     * @mbggenerated
     */
    private Long shipFromLocationId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column stock_location.vendor_id
     *
     * @mbggenerated
     */
    private Long vendorId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column stock_location.contact_person_name
     *
     * @mbggenerated
     */
    private String contactPersonName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column stock_location.contact_company_name
     *
     * @mbggenerated
     */
    private String contactCompanyName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column stock_location.contact_phone_number
     *
     * @mbggenerated
     */
    private String contactPhoneNumber;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column stock_location.contact_email_address
     *
     * @mbggenerated
     */
    private String contactEmailAddress;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column stock_location.address_streetLines
     *
     * @mbggenerated
     */
    private String addressStreetlines;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column stock_location.address_streetLines2
     *
     * @mbggenerated
     */
    private String addressStreetlines2;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column stock_location.address_streetLines3
     *
     * @mbggenerated
     */
    private String addressStreetlines3;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column stock_location.address_city
     *
     * @mbggenerated
     */
    private String addressCity;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column stock_location.address_postal_code
     *
     * @mbggenerated
     */
    private String addressPostalCode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column stock_location.address_country_code
     *
     * @mbggenerated
     */
    private String addressCountryCode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column stock_location.create_at
     *
     * @mbggenerated
     */
    private Date createAt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column stock_location.update_at
     *
     * @mbggenerated
     */
    private Date updateAt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column stock_location.enabled
     *
     * @mbggenerated
     */
    private Boolean enabled;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column stock_location.location_id
     *
     * @return the value of stock_location.location_id
     *
     * @mbggenerated
     */
    public Long getLocationId() {
        return locationId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column stock_location.location_id
     *
     * @param locationId the value for stock_location.location_id
     *
     * @mbggenerated
     */
    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column stock_location.stock_location
     *
     * @return the value of stock_location.stock_location
     *
     * @mbggenerated
     */
    public String getStockLocation() {
        return stockLocation;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column stock_location.stock_location
     *
     * @param stockLocation the value for stock_location.stock_location
     *
     * @mbggenerated
     */
    public void setStockLocation(String stockLocation) {
        this.stockLocation = stockLocation == null ? null : stockLocation.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column stock_location.ship_from_location_id
     *
     * @return the value of stock_location.ship_from_location_id
     *
     * @mbggenerated
     */
    public Long getShipFromLocationId() {
        return shipFromLocationId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column stock_location.ship_from_location_id
     *
     * @param shipFromLocationId the value for stock_location.ship_from_location_id
     *
     * @mbggenerated
     */
    public void setShipFromLocationId(Long shipFromLocationId) {
        this.shipFromLocationId = shipFromLocationId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column stock_location.vendor_id
     *
     * @return the value of stock_location.vendor_id
     *
     * @mbggenerated
     */
    public Long getVendorId() {
        return vendorId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column stock_location.vendor_id
     *
     * @param vendorId the value for stock_location.vendor_id
     *
     * @mbggenerated
     */
    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column stock_location.contact_person_name
     *
     * @return the value of stock_location.contact_person_name
     *
     * @mbggenerated
     */
    public String getContactPersonName() {
        return contactPersonName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column stock_location.contact_person_name
     *
     * @param contactPersonName the value for stock_location.contact_person_name
     *
     * @mbggenerated
     */
    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName == null ? null : contactPersonName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column stock_location.contact_company_name
     *
     * @return the value of stock_location.contact_company_name
     *
     * @mbggenerated
     */
    public String getContactCompanyName() {
        return contactCompanyName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column stock_location.contact_company_name
     *
     * @param contactCompanyName the value for stock_location.contact_company_name
     *
     * @mbggenerated
     */
    public void setContactCompanyName(String contactCompanyName) {
        this.contactCompanyName = contactCompanyName == null ? null : contactCompanyName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column stock_location.contact_phone_number
     *
     * @return the value of stock_location.contact_phone_number
     *
     * @mbggenerated
     */
    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column stock_location.contact_phone_number
     *
     * @param contactPhoneNumber the value for stock_location.contact_phone_number
     *
     * @mbggenerated
     */
    public void setContactPhoneNumber(String contactPhoneNumber) {
        this.contactPhoneNumber = contactPhoneNumber == null ? null : contactPhoneNumber.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column stock_location.contact_email_address
     *
     * @return the value of stock_location.contact_email_address
     *
     * @mbggenerated
     */
    public String getContactEmailAddress() {
        return contactEmailAddress;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column stock_location.contact_email_address
     *
     * @param contactEmailAddress the value for stock_location.contact_email_address
     *
     * @mbggenerated
     */
    public void setContactEmailAddress(String contactEmailAddress) {
        this.contactEmailAddress = contactEmailAddress == null ? null : contactEmailAddress.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column stock_location.address_streetLines
     *
     * @return the value of stock_location.address_streetLines
     *
     * @mbggenerated
     */
    public String getAddressStreetlines() {
        return addressStreetlines;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column stock_location.address_streetLines
     *
     * @param addressStreetlines the value for stock_location.address_streetLines
     *
     * @mbggenerated
     */
    public void setAddressStreetlines(String addressStreetlines) {
        this.addressStreetlines = addressStreetlines == null ? null : addressStreetlines.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column stock_location.address_streetLines2
     *
     * @return the value of stock_location.address_streetLines2
     *
     * @mbggenerated
     */
    public String getAddressStreetlines2() {
        return addressStreetlines2;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column stock_location.address_streetLines2
     *
     * @param addressStreetlines2 the value for stock_location.address_streetLines2
     *
     * @mbggenerated
     */
    public void setAddressStreetlines2(String addressStreetlines2) {
        this.addressStreetlines2 = addressStreetlines2 == null ? null : addressStreetlines2.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column stock_location.address_streetLines3
     *
     * @return the value of stock_location.address_streetLines3
     *
     * @mbggenerated
     */
    public String getAddressStreetlines3() {
        return addressStreetlines3;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column stock_location.address_streetLines3
     *
     * @param addressStreetlines3 the value for stock_location.address_streetLines3
     *
     * @mbggenerated
     */
    public void setAddressStreetlines3(String addressStreetlines3) {
        this.addressStreetlines3 = addressStreetlines3 == null ? null : addressStreetlines3.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column stock_location.address_city
     *
     * @return the value of stock_location.address_city
     *
     * @mbggenerated
     */
    public String getAddressCity() {
        return addressCity;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column stock_location.address_city
     *
     * @param addressCity the value for stock_location.address_city
     *
     * @mbggenerated
     */
    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity == null ? null : addressCity.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column stock_location.address_postal_code
     *
     * @return the value of stock_location.address_postal_code
     *
     * @mbggenerated
     */
    public String getAddressPostalCode() {
        return addressPostalCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column stock_location.address_postal_code
     *
     * @param addressPostalCode the value for stock_location.address_postal_code
     *
     * @mbggenerated
     */
    public void setAddressPostalCode(String addressPostalCode) {
        this.addressPostalCode = addressPostalCode == null ? null : addressPostalCode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column stock_location.address_country_code
     *
     * @return the value of stock_location.address_country_code
     *
     * @mbggenerated
     */
    public String getAddressCountryCode() {
        return addressCountryCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column stock_location.address_country_code
     *
     * @param addressCountryCode the value for stock_location.address_country_code
     *
     * @mbggenerated
     */
    public void setAddressCountryCode(String addressCountryCode) {
        this.addressCountryCode = addressCountryCode == null ? null : addressCountryCode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column stock_location.create_at
     *
     * @return the value of stock_location.create_at
     *
     * @mbggenerated
     */
    public Date getCreateAt() {
        return createAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column stock_location.create_at
     *
     * @param createAt the value for stock_location.create_at
     *
     * @mbggenerated
     */
    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column stock_location.update_at
     *
     * @return the value of stock_location.update_at
     *
     * @mbggenerated
     */
    public Date getUpdateAt() {
        return updateAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column stock_location.update_at
     *
     * @param updateAt the value for stock_location.update_at
     *
     * @mbggenerated
     */
    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column stock_location.enabled
     *
     * @return the value of stock_location.enabled
     *
     * @mbggenerated
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column stock_location.enabled
     *
     * @param enabled the value for stock_location.enabled
     *
     * @mbggenerated
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}