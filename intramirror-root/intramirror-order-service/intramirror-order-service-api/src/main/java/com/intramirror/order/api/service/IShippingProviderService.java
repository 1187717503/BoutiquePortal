/**
 *
 */
package com.intramirror.order.api.service;

import com.intramirror.order.api.model.ShippingProvider;

/**
 * @author yuan
 */
public interface IShippingProviderService {

    ShippingProvider getShippingProviderByShipmentId(Long shipmentId);

    ShippingProvider getShippingProviderByName(String zsy);
}
