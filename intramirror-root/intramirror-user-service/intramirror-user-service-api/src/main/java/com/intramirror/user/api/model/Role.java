package com.intramirror.user.api.model;

import java.util.Date;

public class Role {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column role.role_id
     *
     * @mbggenerated
     */
    private Long roleId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column role.role_name
     *
     * @mbggenerated
     */
    private String roleName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column role.remark
     *
     * @mbggenerated
     */
    private String remark;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column role.privilege
     *
     * @mbggenerated
     */
    private Long privilege;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column role.operator
     *
     * @mbggenerated
     */
    private String operator;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column role.created_at
     *
     * @mbggenerated
     */
    private Date createdAt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column role.updated_at
     *
     * @mbggenerated
     */
    private Date updatedAt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column role.enabled
     *
     * @mbggenerated
     */
    private Boolean enabled;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column role.can_show
     *
     * @mbggenerated
     */
    private Boolean canShow;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column role.role_id
     *
     * @return the value of role.role_id
     *
     * @mbggenerated
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column role.role_id
     *
     * @param roleId the value for role.role_id
     *
     * @mbggenerated
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column role.role_name
     *
     * @return the value of role.role_name
     *
     * @mbggenerated
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column role.role_name
     *
     * @param roleName the value for role.role_name
     *
     * @mbggenerated
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName == null ? null : roleName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column role.remark
     *
     * @return the value of role.remark
     *
     * @mbggenerated
     */
    public String getRemark() {
        return remark;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column role.remark
     *
     * @param remark the value for role.remark
     *
     * @mbggenerated
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column role.privilege
     *
     * @return the value of role.privilege
     *
     * @mbggenerated
     */
    public Long getPrivilege() {
        return privilege;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column role.privilege
     *
     * @param privilege the value for role.privilege
     *
     * @mbggenerated
     */
    public void setPrivilege(Long privilege) {
        this.privilege = privilege;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column role.operator
     *
     * @return the value of role.operator
     *
     * @mbggenerated
     */
    public String getOperator() {
        return operator;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column role.operator
     *
     * @param operator the value for role.operator
     *
     * @mbggenerated
     */
    public void setOperator(String operator) {
        this.operator = operator == null ? null : operator.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column role.created_at
     *
     * @return the value of role.created_at
     *
     * @mbggenerated
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column role.created_at
     *
     * @param createdAt the value for role.created_at
     *
     * @mbggenerated
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column role.updated_at
     *
     * @return the value of role.updated_at
     *
     * @mbggenerated
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column role.updated_at
     *
     * @param updatedAt the value for role.updated_at
     *
     * @mbggenerated
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column role.enabled
     *
     * @return the value of role.enabled
     *
     * @mbggenerated
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column role.enabled
     *
     * @param enabled the value for role.enabled
     *
     * @mbggenerated
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column role.can_show
     *
     * @return the value of role.can_show
     *
     * @mbggenerated
     */
    public Boolean getCanShow() {
        return canShow;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column role.can_show
     *
     * @param canShow the value for role.can_show
     *
     * @mbggenerated
     */
    public void setCanShow(Boolean canShow) {
        this.canShow = canShow;
    }
}