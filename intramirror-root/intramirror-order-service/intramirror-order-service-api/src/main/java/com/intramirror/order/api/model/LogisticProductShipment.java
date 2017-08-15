/**
 * 
 */
package com.intramirror.order.api.model;

/**
 * @author Mr.Y
 *
 */
public class LogisticProductShipment {

	private Long logisticProductShipmentId;
	
	private Long logisticProductId;
	
	private Long subShipmentId;

	public Long getLogisticProductShipmentId() {
		return logisticProductShipmentId;
	}

	public void setLogisticProductShipmentId(Long logisticProductShipmentId) {
		this.logisticProductShipmentId = logisticProductShipmentId;
	}

	public Long getSubShipmentId() {
		return subShipmentId;
	}

	public void setSubShipmentId(Long subShipmentId) {
		this.subShipmentId = subShipmentId;
	}

	public Long getLogisticProductId() {
		return logisticProductId;
	}

	public void setLogisticProductId(Long logisticProductId) {
		this.logisticProductId = logisticProductId;
	}
	
	
}
