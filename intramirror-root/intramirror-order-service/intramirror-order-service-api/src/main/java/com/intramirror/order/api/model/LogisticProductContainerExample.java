package com.intramirror.order.api.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LogisticProductContainerExample {
    /**
     * database table is logistic_product_container
     * @mbg.generated
     */
    protected String orderByClause;

    /**
     * database table is logistic_product_container
     * @mbg.generated
     */
    protected boolean distinct;

    /**
     * database table is logistic_product_container
     * @mbg.generated
     */
    protected List<Criteria> oredCriteria;

    private Integer limit;

    private Integer offset;

    /**
     * @mbg.generated
     */
    public LogisticProductContainerExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * @mbg.generated
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * @mbg.generated
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * @mbg.generated
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * @mbg.generated
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * @mbg.generated
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * @mbg.generated
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * @mbg.generated
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * @mbg.generated
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * @mbg.generated
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * @mbg.generated
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getOffset() {
        return offset;
    }

    /**
     * This class was generated by MyBatis Generator.
     * database table is logistic_product_container
     * @mbg.generated
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andContainerIdIsNull() {
            addCriterion("container_id is null");
            return (Criteria) this;
        }

        public Criteria andContainerIdIsNotNull() {
            addCriterion("container_id is not null");
            return (Criteria) this;
        }

        public Criteria andContainerIdEqualTo(Long value) {
            addCriterion("container_id =", value, "containerId");
            return (Criteria) this;
        }

        public Criteria andContainerIdNotEqualTo(Long value) {
            addCriterion("container_id <>", value, "containerId");
            return (Criteria) this;
        }

        public Criteria andContainerIdGreaterThan(Long value) {
            addCriterion("container_id >", value, "containerId");
            return (Criteria) this;
        }

        public Criteria andContainerIdGreaterThanOrEqualTo(Long value) {
            addCriterion("container_id >=", value, "containerId");
            return (Criteria) this;
        }

        public Criteria andContainerIdLessThan(Long value) {
            addCriterion("container_id <", value, "containerId");
            return (Criteria) this;
        }

        public Criteria andContainerIdLessThanOrEqualTo(Long value) {
            addCriterion("container_id <=", value, "containerId");
            return (Criteria) this;
        }

        public Criteria andContainerIdIn(List<Long> values) {
            addCriterion("container_id in", values, "containerId");
            return (Criteria) this;
        }

        public Criteria andContainerIdNotIn(List<Long> values) {
            addCriterion("container_id not in", values, "containerId");
            return (Criteria) this;
        }

        public Criteria andContainerIdBetween(Long value1, Long value2) {
            addCriterion("container_id between", value1, value2, "containerId");
            return (Criteria) this;
        }

        public Criteria andContainerIdNotBetween(Long value1, Long value2) {
            addCriterion("container_id not between", value1, value2, "containerId");
            return (Criteria) this;
        }

        public Criteria andShipmentIdIsNull() {
            addCriterion("shipment_id is null");
            return (Criteria) this;
        }

        public Criteria andShipmentIdIsNotNull() {
            addCriterion("shipment_id is not null");
            return (Criteria) this;
        }

        public Criteria andShipmentIdEqualTo(Long value) {
            addCriterion("shipment_id =", value, "shipmentId");
            return (Criteria) this;
        }

        public Criteria andShipmentIdNotEqualTo(Long value) {
            addCriterion("shipment_id <>", value, "shipmentId");
            return (Criteria) this;
        }

        public Criteria andShipmentIdGreaterThan(Long value) {
            addCriterion("shipment_id >", value, "shipmentId");
            return (Criteria) this;
        }

        public Criteria andShipmentIdGreaterThanOrEqualTo(Long value) {
            addCriterion("shipment_id >=", value, "shipmentId");
            return (Criteria) this;
        }

        public Criteria andShipmentIdLessThan(Long value) {
            addCriterion("shipment_id <", value, "shipmentId");
            return (Criteria) this;
        }

        public Criteria andShipmentIdLessThanOrEqualTo(Long value) {
            addCriterion("shipment_id <=", value, "shipmentId");
            return (Criteria) this;
        }

        public Criteria andShipmentIdIn(List<Long> values) {
            addCriterion("shipment_id in", values, "shipmentId");
            return (Criteria) this;
        }

        public Criteria andShipmentIdNotIn(List<Long> values) {
            addCriterion("shipment_id not in", values, "shipmentId");
            return (Criteria) this;
        }

        public Criteria andShipmentIdBetween(Long value1, Long value2) {
            addCriterion("shipment_id between", value1, value2, "shipmentId");
            return (Criteria) this;
        }

        public Criteria andShipmentIdNotBetween(Long value1, Long value2) {
            addCriterion("shipment_id not between", value1, value2, "shipmentId");
            return (Criteria) this;
        }

        public Criteria andLogisticsProductIdIsNull() {
            addCriterion("logistics_product_id is null");
            return (Criteria) this;
        }

        public Criteria andLogisticsProductIdIsNotNull() {
            addCriterion("logistics_product_id is not null");
            return (Criteria) this;
        }

        public Criteria andLogisticsProductIdEqualTo(Long value) {
            addCriterion("logistics_product_id =", value, "logisticsProductId");
            return (Criteria) this;
        }

        public Criteria andLogisticsProductIdNotEqualTo(Long value) {
            addCriterion("logistics_product_id <>", value, "logisticsProductId");
            return (Criteria) this;
        }

        public Criteria andLogisticsProductIdGreaterThan(Long value) {
            addCriterion("logistics_product_id >", value, "logisticsProductId");
            return (Criteria) this;
        }

        public Criteria andLogisticsProductIdGreaterThanOrEqualTo(Long value) {
            addCriterion("logistics_product_id >=", value, "logisticsProductId");
            return (Criteria) this;
        }

        public Criteria andLogisticsProductIdLessThan(Long value) {
            addCriterion("logistics_product_id <", value, "logisticsProductId");
            return (Criteria) this;
        }

        public Criteria andLogisticsProductIdLessThanOrEqualTo(Long value) {
            addCriterion("logistics_product_id <=", value, "logisticsProductId");
            return (Criteria) this;
        }

        public Criteria andLogisticsProductIdIn(List<Long> values) {
            addCriterion("logistics_product_id in", values, "logisticsProductId");
            return (Criteria) this;
        }

        public Criteria andLogisticsProductIdNotIn(List<Long> values) {
            addCriterion("logistics_product_id not in", values, "logisticsProductId");
            return (Criteria) this;
        }

        public Criteria andLogisticsProductIdBetween(Long value1, Long value2) {
            addCriterion("logistics_product_id between", value1, value2, "logisticsProductId");
            return (Criteria) this;
        }

        public Criteria andLogisticsProductIdNotBetween(Long value1, Long value2) {
            addCriterion("logistics_product_id not between", value1, value2, "logisticsProductId");
            return (Criteria) this;
        }

        public Criteria andOrderLineNumIsNull() {
            addCriterion("order_line_num is null");
            return (Criteria) this;
        }

        public Criteria andOrderLineNumIsNotNull() {
            addCriterion("order_line_num is not null");
            return (Criteria) this;
        }

        public Criteria andOrderLineNumEqualTo(String value) {
            addCriterion("order_line_num =", value, "orderLineNum");
            return (Criteria) this;
        }

        public Criteria andOrderLineNumNotEqualTo(String value) {
            addCriterion("order_line_num <>", value, "orderLineNum");
            return (Criteria) this;
        }

        public Criteria andOrderLineNumGreaterThan(String value) {
            addCriterion("order_line_num >", value, "orderLineNum");
            return (Criteria) this;
        }

        public Criteria andOrderLineNumGreaterThanOrEqualTo(String value) {
            addCriterion("order_line_num >=", value, "orderLineNum");
            return (Criteria) this;
        }

        public Criteria andOrderLineNumLessThan(String value) {
            addCriterion("order_line_num <", value, "orderLineNum");
            return (Criteria) this;
        }

        public Criteria andOrderLineNumLessThanOrEqualTo(String value) {
            addCriterion("order_line_num <=", value, "orderLineNum");
            return (Criteria) this;
        }

        public Criteria andOrderLineNumLike(String value) {
            addCriterion("order_line_num like", value, "orderLineNum");
            return (Criteria) this;
        }

        public Criteria andOrderLineNumNotLike(String value) {
            addCriterion("order_line_num not like", value, "orderLineNum");
            return (Criteria) this;
        }

        public Criteria andOrderLineNumIn(List<String> values) {
            addCriterion("order_line_num in", values, "orderLineNum");
            return (Criteria) this;
        }

        public Criteria andOrderLineNumNotIn(List<String> values) {
            addCriterion("order_line_num not in", values, "orderLineNum");
            return (Criteria) this;
        }

        public Criteria andOrderLineNumBetween(String value1, String value2) {
            addCriterion("order_line_num between", value1, value2, "orderLineNum");
            return (Criteria) this;
        }

        public Criteria andOrderLineNumNotBetween(String value1, String value2) {
            addCriterion("order_line_num not between", value1, value2, "orderLineNum");
            return (Criteria) this;
        }

        public Criteria andVendorIdIsNull() {
            addCriterion("vendor_id is null");
            return (Criteria) this;
        }

        public Criteria andVendorIdIsNotNull() {
            addCriterion("vendor_id is not null");
            return (Criteria) this;
        }

        public Criteria andVendorIdEqualTo(Long value) {
            addCriterion("vendor_id =", value, "vendorId");
            return (Criteria) this;
        }

        public Criteria andVendorIdNotEqualTo(Long value) {
            addCriterion("vendor_id <>", value, "vendorId");
            return (Criteria) this;
        }

        public Criteria andVendorIdGreaterThan(Long value) {
            addCriterion("vendor_id >", value, "vendorId");
            return (Criteria) this;
        }

        public Criteria andVendorIdGreaterThanOrEqualTo(Long value) {
            addCriterion("vendor_id >=", value, "vendorId");
            return (Criteria) this;
        }

        public Criteria andVendorIdLessThan(Long value) {
            addCriterion("vendor_id <", value, "vendorId");
            return (Criteria) this;
        }

        public Criteria andVendorIdLessThanOrEqualTo(Long value) {
            addCriterion("vendor_id <=", value, "vendorId");
            return (Criteria) this;
        }

        public Criteria andVendorIdIn(List<Long> values) {
            addCriterion("vendor_id in", values, "vendorId");
            return (Criteria) this;
        }

        public Criteria andVendorIdNotIn(List<Long> values) {
            addCriterion("vendor_id not in", values, "vendorId");
            return (Criteria) this;
        }

        public Criteria andVendorIdBetween(Long value1, Long value2) {
            addCriterion("vendor_id between", value1, value2, "vendorId");
            return (Criteria) this;
        }

        public Criteria andVendorIdNotBetween(Long value1, Long value2) {
            addCriterion("vendor_id not between", value1, value2, "vendorId");
            return (Criteria) this;
        }

        public Criteria andRecieveStatusIsNull() {
            addCriterion("recieve_status is null");
            return (Criteria) this;
        }

        public Criteria andRecieveStatusIsNotNull() {
            addCriterion("recieve_status is not null");
            return (Criteria) this;
        }

        public Criteria andRecieveStatusEqualTo(Integer value) {
            addCriterion("recieve_status =", value, "recieveStatus");
            return (Criteria) this;
        }

        public Criteria andRecieveStatusNotEqualTo(Integer value) {
            addCriterion("recieve_status <>", value, "recieveStatus");
            return (Criteria) this;
        }

        public Criteria andRecieveStatusGreaterThan(Integer value) {
            addCriterion("recieve_status >", value, "recieveStatus");
            return (Criteria) this;
        }

        public Criteria andRecieveStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("recieve_status >=", value, "recieveStatus");
            return (Criteria) this;
        }

        public Criteria andRecieveStatusLessThan(Integer value) {
            addCriterion("recieve_status <", value, "recieveStatus");
            return (Criteria) this;
        }

        public Criteria andRecieveStatusLessThanOrEqualTo(Integer value) {
            addCriterion("recieve_status <=", value, "recieveStatus");
            return (Criteria) this;
        }

        public Criteria andRecieveStatusIn(List<Integer> values) {
            addCriterion("recieve_status in", values, "recieveStatus");
            return (Criteria) this;
        }

        public Criteria andRecieveStatusNotIn(List<Integer> values) {
            addCriterion("recieve_status not in", values, "recieveStatus");
            return (Criteria) this;
        }

        public Criteria andRecieveStatusBetween(Integer value1, Integer value2) {
            addCriterion("recieve_status between", value1, value2, "recieveStatus");
            return (Criteria) this;
        }

        public Criteria andRecieveStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("recieve_status not between", value1, value2, "recieveStatus");
            return (Criteria) this;
        }

        public Criteria andVersionIsNull() {
            addCriterion("version is null");
            return (Criteria) this;
        }

        public Criteria andVersionIsNotNull() {
            addCriterion("version is not null");
            return (Criteria) this;
        }

        public Criteria andVersionEqualTo(Integer value) {
            addCriterion("version =", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionNotEqualTo(Integer value) {
            addCriterion("version <>", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionGreaterThan(Integer value) {
            addCriterion("version >", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionGreaterThanOrEqualTo(Integer value) {
            addCriterion("version >=", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionLessThan(Integer value) {
            addCriterion("version <", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionLessThanOrEqualTo(Integer value) {
            addCriterion("version <=", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionIn(List<Integer> values) {
            addCriterion("version in", values, "version");
            return (Criteria) this;
        }

        public Criteria andVersionNotIn(List<Integer> values) {
            addCriterion("version not in", values, "version");
            return (Criteria) this;
        }

        public Criteria andVersionBetween(Integer value1, Integer value2) {
            addCriterion("version between", value1, value2, "version");
            return (Criteria) this;
        }

        public Criteria andVersionNotBetween(Integer value1, Integer value2) {
            addCriterion("version not between", value1, value2, "version");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNull() {
            addCriterion("update_time is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNotNull() {
            addCriterion("update_time is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeEqualTo(Date value) {
            addCriterion("update_time =", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotEqualTo(Date value) {
            addCriterion("update_time <>", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThan(Date value) {
            addCriterion("update_time >", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("update_time >=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThan(Date value) {
            addCriterion("update_time <", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("update_time <=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIn(List<Date> values) {
            addCriterion("update_time in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotIn(List<Date> values) {
            addCriterion("update_time not in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("update_time between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("update_time not between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andIsDeletedIsNull() {
            addCriterion("is_deleted is null");
            return (Criteria) this;
        }

        public Criteria andIsDeletedIsNotNull() {
            addCriterion("is_deleted is not null");
            return (Criteria) this;
        }

        public Criteria andIsDeletedEqualTo(Integer value) {
            addCriterion("is_deleted =", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedNotEqualTo(Integer value) {
            addCriterion("is_deleted <>", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedGreaterThan(Integer value) {
            addCriterion("is_deleted >", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedGreaterThanOrEqualTo(Integer value) {
            addCriterion("is_deleted >=", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedLessThan(Integer value) {
            addCriterion("is_deleted <", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedLessThanOrEqualTo(Integer value) {
            addCriterion("is_deleted <=", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedIn(List<Integer> values) {
            addCriterion("is_deleted in", values, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedNotIn(List<Integer> values) {
            addCriterion("is_deleted not in", values, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedBetween(Integer value1, Integer value2) {
            addCriterion("is_deleted between", value1, value2, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedNotBetween(Integer value1, Integer value2) {
            addCriterion("is_deleted not between", value1, value2, "isDeleted");
            return (Criteria) this;
        }
    }

    /**
     * database table is logistic_product_container
     * @mbg.generated do_not_delete_during_merge
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * database table is logistic_product_container
     * @mbg.generated
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}