package com.intramirror.order.api.model;

import java.util.Date;

public class SubShipment {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sub_shipment.sub_shipment_id
     *
     * @mbggenerated
     */
    private Long subShipmentId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sub_shipment.shipping_segment_id
     *
     * @mbggenerated
     */
    private Long shippingSegmentId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sub_shipment.segment_sequence
     *
     * @mbggenerated
     */
    private Long segmentSequence;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sub_shipment.awb_num
     *
     * @mbggenerated
     */
    
    private String awbNum;
    /**
	 * @Fields 收货人
	 */
	private String consignee;
	/**
	 * @Fields 地址
	 */
	private String shipToAddr;
	/**
	 * @Fields 区
	 */
	private String shipToDistrict;
	/**
	 * @Fields 城市
	 */
	private String shipToCity;
	/**
	 * @Fields 省份
	 */
	private String shipToProvince;
	/**
	 * @Fields 国家
	 */
	private String shipToCountry;
	
	/**
	 * @Fields shipment ID
	 */
	private Long shipmentId;

    public String getConsignee() {
		return consignee;
	}

	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}

	public String getShipToAddr() {
		return shipToAddr;
	}

	public void setShipToAddr(String shipToAddr) {
		this.shipToAddr = shipToAddr;
	}

	public String getShipToDistrict() {
		return shipToDistrict;
	}

	public void setShipToDistrict(String shipToDistrict) {
		this.shipToDistrict = shipToDistrict;
	}

	public String getShipToCity() {
		return shipToCity;
	}

	public void setShipToCity(String shipToCity) {
		this.shipToCity = shipToCity;
	}

	public String getShipToProvince() {
		return shipToProvince;
	}

	public void setShipToProvince(String shipToProvince) {
		this.shipToProvince = shipToProvince;
	}

	public String getShipToCountry() {
		return shipToCountry;
	}

	public void setShipToCountry(String shipToCountry) {
		this.shipToCountry = shipToCountry;
	}

	/**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sub_shipment.status
     *
     * @mbggenerated
     */
    private Integer status;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sub_shipment.created_at
     *
     * @mbggenerated
     */
    private Date createdAt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sub_shipment.updated_at
     *
     * @mbggenerated
     */
    private Date updatedAt;
    
    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sub_shipment.sub_shipment_id
     *
     * @return the value of sub_shipment.sub_shipment_id
     *
     * @mbggenerated
     */
    public Long getSubShipmentId() {
        return subShipmentId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sub_shipment.sub_shipment_id
     *
     * @param subShipmentId the value for sub_shipment.sub_shipment_id
     *
     * @mbggenerated
     */
    public void setSubShipmentId(Long subShipmentId) {
        this.subShipmentId = subShipmentId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sub_shipment.shipping_segment_id
     *
     * @return the value of sub_shipment.shipping_segment_id
     *
     * @mbggenerated
     */
    public Long getShippingSegmentId() {
        return shippingSegmentId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sub_shipment.shipping_segment_id
     *
     * @param shippingSegmentId the value for sub_shipment.shipping_segment_id
     *
     * @mbggenerated
     */
    public void setShippingSegmentId(Long shippingSegmentId) {
        this.shippingSegmentId = shippingSegmentId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sub_shipment.segment_sequence
     *
     * @return the value of sub_shipment.segment_sequence
     *
     * @mbggenerated
     */
    public Long getSegmentSequence() {
        return segmentSequence;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sub_shipment.segment_sequence
     *
     * @param segmentSequence the value for sub_shipment.segment_sequence
     *
     * @mbggenerated
     */
    public void setSegmentSequence(Long segmentSequence) {
        this.segmentSequence = segmentSequence;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sub_shipment.awb_num
     *
     * @return the value of sub_shipment.awb_num
     *
     * @mbggenerated
     */
    public String getAwbNum() {
        return awbNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sub_shipment.awb_num
     *
     * @param awbNum the value for sub_shipment.awb_num
     *
     * @mbggenerated
     */
    public void setAwbNum(String awbNum) {
        this.awbNum = awbNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sub_shipment.status
     *
     * @return the value of sub_shipment.status
     *
     * @mbggenerated
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sub_shipment.status
     *
     * @param status the value for sub_shipment.status
     *
     * @mbggenerated
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sub_shipment.created_at
     *
     * @return the value of sub_shipment.created_at
     *
     * @mbggenerated
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sub_shipment.created_at
     *
     * @param createdAt the value for sub_shipment.created_at
     *
     * @mbggenerated
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sub_shipment.updated_at
     *
     * @return the value of sub_shipment.updated_at
     *
     * @mbggenerated
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sub_shipment.updated_at
     *
     * @param updatedAt the value for sub_shipment.updated_at
     *
     * @mbggenerated
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

	public Long getShipmentId() {
		return shipmentId;
	}

	public void setShipmentId(Long shipmentId) {
		this.shipmentId = shipmentId;
	}
}