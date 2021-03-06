package com.intramirror.order.api.model;

public class ShippingProvider {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shipping_provider.shipping_provider_id
     *
     * @mbggenerated
     */
    private Long shippingProviderId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shipping_provider.name
     *
     * @mbggenerated
     */
    private String name;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shipping_provider.transfer_consignee
     *
     * @mbggenerated
     */
    private String transferConsignee;

    private String personName;
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shipping_provider.transfer_addr
     *
     * @mbggenerated
     */
    private String transferAddr;
    private String transferAddr3;
    private String transferAddr2;

    private String transferContact;

    private String transferPiva;

    private String emailAddress;

    private String countryCode;
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shipping_provider.addr_district
     *
     * @mbggenerated
     */
    private String addrDistrict;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shipping_provider.addr_city
     *
     * @mbggenerated
     */
    private String addrCity;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shipping_provider.addr_province
     *
     * @mbggenerated
     */
    private String addrProvince;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shipping_provider.addr_country
     *
     * @mbggenerated
     */
    private String addrCountry;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shipping_provider.zip_code
     *
     * @mbggenerated
     */
    private String zipCode;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shipping_provider.shipping_provider_id
     *
     * @return the value of shipping_provider.shipping_provider_id
     *
     * @mbggenerated
     */
    public Long getShippingProviderId() {
        return shippingProviderId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shipping_provider.shipping_provider_id
     *
     * @param shippingProviderId the value for shipping_provider.shipping_provider_id
     *
     * @mbggenerated
     */
    public void setShippingProviderId(Long shippingProviderId) {
        this.shippingProviderId = shippingProviderId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shipping_provider.name
     *
     * @return the value of shipping_provider.name
     *
     * @mbggenerated
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shipping_provider.name
     *
     * @param name the value for shipping_provider.name
     *
     * @mbggenerated
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shipping_provider.transfer_consignee
     *
     * @return the value of shipping_provider.transfer_consignee
     *
     * @mbggenerated
     */
    public String getTransferConsignee() {
        return transferConsignee;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shipping_provider.transfer_consignee
     *
     * @param transferConsignee the value for shipping_provider.transfer_consignee
     *
     * @mbggenerated
     */
    public void setTransferConsignee(String transferConsignee) {
        this.transferConsignee = transferConsignee == null ? null : transferConsignee.trim();
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getTransferPiva() {
        return transferPiva;
    }

    public void setTransferPiva(String transferPiva) {
        this.transferPiva = transferPiva;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shipping_provider.transfer_addr
     *
     * @return the value of shipping_provider.transfer_addr
     *
     * @mbggenerated
     */
    public String getTransferAddr() {
        return transferAddr;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shipping_provider.transfer_addr
     *
     * @param transferAddr the value for shipping_provider.transfer_addr
     *
     * @mbggenerated
     */
    public void setTransferAddr(String transferAddr) {
        this.transferAddr = transferAddr == null ? null : transferAddr.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shipping_provider.addr_district
     *
     * @return the value of shipping_provider.addr_district
     *
     * @mbggenerated
     */
    public String getAddrDistrict() {
        return addrDistrict;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shipping_provider.addr_district
     *
     * @param addrDistrict the value for shipping_provider.addr_district
     *
     * @mbggenerated
     */
    public void setAddrDistrict(String addrDistrict) {
        this.addrDistrict = addrDistrict == null ? null : addrDistrict.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shipping_provider.addr_city
     *
     * @return the value of shipping_provider.addr_city
     *
     * @mbggenerated
     */
    public String getAddrCity() {
        return addrCity;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shipping_provider.addr_city
     *
     * @param addrCity the value for shipping_provider.addr_city
     *
     * @mbggenerated
     */
    public void setAddrCity(String addrCity) {
        this.addrCity = addrCity == null ? null : addrCity.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shipping_provider.addr_province
     *
     * @return the value of shipping_provider.addr_province
     *
     * @mbggenerated
     */
    public String getAddrProvince() {
        return addrProvince;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shipping_provider.addr_province
     *
     * @param addrProvince the value for shipping_provider.addr_province
     *
     * @mbggenerated
     */
    public void setAddrProvince(String addrProvince) {
        this.addrProvince = addrProvince == null ? null : addrProvince.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shipping_provider.addr_country
     *
     * @return the value of shipping_provider.addr_country
     *
     * @mbggenerated
     */
    public String getAddrCountry() {
        return addrCountry;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shipping_provider.addr_country
     *
     * @param addrCountry the value for shipping_provider.addr_country
     *
     * @mbggenerated
     */
    public void setAddrCountry(String addrCountry) {
        this.addrCountry = addrCountry == null ? null : addrCountry.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shipping_provider.zip_code
     *
     * @return the value of shipping_provider.zip_code
     *
     * @mbggenerated
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shipping_provider.zip_code
     *
     * @param zipCode the value for shipping_provider.zip_code
     *
     * @mbggenerated
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode == null ? null : zipCode.trim();
    }

    public String getTransferAddr3() {
        return transferAddr3;
    }

    public void setTransferAddr3(String transferAddr3) {
        this.transferAddr3 = transferAddr3;
    }

    public String getTransferAddr2() {
        return transferAddr2;
    }

    public void setTransferAddr2(String transferAddr2) {
        this.transferAddr2 = transferAddr2;
    }

    public String getTransferContact() {
        return transferContact;
    }

    public void setTransferContact(String transferContact) {
        this.transferContact = transferContact;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}