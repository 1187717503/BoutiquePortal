package com.intramirror.user.api.model;

import java.util.Date;

public class User {

    private boolean isParent;

    /**
     * This field was generated by MyBatis Generator. This field corresponds to
     * the database column user.user_id
     * @mbggenerated
     */
    private Long userId;

    /**
     * This field was generated by MyBatis Generator. This field corresponds to
     * the database column user.username
     * @mbggenerated
     */
    private String username;

    /**
     * This field was generated by MyBatis Generator. This field corresponds to
     * the database column user.email
     * @mbggenerated
     */
    private String email;

    /**
     * This field was generated by MyBatis Generator. This field corresponds to
     * the database column user.telephone
     * @mbggenerated
     */
    private String telephone;

    /**
     * This field was generated by MyBatis Generator. This field corresponds to
     * the database column user.contact_person_name
     * @mbggenerated
     */
    private String contactPersonName;

    /**
     * This field was generated by MyBatis Generator. This field corresponds to
     * the database column user.password
     * @mbggenerated
     */
    private String password;

    /**
     * This field was generated by MyBatis Generator. This field corresponds to
     * the database column user.identity_card
     * @mbggenerated
     */
    private String identityCard;

    /**
     * This field was generated by MyBatis Generator. This field corresponds to
     * the database column user.identity_card_name
     * @mbggenerated
     */
    private String identityCardName;

    /**
     * This field was generated by MyBatis Generator. This field corresponds to
     * the database column user.electronic_registered_person_id
     * @mbggenerated
     */
    private String electronicRegisteredPersonId;

    /**
     * This field was generated by MyBatis Generator. This field corresponds to
     * the database column user.user_image
     * @mbggenerated
     */
    private String userImage;

    /**
     * This field was generated by MyBatis Generator. This field corresponds to
     * the database column user.source
     * @mbggenerated
     */
    private Byte source;

    /**
     * This field was generated by MyBatis Generator. This field corresponds to
     * the database column user.last_login
     * @mbggenerated
     */
    private Date lastLogin;

    /**
     * This field was generated by MyBatis Generator. This field corresponds to
     * the database column user.type
     * @mbggenerated
     */
    private Byte type;

    /**
     * This field was generated by MyBatis Generator. This field corresponds to
     * the database column user.created_at
     * @mbggenerated
     */
    private Date createdAt;

    /**
     * This field was generated by MyBatis Generator. This field corresponds to
     * the database column user.updated_at
     * @mbggenerated
     */
    private Date updatedAt;

    /**
     * This field was generated by MyBatis Generator. This field corresponds to
     * the database column user.enabled
     * @mbggenerated
     */
    private Boolean enabled;

    /**
     * This field was generated by MyBatis Generator. This field corresponds to
     * the database column user.wechat
     * @mbggenerated
     */
    private String wechat;

    /**
     * This field was generated by MyBatis Generator. This field corresponds to
     * the database column user.contact_phone
     * @mbggenerated
     */
    private String contactPhone;

    /**
     * This field was generated by MyBatis Generator. This field corresponds to
     * the database column user.area_code
     * @mbggenerated
     */
    private String areaCode;

    private String access_token;
    private String refresh_token;
    private String openid;
    private String unionid;
    public String invitation_code;
    public String invited_code;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the
     * value of the database column user.user_id
     * @return the value of user.user_id
     * @mbggenerated
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the
     * value of the database column user.user_id
     * @param userId
     *         the value for user.user_id
     * @mbggenerated
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the
     * value of the database column user.username
     * @return the value of user.username
     * @mbggenerated
     */
    public String getUsername() {
        return username;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the
     * value of the database column user.username
     * @param username
     *         the value for user.username
     * @mbggenerated
     */
    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the
     * value of the database column user.email
     * @return the value of user.email
     * @mbggenerated
     */
    public String getEmail() {
        return email;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the
     * value of the database column user.email
     * @param email
     *         the value for user.email
     * @mbggenerated
     */
    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the
     * value of the database column user.telephone
     * @return the value of user.telephone
     * @mbggenerated
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the
     * value of the database column user.telephone
     * @param telephone
     *         the value for user.telephone
     * @mbggenerated
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone == null ? null : telephone.trim();
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the
     * value of the database column user.contact_person_name
     * @return the value of user.contact_person_name
     * @mbggenerated
     */
    public String getContactPersonName() {
        return contactPersonName;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the
     * value of the database column user.contact_person_name
     * @param contactPersonName
     *         the value for user.contact_person_name
     * @mbggenerated
     */
    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName == null ? null : contactPersonName.trim();
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the
     * value of the database column user.password
     * @return the value of user.password
     * @mbggenerated
     */
    public String getPassword() {
        return password;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the
     * value of the database column user.password
     * @param password
     *         the value for user.password
     * @mbggenerated
     */
    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the
     * value of the database column user.identity_card
     * @return the value of user.identity_card
     * @mbggenerated
     */
    public String getIdentityCard() {
        return identityCard;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the
     * value of the database column user.identity_card
     * @param identityCard
     *         the value for user.identity_card
     * @mbggenerated
     */
    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard == null ? null : identityCard.trim();
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the
     * value of the database column user.identity_card_name
     * @return the value of user.identity_card_name
     * @mbggenerated
     */
    public String getIdentityCardName() {
        return identityCardName;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the
     * value of the database column user.identity_card_name
     * @param identityCardName
     *         the value for user.identity_card_name
     * @mbggenerated
     */
    public void setIdentityCardName(String identityCardName) {
        this.identityCardName = identityCardName == null ? null : identityCardName.trim();
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the
     * value of the database column user.electronic_registered_person_id
     * @return the value of user.electronic_registered_person_id
     * @mbggenerated
     */
    public String getElectronicRegisteredPersonId() {
        return electronicRegisteredPersonId;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the
     * value of the database column user.electronic_registered_person_id
     * @param electronicRegisteredPersonId
     *         the value for user.electronic_registered_person_id
     * @mbggenerated
     */
    public void setElectronicRegisteredPersonId(String electronicRegisteredPersonId) {
        this.electronicRegisteredPersonId = electronicRegisteredPersonId == null ? null : electronicRegisteredPersonId.trim();
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the
     * value of the database column user.user_image
     * @return the value of user.user_image
     * @mbggenerated
     */
    public String getUserImage() {
        return userImage;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the
     * value of the database column user.user_image
     * @param userImage
     *         the value for user.user_image
     * @mbggenerated
     */
    public void setUserImage(String userImage) {
        this.userImage = userImage == null ? null : userImage.trim();
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the
     * value of the database column user.source
     * @return the value of user.source
     * @mbggenerated
     */
    public Byte getSource() {
        return source;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the
     * value of the database column user.source
     * @param source
     *         the value for user.source
     * @mbggenerated
     */
    public void setSource(Byte source) {
        this.source = source;
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the
     * value of the database column user.last_login
     * @return the value of user.last_login
     * @mbggenerated
     */
    public Date getLastLogin() {
        return lastLogin;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the
     * value of the database column user.last_login
     * @param lastLogin
     *         the value for user.last_login
     * @mbggenerated
     */
    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the
     * value of the database column user.type
     * @return the value of user.type
     * @mbggenerated
     */
    public Byte getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the
     * value of the database column user.type
     * @param type
     *         the value for user.type
     * @mbggenerated
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the
     * value of the database column user.created_at
     * @return the value of user.created_at
     * @mbggenerated
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the
     * value of the database column user.created_at
     * @param createdAt
     *         the value for user.created_at
     * @mbggenerated
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the
     * value of the database column user.updated_at
     * @return the value of user.updated_at
     * @mbggenerated
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the
     * value of the database column user.updated_at
     * @param updatedAt
     *         the value for user.updated_at
     * @mbggenerated
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the
     * value of the database column user.enabled
     * @return the value of user.enabled
     * @mbggenerated
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the
     * value of the database column user.enabled
     * @param enabled
     *         the value for user.enabled
     * @mbggenerated
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the
     * value of the database column user.wechat
     * @return the value of user.wechat
     * @mbggenerated
     */
    public String getWechat() {
        return wechat;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the
     * value of the database column user.wechat
     * @param wechat
     *         the value for user.wechat
     * @mbggenerated
     */
    public void setWechat(String wechat) {
        this.wechat = wechat == null ? null : wechat.trim();
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the
     * value of the database column user.contact_phone
     * @return the value of user.contact_phone
     * @mbggenerated
     */
    public String getContactPhone() {
        return contactPhone;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the
     * value of the database column user.contact_phone
     * @param contactPhone
     *         the value for user.contact_phone
     * @mbggenerated
     */
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone == null ? null : contactPhone.trim();
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the
     * value of the database column user.area_code
     * @return the value of user.area_code
     * @mbggenerated
     */
    public String getAreaCode() {
        return areaCode;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the
     * value of the database column user.area_code
     * @param areaCode
     *         the value for user.area_code
     * @mbggenerated
     */
    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode == null ? null : areaCode.trim();
    }

    public String getInvitation_code() {
        return invitation_code;
    }

    public void setInvitation_code(String invitation_code) {
        this.invitation_code = invitation_code;
    }

    public String getInvited_code() {
        return invited_code;
    }

    public void setInvited_code(String invited_code) {
        this.invited_code = invited_code;
    }

    public boolean getIsParent() {
        return isParent;
    }

    public void setIsParent(boolean parent) {
        isParent = parent;
    }
}