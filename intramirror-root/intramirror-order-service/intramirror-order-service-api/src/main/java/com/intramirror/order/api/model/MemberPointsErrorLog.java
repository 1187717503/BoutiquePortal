package com.intramirror.order.api.model;

import java.util.Date;

/**
 * database table is member_points_error_log
 * @mbg.generated do_not_delete_during_merge
 */
public class MemberPointsErrorLog {
    /**
     * database column is member_points_error_log.id
     * @mbg.generated
     */
    private Long id;

    /**
     * 订单号
     * database column is member_points_error_log.order_line_num
     * @mbg.generated
     */
    private String orderLineNum;

    /**
     * 会员积分，请求参数
     * database column is member_points_error_log.request_body
     * @mbg.generated
     */
    private String requestBody;

    /**
     * 接口返回报文
     * database column is member_points_error_log.response_body
     * @mbg.generated
     */
    private String responseBody;

    /**
     * 创建时间
     * database column is member_points_error_log.create_time
     * @mbg.generated
     */
    private Date createTime;

    /**
     * 更新时间
     * database column is member_points_error_log.update_time
     * @mbg.generated
     */
    private Date updateTime;

    /**
     * 是否删除 0：未删除 1：已删除
     * database column is member_points_error_log.is_deleted
     * @mbg.generated
     */
    private Integer isDeleted;

    /**
     * @return the value of member_points_error_log.id
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the value for member_points_error_log.id
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the value of member_points_error_log.order_line_num
     * @mbg.generated
     */
    public String getOrderLineNum() {
        return orderLineNum;
    }

    /**
     * @param orderLineNum the value for member_points_error_log.order_line_num
     * @mbg.generated
     */
    public void setOrderLineNum(String orderLineNum) {
        this.orderLineNum = orderLineNum == null ? null : orderLineNum.trim();
    }

    /**
     * @return the value of member_points_error_log.request_body
     * @mbg.generated
     */
    public String getRequestBody() {
        return requestBody;
    }

    /**
     * @param requestBody the value for member_points_error_log.request_body
     * @mbg.generated
     */
    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody == null ? null : requestBody.trim();
    }

    /**
     * @return the value of member_points_error_log.response_body
     * @mbg.generated
     */
    public String getResponseBody() {
        return responseBody;
    }

    /**
     * @param responseBody the value for member_points_error_log.response_body
     * @mbg.generated
     */
    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody == null ? null : responseBody.trim();
    }

    /**
     * @return the value of member_points_error_log.create_time
     * @mbg.generated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime the value for member_points_error_log.create_time
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return the value of member_points_error_log.update_time
     * @mbg.generated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime the value for member_points_error_log.update_time
     * @mbg.generated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * @return the value of member_points_error_log.is_deleted
     * @mbg.generated
     */
    public Integer getIsDeleted() {
        return isDeleted;
    }

    /**
     * @param isDeleted the value for member_points_error_log.is_deleted
     * @mbg.generated
     */
    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
}